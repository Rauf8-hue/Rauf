package com.example.ai

enum class RequestType(
    val id: String,
    val label: String,
    val badge: String,
    val icon: String,
    val description: String
) {
    AUTO("AUTO", "AUTO", "⚡ Auto Detect", "⚡", "Automatically detect request intent & language"),
    GENERAL("GENERAL", "GENERAL", "💬 General", "💬", "General questions, math & conceptual answers"),
    CODE("CODE", "CODE", "</> Write Code", "</>", "Generate clean working code with run commands"),
    PROMPT("PROMPT", "PROMPT", "✦ Prompt Craft", "✦", "Generate master copy-paste prompt specification"),
    EXPLAIN("EXPLAIN", "EXPLAIN", "🧠 Explain", "🧠", "Break down code and concepts step-by-step"),
    DEBUG("DEBUG", "DEBUG", "🐞 Fix Bug", "🐞", "Diagnose error, root cause, fix and prevention"),
    PROJECT("PROJECT", "PROJECT", "🏗 Build Project", "🏗", "Scaffold full project architecture and files"),
    LEARN("LEARN", "LEARN", "📚 Learn", "📚", "Tutorials, lessons, roadmaps and drills"),
    REVIEW("REVIEW", "REVIEW", "🔍 Review", "🔍", "Quality, security, performance & clean code audit"),
    OPTIMIZE("OPTIMIZE", "OPTIMIZE", "⚙ Optimize", "⚙", "Refactor for speed, memory & maintainability"),
    CONVERT("CONVERT", "CONVERT", "🔄 Convert", "🔄", "Translate code between languages and frameworks"),
    TESTS("TESTS", "TESTS", "🧪 Tests", "🧪", "Unit, integration and edge case tests"),
    GAME_DEV("GAME_DEV", "GAME DEV", "🎮 Game Dev", "🎮", "Game mechanics, 3D physics & controllers"),
    DATABASE("DATABASE", "DATABASE", "🗄️ Database", "🗄️", "SQL/NoSQL schema, queries & indexing"),
    GIT("GIT", "GIT", "🌿 Git Helper", "🌿", "Git commands, workflow & merge resolution"),
    TERMINAL("TERMINAL", "TERMINAL", "💻 Terminal", "💻", "Shell commands, scripts & safety analysis"),
    API("API", "API", "🔌 API Design", "🔌", "REST, CRUD, authentication & schemas")
}

data class IntentAnalysis(
    val requestType: RequestType,
    val detectedLanguage: String? = null,
    val isGeneralQuestion: Boolean = false,
    val isSimpleRequest: Boolean = false,
    val isMultiFileProjectRequested: Boolean = false,
    val isPromptRequested: Boolean = false,
    val isCodeReviewRequested: Boolean = false,
    val isDebugRequested: Boolean = false,
    val displayBadge: String = "GENERAL"
) {
    fun getFormattedModeIndicator(): String {
        return "AI AGENT [ $displayBadge ]"
    }
}

data class AIRequest(
    val prompt: String,
    val requestType: RequestType = RequestType.AUTO,
    val experienceLevel: String = "Beginner", // "Beginner", "Intermediate", "Advanced"
    val projectContext: String? = null,
    val currentFileContent: String? = null,
    val currentFileName: String? = null,
    val attachedImageBase64: String? = null,
    val chatHistory: List<Pair<String, String>> = emptyList(), // List of (role, content)
    val intentAnalysis: IntentAnalysis? = null
)

data class AIResponseChunk(
    val text: String = "",
    val detectedType: RequestType = RequestType.CODE,
    val intentAnalysis: IntentAnalysis? = null,
    val codeBlocks: List<ExtractedCodeBlock> = emptyList(),
    val promptSpec: String? = null,
    val isComplete: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val providerUsed: String = "gemini",
    val modelUsed: String = "gemini-3.5-flash"
)

data class ExtractedCodeBlock(
    val language: String,
    val code: String,
    val filename: String? = null
)

data class ConnectionTestResult(
    val isSuccess: Boolean,
    val message: String,
    val latencyMs: Long = 0,
    val provider: String = ""
)

