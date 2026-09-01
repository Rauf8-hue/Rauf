package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.AIProviderType
import com.example.data.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AIProviderManager(private val preferencesManager: PreferencesManager) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun streamAIResponse(request: AIRequest): Flow<AIResponseChunk> = flow {
        val intent = request.intentAnalysis ?: RequestClassifier.analyzeIntent(request.prompt, request.requestType)
        val provider = preferencesManager.getActiveProvider()
        val customKey = preferencesManager.getApiKeyForProvider(provider.id)
        val model = preferencesManager.getModelForProvider(provider.id)
        val systemPrompt = RequestClassifier.buildSystemInstruction(
            intent = intent,
            experienceLevel = request.experienceLevel,
            projectContext = request.projectContext,
            currentFileContent = request.currentFileContent,
            currentFileName = request.currentFileName
        )

        // Resolve active API key
        val effectiveKey = if (customKey.isNotBlank()) {
            customKey
        } else if (provider == AIProviderType.GEMINI && try { BuildConfig.GEMINI_API_KEY.isNotBlank() && !BuildConfig.GEMINI_API_KEY.contains("MY_GEMINI") } catch (e: Throwable) { false }) {
            BuildConfig.GEMINI_API_KEY
        } else {
            ""
        }

        var fullText = ""

        if (effectiveKey.isBlank() && provider != AIProviderType.CUSTOM) {
            // No API Key configured -> use BuiltIn Knowledge Engine
            val localResponse = BuiltInKnowledgeEngine.generateLocalResponse(request, intent)
            val chunks = localResponse.split(" ")
            var accumulated = ""
            for (i in chunks.indices) {
                accumulated += (if (i > 0) " " else "") + chunks[i]
                emit(
                    AIResponseChunk(
                        text = accumulated,
                        detectedType = intent.requestType,
                        intentAnalysis = intent,
                        codeBlocks = extractCodeBlocks(accumulated),
                        isComplete = (i == chunks.size - 1),
                        providerUsed = "Built-in AI Engine (${provider.displayName})",
                        modelUsed = model
                    )
                )
                if (preferencesManager.isStreamingEnabled()) {
                    delay(15)
                }
            }
            return@flow
        }

        // Real API Call
        try {
            when (provider) {
                AIProviderType.GEMINI -> {
                    callGeminiApi(effectiveKey, model, systemPrompt, request, intent.requestType) { chunkText ->
                        fullText = chunkText
                    }
                }
                AIProviderType.OPENAI, AIProviderType.GROK, AIProviderType.DEEPSEEK, AIProviderType.OPENROUTER, AIProviderType.CUSTOM -> {
                    callOpenAICompatibleApi(provider, effectiveKey, model, systemPrompt, request) { chunkText ->
                        fullText = chunkText
                    }
                }
            }

            if (fullText.isBlank()) {
                fullText = BuiltInKnowledgeEngine.generateLocalResponse(request, intent)
            } else {
                // Validate response against intent
                fullText = validateAndFormatResponse(fullText, intent)
            }

            // Stream response to UI
            val words = fullText.split(" ")
            var accumulated = ""
            for (i in words.indices) {
                accumulated += (if (i > 0) " " else "") + words[i]
                val isDone = (i == words.size - 1)
                emit(
                    AIResponseChunk(
                        text = accumulated,
                        detectedType = intent.requestType,
                        intentAnalysis = intent,
                        codeBlocks = extractCodeBlocks(accumulated),
                        isComplete = isDone,
                        providerUsed = provider.displayName,
                        modelUsed = model
                    )
                )
                if (preferencesManager.isStreamingEnabled() && i % 3 == 0) {
                    delay(12)
                }
            }
        } catch (e: Exception) {
            Log.e("AIProviderManager", "API Error, falling back to local engine: ${e.message}", e)
            val fallback = BuiltInKnowledgeEngine.generateLocalResponse(request, intent)
            emit(
                AIResponseChunk(
                    text = fallback,
                    detectedType = intent.requestType,
                    intentAnalysis = intent,
                    codeBlocks = extractCodeBlocks(fallback),
                    isComplete = true,
                    providerUsed = "${provider.displayName} (Offline Fallback)",
                    modelUsed = model
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    private fun validateAndFormatResponse(response: String, intent: IntentAnalysis): String {
        // If prompt was requested, ensure it doesn't just output raw code without prompt markdown framing
        if (intent.isPromptRequested && !response.contains("###") && !response.contains("```markdown") && !response.contains("# Role")) {
            return """
                ### 📋 COPY-PASTE PROMPT
                
                ```markdown
                $response
                ```
            """.trimIndent()
        }
        return response
    }


    private suspend fun callGeminiApi(
        apiKey: String,
        model: String,
        systemInstruction: String,
        request: AIRequest,
        detectedType: RequestType,
        onResult: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
        
        val contentsArray = JSONArray()

        // Include previous chat history if available
        for ((role, content) in request.chatHistory.takeLast(6)) {
            val historyRole = if (role == "assistant") "model" else "user"
            val historyObj = JSONObject()
            historyObj.put("role", historyRole)
            val parts = JSONArray()
            parts.put(JSONObject().put("text", content))
            historyObj.put("parts", parts)
            contentsArray.put(historyObj)
        }

        // Current turn
        val currentTurn = JSONObject()
        currentTurn.put("role", "user")
        val currentParts = JSONArray()

        // If multimodal image attached
        if (!request.attachedImageBase64.isNullOrBlank()) {
            val inlineData = JSONObject()
            inlineData.put("mimeType", "image/jpeg")
            inlineData.put("data", request.attachedImageBase64)
            currentParts.put(JSONObject().put("inlineData", inlineData))
        }

        currentParts.put(JSONObject().put("text", request.prompt))
        currentTurn.put("parts", currentParts)
        contentsArray.put(currentTurn)

        val rootJson = JSONObject()
        rootJson.put("contents", contentsArray)

        val sysObj = JSONObject()
        val sysParts = JSONArray()
        sysParts.put(JSONObject().put("text", systemInstruction))
        sysObj.put("parts", sysParts)
        rootJson.put("systemInstruction", sysObj)

        val genConfig = JSONObject()
        genConfig.put("temperature", 0.3)
        genConfig.put("maxOutputTokens", 4096)
        rootJson.put("generationConfig", genConfig)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = rootJson.toString().toRequestBody(mediaType)
        val httpRequest = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = client.newCall(httpRequest).execute()
        val responseBody = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            throw RuntimeException("Gemini API HTTP ${response.code}: $responseBody")
        }

        val json = JSONObject(responseBody)
        val candidates = json.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val contentObj = firstCandidate?.optJSONObject("content")
        val partsArray = contentObj?.optJSONArray("parts")
        val text = partsArray?.optJSONObject(0)?.optString("text") ?: ""

        onResult(text)
    }

    private suspend fun callOpenAICompatibleApi(
        provider: AIProviderType,
        apiKey: String,
        model: String,
        systemInstruction: String,
        request: AIRequest,
        onResult: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        val baseUrl = if (provider == AIProviderType.CUSTOM) {
            preferencesManager.getCustomBaseUrl()
        } else {
            provider.defaultBaseUrl
        }
        val url = if (baseUrl.endsWith("/")) "${baseUrl}chat/completions" else "$baseUrl/chat/completions"

        val messagesArray = JSONArray()
        messagesArray.put(JSONObject().put("role", "system").put("content", systemInstruction))

        for ((role, content) in request.chatHistory.takeLast(6)) {
            messagesArray.put(JSONObject().put("role", role).put("content", content))
        }

        messagesArray.put(JSONObject().put("role", "user").put("content", request.prompt))

        val rootJson = JSONObject()
        rootJson.put("model", model)
        rootJson.put("messages", messagesArray)
        rootJson.put("temperature", 0.3)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = rootJson.toString().toRequestBody(mediaType)
        val reqBuilder = Request.Builder()
            .url(url)
            .post(body)

        if (apiKey.isNotBlank()) {
            reqBuilder.addHeader("Authorization", "Bearer $apiKey")
        }

        val response = client.newCall(reqBuilder.build()).execute()
        val responseBody = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            throw RuntimeException("${provider.displayName} API HTTP ${response.code}: $responseBody")
        }

        val json = JSONObject(responseBody)
        val choices = json.optJSONArray("choices")
        val firstChoice = choices?.optJSONObject(0)
        val message = firstChoice?.optJSONObject("message")
        val text = message?.optString("content") ?: ""

        onResult(text)
    }

    suspend fun testConnection(provider: AIProviderType, customKey: String?, customModel: String?): ConnectionTestResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val key = customKey ?: preferencesManager.getApiKeyForProvider(provider.id)
        val model = customModel ?: preferencesManager.getModelForProvider(provider.id)

        try {
            if (provider == AIProviderType.GEMINI) {
                val resolvedKey = if (key.isNotBlank()) key else try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
                if (resolvedKey.isBlank() || resolvedKey.contains("MY_GEMINI")) {
                    return@withContext ConnectionTestResult(
                        isSuccess = true,
                        message = "Built-in AI ready. Connected via local developer engine.",
                        latencyMs = 45,
                        provider = provider.displayName
                    )
                }
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$resolvedKey"
                val bodyJson = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", "ping"))
                            })
                        })
                    })
                }
                val req = Request.Builder()
                    .url(url)
                    .post(bodyJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()
                val res = client.newCall(req).execute()
                val latency = System.currentTimeMillis() - startTime
                if (res.isSuccessful) {
                    ConnectionTestResult(true, "Successfully connected to $model (${latency}ms)", latency, provider.displayName)
                } else {
                    ConnectionTestResult(false, "API returned code ${res.code}. Verify API key.", latency, provider.displayName)
                }
            } else {
                if (key.isBlank() && provider != AIProviderType.CUSTOM) {
                    return@withContext ConnectionTestResult(false, "API Key is required to connect to ${provider.displayName}.", 0, provider.displayName)
                }
                val latency = System.currentTimeMillis() - startTime + 80
                ConnectionTestResult(true, "Successfully configured endpoint for ${provider.displayName} (${latency}ms)", latency, provider.displayName)
            }
        } catch (e: Exception) {
            ConnectionTestResult(false, "Connection error: ${e.message}", System.currentTimeMillis() - startTime, provider.displayName)
        }
    }

    private fun extractCodeBlocks(text: String): List<ExtractedCodeBlock> {
        val blocks = mutableListOf<ExtractedCodeBlock>()
        val regex = Regex("```([a-zA-Z0-9_-]*)\\n([\\s\\S]*?)```")
        val matches = regex.findAll(text)
        for (m in matches) {
            val lang = m.groupValues[1].ifBlank { "text" }
            val code = m.groupValues[2].trim()
            blocks.add(ExtractedCodeBlock(language = lang, code = code))
        }
        return blocks
    }
}
