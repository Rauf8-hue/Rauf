package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppThemePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DeveloperExperienceLevel(val title: String, val description: String) {
    BEGINNER("Beginner", "Step-by-step guidance, jargon explanations, exact terminal instructions & simplified examples."),
    INTERMEDIATE("Intermediate", "Multi-file architecture, performance tips, API patterns & streamlined explanations."),
    ADVANCED("Advanced", "High-scale architecture, security analysis, CI/CD, deep refactoring & minimal boilerplate.")
}

enum class AIProviderType(
    val id: String,
    val displayName: String,
    val defaultModel: String,
    val supportedModels: List<String>,
    val defaultBaseUrl: String,
    val keyPortalUrl: String
) {
    GEMINI(
        id = "gemini",
        displayName = "Google Gemini",
        defaultModel = "gemini-3.5-flash",
        supportedModels = listOf("gemini-3.5-flash", "gemini-3.1-pro-preview", "gemini-3.1-flash-lite-preview"),
        defaultBaseUrl = "https://generativelanguage.googleapis.com/",
        keyPortalUrl = "https://aistudio.google.com/app/apikey"
    ),
    OPENAI(
        id = "openai",
        displayName = "OpenAI",
        defaultModel = "gpt-4o",
        supportedModels = listOf("gpt-4o", "gpt-4o-mini", "o3-mini", "gpt-4-turbo"),
        defaultBaseUrl = "https://api.openai.com/v1/",
        keyPortalUrl = "https://platform.openai.com/api-keys"
    ),
    GROK(
        id = "grok",
        displayName = "xAI Grok",
        defaultModel = "grok-2-latest",
        supportedModels = listOf("grok-2-latest", "grok-beta", "grok-vision-beta"),
        defaultBaseUrl = "https://api.x.ai/v1/",
        keyPortalUrl = "https://console.x.ai/"
    ),
    DEEPSEEK(
        id = "deepseek",
        displayName = "DeepSeek",
        defaultModel = "deepseek-coder",
        supportedModels = listOf("deepseek-coder", "deepseek-chat", "deepseek-reasoner"),
        defaultBaseUrl = "https://api.deepseek.com/v1/",
        keyPortalUrl = "https://platform.deepseek.com/api_keys"
    ),
    OPENROUTER(
        id = "openrouter",
        displayName = "OpenRouter",
        defaultModel = "anthropic/claude-3.5-sonnet",
        supportedModels = listOf("anthropic/claude-3.5-sonnet", "meta-llama/llama-3.3-70b-instruct", "google/gemini-2.0-flash-001"),
        defaultBaseUrl = "https://openrouter.ai/api/v1/",
        keyPortalUrl = "https://openrouter.ai/keys"
    ),
    CUSTOM(
        id = "custom",
        displayName = "Custom Endpoint",
        defaultModel = "default-model",
        supportedModels = listOf("default-model", "custom-coder-v1"),
        defaultBaseUrl = "http://localhost:8000/v1/",
        keyPortalUrl = "https://github.com"
    )
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ai_agent_settings", Context.MODE_PRIVATE)

    private val _experienceLevel = MutableStateFlow(getExperienceLevel())
    val experienceLevel: StateFlow<DeveloperExperienceLevel> = _experienceLevel.asStateFlow()

    private val _activeProvider = MutableStateFlow(getActiveProvider())
    val activeProvider: StateFlow<AIProviderType> = _activeProvider.asStateFlow()

    private val _themePreset = MutableStateFlow(getThemePreset())
    val themePreset: StateFlow<AppThemePreset> = _themePreset.asStateFlow()

    private val _streamingEnabled = MutableStateFlow(isStreamingEnabled())
    val streamingEnabled: StateFlow<Boolean> = _streamingEnabled.asStateFlow()

    private val _codeFontSize = MutableStateFlow(getCodeFontSize())
    val codeFontSize: StateFlow<Int> = _codeFontSize.asStateFlow()

    fun isFirstLaunchCompleted(): Boolean {
        return prefs.getBoolean("first_launch_completed", false)
    }

    fun setFirstLaunchCompleted(completed: Boolean) {
        prefs.edit().putBoolean("first_launch_completed", completed).apply()
    }

    fun getExperienceLevel(): DeveloperExperienceLevel {
        val name = prefs.getString("experience_level", DeveloperExperienceLevel.BEGINNER.name)
        return try {
            DeveloperExperienceLevel.valueOf(name ?: DeveloperExperienceLevel.BEGINNER.name)
        } catch (e: Exception) {
            DeveloperExperienceLevel.BEGINNER
        }
    }

    fun setExperienceLevel(level: DeveloperExperienceLevel) {
        prefs.edit().putString("experience_level", level.name).apply()
        _experienceLevel.value = level
    }

    fun getActiveProvider(): AIProviderType {
        val id = prefs.getString("active_provider", AIProviderType.GEMINI.id)
        return AIProviderType.values().firstOrNull { it.id == id } ?: AIProviderType.GEMINI
    }

    fun setActiveProvider(provider: AIProviderType) {
        prefs.edit().putString("active_provider", provider.id).apply()
        _activeProvider.value = provider
    }

    fun getApiKeyForProvider(providerId: String): String {
        return prefs.getString("api_key_$providerId", "") ?: ""
    }

    fun setApiKeyForProvider(providerId: String, key: String) {
        prefs.edit().putString("api_key_$providerId", key.trim()).apply()
    }

    fun deleteApiKeyForProvider(providerId: String) {
        prefs.edit().remove("api_key_$providerId").apply()
    }

    fun getModelForProvider(providerId: String): String {
        val defaultModel = AIProviderType.values().firstOrNull { it.id == providerId }?.defaultModel ?: "gemini-3.5-flash"
        return prefs.getString("model_$providerId", defaultModel) ?: defaultModel
    }

    fun setModelForProvider(providerId: String, model: String) {
        prefs.edit().putString("model_$providerId", model).apply()
    }

    fun getCustomBaseUrl(): String {
        return prefs.getString("custom_base_url", "http://localhost:8000/v1/") ?: "http://localhost:8000/v1/"
    }

    fun setCustomBaseUrl(url: String) {
        prefs.edit().putString("custom_base_url", url.trim()).apply()
    }

    fun getThemePreset(): AppThemePreset {
        val name = prefs.getString("theme_preset", AppThemePreset.QUANTUM_GREEN.name)
        return try {
            AppThemePreset.valueOf(name ?: AppThemePreset.QUANTUM_GREEN.name)
        } catch (e: Exception) {
            AppThemePreset.QUANTUM_GREEN
        }
    }

    fun setThemePreset(preset: AppThemePreset) {
        prefs.edit().putString("theme_preset", preset.name).apply()
        _themePreset.value = preset
    }

    fun isStreamingEnabled(): Boolean {
        return prefs.getBoolean("streaming_enabled", true)
    }

    fun setStreamingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("streaming_enabled", enabled).apply()
        _streamingEnabled.value = enabled
    }

    fun getCodeFontSize(): Int {
        return prefs.getInt("code_font_size", 12)
    }

    fun setCodeFontSize(size: Int) {
        prefs.edit().putInt("code_font_size", size).apply()
        _codeFontSize.value = size
    }

    fun isAutoCopyEnabled(): Boolean {
        return prefs.getBoolean("auto_copy_enabled", false)
    }

    fun setAutoCopyEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("auto_copy_enabled", enabled).apply()
    }

    companion object {
        fun maskApiKey(key: String): String {
            if (key.isBlank()) return "Not configured"
            if (key.length <= 8) return "••••••••"
            val prefix = key.take(3)
            val suffix = key.takeLast(4)
            return "$prefix-••••••••••••$suffix"
        }
    }
}
