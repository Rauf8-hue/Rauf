package com.example.ai

object RequestClassifier {

    fun analyzeIntent(input: String, selectedMode: RequestType): IntentAnalysis {
        val trimmed = input.trim()
        val text = trimmed.lowercase()

        // 1. Language / Technology Detection
        val detectedLanguage = detectLanguage(text)

        // 2. Check for PROMPT Generation Request
        val isPromptRequested = text.startsWith("give me a prompt") ||
                text.startsWith("create a prompt") ||
                text.startsWith("write a prompt") ||
                text.startsWith("generate a prompt") ||
                text.startsWith("provide a prompt") ||
                text.startsWith("prompt for") ||
                text.startsWith("prompt to") ||
                text.contains("system prompt") ||
                text.contains("ai studio prompt") ||
                text.contains("master prompt") ||
                text.contains("prompt specification")

        // 3. Check for Full Multi-File Project Request
        val isMultiFileProjectRequested = text.startsWith("build me a complete project") ||
                text.startsWith("build a complete project") ||
                text.startsWith("create a complete project") ||
                text.startsWith("build me a full project") ||
                text.startsWith("create the full application") ||
                text.startsWith("build this as a production application") ||
                text.startsWith("give me all files") ||
                text.startsWith("create the project structure") ||
                text.startsWith("build a full stack") ||
                text.contains("complete multi-file project") ||
                text.contains("scaffold full project")

        // 4. Check for Code Review Request
        val isCodeReviewRequested = (text.startsWith("review") ||
                text.contains("code review") ||
                text.contains("audit this code") ||
                text.contains("check my code") ||
                text.contains("find vulnerabilities in this code") ||
                text.contains("analyze code quality")) &&
                !isPromptRequested

        // 5. Check for Debugging Request
        val isDebugRequested = (text.startsWith("why am i getting this error") ||
                text.startsWith("fix this error") ||
                text.startsWith("fix this bug") ||
                text.startsWith("debug this") ||
                text.startsWith("fix my code") ||
                text.startsWith("debug:") ||
                text.contains("syntaxerror") ||
                text.contains("nullpointerexception") ||
                text.contains("typeerror:") ||
                text.contains("failed to compile") ||
                text.contains("traceback (most recent call last)")) &&
                !isPromptRequested

        // 6. Check for Simple Math or General Question
        val isMath = isMathExpression(text)
        val isGeneralQuestion = isMath || isGeneralKnowledgeQuestion(text)

        // Determine Effective Request Type
        val requestType: RequestType = when {
            selectedMode != RequestType.AUTO -> selectedMode
            isPromptRequested -> RequestType.PROMPT
            isCodeReviewRequested -> RequestType.REVIEW
            isDebugRequested -> RequestType.DEBUG
            isMultiFileProjectRequested -> RequestType.PROJECT
            isGeneralQuestion -> RequestType.GENERAL
            text.startsWith("explain") || text.startsWith("what does this code do") || text.startsWith("how does this work") -> RequestType.EXPLAIN
            text.startsWith("optimize") || text.startsWith("refactor") || text.contains("make it faster") -> RequestType.OPTIMIZE
            text.contains("convert to") || text.contains("translate to") || text.contains("rewrite in") -> RequestType.CONVERT
            text.startsWith("test") || text.contains("write unit tests") || text.contains("generate tests") -> RequestType.TESTS
            text.contains("game") || text.contains("unity") || text.contains("three.js") || text.contains("godot") -> RequestType.GAME_DEV
            text.startsWith("git ") || text.contains("merge conflict") || text.contains("git rebase") || text.contains("git commit") -> RequestType.GIT
            text.startsWith("terminal") || text.startsWith("command to") || text.contains("shell command") || text.contains("bash command") -> RequestType.TERMINAL
            text.contains("sql query") || text.contains("database schema") || text.contains("mongodb") || text.contains("postgresql") -> RequestType.DATABASE
            text.contains("rest api") || text.contains("fastapi endpoint") || text.contains("crud endpoints") -> RequestType.API
            text.startsWith("learn") || text.startsWith("teach me") || text.startsWith("roadmap for") -> RequestType.LEARN
            text.startsWith("write ") || text.startsWith("create ") || text.startsWith("provide ") || text.startsWith("code ") || text.contains("calculator") || detectedLanguage != null -> RequestType.CODE
            else -> RequestType.GENERAL
        }

        // Build Display Badge formatted strictly as requested (e.g. GENERAL, CODE • PYTHON, CODE • BASH, DEBUG, PROMPT, REVIEW)
        val displayBadge = buildDisplayBadge(requestType, detectedLanguage)

        return IntentAnalysis(
            requestType = requestType,
            detectedLanguage = detectedLanguage,
            isGeneralQuestion = (requestType == RequestType.GENERAL),
            isSimpleRequest = isMath || (requestType == RequestType.GENERAL) || (requestType == RequestType.CODE && !isMultiFileProjectRequested),
            isMultiFileProjectRequested = isMultiFileProjectRequested || (requestType == RequestType.PROJECT),
            isPromptRequested = isPromptRequested || (requestType == RequestType.PROMPT),
            isCodeReviewRequested = isCodeReviewRequested || (requestType == RequestType.REVIEW),
            isDebugRequested = isDebugRequested || (requestType == RequestType.DEBUG),
            displayBadge = displayBadge
        )
    }

    private fun detectLanguage(text: String): String? {
        return when {
            text.contains("python") || text.contains("py ") || text.endsWith(".py") || text.contains("fastapi") || text.contains("django") || text.contains("flask") -> "PYTHON"
            text.contains("bash") || text.contains("shell script") || text.contains("sh script") || text.contains("zsh") || text.contains("terminal script") || text.contains("shell") -> "BASH"
            text.contains("react") || text.contains("jsx") || text.contains("tsx") -> "REACT"
            text.contains("typescript") || text.contains("ts ") || text.endsWith(".ts") -> "TYPESCRIPT"
            text.contains("javascript") || text.contains("js ") || text.endsWith(".js") || text.contains("node") || text.contains("express") -> "JAVASCRIPT"
            text.contains("kotlin") || text.contains("compose") || text.endsWith(".kt") -> "KOTLIN"
            text.contains("java") && !text.contains("javascript") -> "JAVA"
            text.contains("c++") || text.contains("cpp") -> "C++"
            text.contains("rust") -> "RUST"
            text.contains("golang") || text.contains("go language") || (text.contains("go ") && text.contains("code")) -> "GO"
            text.contains("sql") || text.contains("sqlite") || text.contains("postgres") || text.contains("mysql") -> "SQL"
            text.contains("html") || text.contains("css") || text.contains("tailwind") -> "HTML/CSS"
            text.contains("swift") || text.contains("swiftui") -> "SWIFT"
            text.contains("php") -> "PHP"
            text.contains("ruby") -> "RUBY"
            text.contains("c#") || text.contains("csharp") -> "C#"
            else -> null
        }
    }

    private fun isMathExpression(text: String): Boolean {
        val clean = text.replace("what is", "").replace("what's", "").replace("calculate", "").replace("evaluate", "").replace("?", "").trim()
        val mathPattern = Regex("^([0-9]+(\\.[0-9]+)?\\s*([+\\-*/%^xX]|plus|minus|times|divided by)\\s*)+[0-9]+(\\.[0-9]+)?$")
        return mathPattern.matches(clean) || clean.matches(Regex("^[0-9\\s+\\-*/().^]+$")) && clean.any { it.isDigit() } && clean.any { "+-*/^%".contains(it) }
    }

    private fun isGeneralKnowledgeQuestion(text: String): Boolean {
        // Plain informational questions that do not ask for code generation
        val isExplicitCodeRequest = text.startsWith("write") || text.startsWith("create") || text.startsWith("build") || text.startsWith("code") || text.startsWith("give me a script") || text.startsWith("provide a script")
        if (isExplicitCodeRequest) return false

        return text.startsWith("what is ") ||
                text.startsWith("what's ") ||
                text.startsWith("who invented") ||
                text.startsWith("who is") ||
                text.startsWith("why is") ||
                text.startsWith("how does") ||
                text.startsWith("when was") ||
                text.startsWith("tell me about") ||
                text.contains("difference between") ||
                text.startsWith("explain ") && !text.contains("this code") && !text.contains("my code") ||
                text.matches(Regex("^[a-zA-Z\\s?]+$")) && (text.contains("ram") || text.contains("rom") || text.contains("cpu") || text.contains("internet") || text.contains("compiler") || text.contains("recursion"))
    }

    private fun buildDisplayBadge(requestType: RequestType, language: String?): String {
        return when (requestType) {
            RequestType.GENERAL -> "GENERAL"
            RequestType.CODE -> if (language != null) "CODE • $language" else "CODE"
            RequestType.PROMPT -> "PROMPT"
            RequestType.DEBUG -> "DEBUG"
            RequestType.REVIEW -> "REVIEW"
            RequestType.EXPLAIN -> if (language != null) "EXPLAIN • $language" else "EXPLAIN"
            RequestType.PROJECT -> if (language != null) "PROJECT • $language" else "PROJECT"
            RequestType.LEARN -> "LEARN"
            RequestType.OPTIMIZE -> "OPTIMIZE"
            RequestType.CONVERT -> "CONVERT"
            RequestType.TESTS -> "TESTS"
            RequestType.GAME_DEV -> "GAME DEV"
            RequestType.DATABASE -> "DATABASE"
            RequestType.GIT -> "GIT"
            RequestType.TERMINAL -> if (language != null) "TERMINAL • $language" else "TERMINAL"
            RequestType.API -> "API"
            RequestType.AUTO -> "GENERAL"
        }
    }

    fun detectRequestType(input: String, selectedMode: RequestType): RequestType {
        return analyzeIntent(input, selectedMode).requestType
    }

    fun buildSystemInstruction(
        intent: IntentAnalysis,
        experienceLevel: String,
        projectContext: String? = null,
        currentFileContent: String? = null,
        currentFileName: String? = null
    ): String {
        val levelGuide = when (experienceLevel.lowercase()) {
            "beginner" -> """
                - USER EXPERIENCE LEVEL: BEGINNER.
                - Use friendly, accessible explanations without overwhelming jargon.
                - When code is requested, provide clear step-by-step instructions and exact terminal run commands.
                - Explain what the code does simply.
                - Note: Beginner level changes the explanation depth, NOT the requested task!
            """.trimIndent()
            "intermediate" -> """
                - USER EXPERIENCE LEVEL: INTERMEDIATE.
                - Provide clean architecture, modularity, idiomatic code, and appropriate error handling.
                - Keep explanations concise and focused on design choices.
            """.trimIndent()
            else -> """
                - USER EXPERIENCE LEVEL: ADVANCED.
                - Focus on high performance, enterprise architecture, security hardening, and clean abstractions.
                - Minimal boilerplate explanation; focus on architectural trade-offs and edge cases.
            """.trimIndent()
        }

        val typeDirective = when {
            intent.isPromptRequested -> """
                CRITICAL DIRECTIVE — PROMPT GENERATION MODE:
                The user asked for a PROMPT (Prompt Engineering Request).
                DO NOT generate application code! DO NOT return implementation code!
                Construct a copy-paste-ready master prompt specification formatted in markdown.
                Include:
                ### 📋 COPY-PASTE PROMPT
                ```markdown
                # Role & Objective
                # Tech Stack
                # Functional Specifications
                # Architecture & File Structure
                # Styling & UI Requirements
                # Edge Cases & Error Handling
                ```
            """.trimIndent()

            intent.isCodeReviewRequested -> """
                CRITICAL DIRECTIVE — CODE REVIEW MODE:
                The user explicitly asked to review or audit code.
                Perform a structured code review evaluating:
                - Code Quality & Clean Code principles
                - Security vulnerabilities & sensitive data handling
                - Performance bottlenecks & optimizations
                - Error handling & edge cases
                Format findings with clear severity tags: [CRITICAL], [HIGH], [MEDIUM], [LOW].
            """.trimIndent()

            intent.isDebugRequested -> """
                CRITICAL DIRECTIVE — DEBUG MODE:
                The user has an error or debugging request.
                Structure your answer strictly as:
                ### 🐞 Problem Identified
                [1-2 sentence summary of the issue]
                ### 🔍 Why It Happened
                [Root cause analysis]
                ### 🛠 Fix & Commands
                [Step-by-step fix commands or package installations]
                ### 💻 Corrected Code
                ```[language]
                [Clean working code]
                ```
            """.trimIndent()

            intent.isGeneralQuestion -> """
                CRITICAL DIRECTIVE — GENERAL QUESTION MODE:
                The user is asking a general question, math problem, or conceptual query.
                - Answer the user's actual question directly, accurately, and naturally.
                - For math (e.g. "What is 2+2?"), simply answer the calculation directly (e.g. "2 + 2 = 4.").
                - Do NOT produce code blocks, folder structures, or architecture unless specifically asked.
                - NEVER activate Code Review, Security Audit, or Performance Audit for normal general questions.
            """.trimIndent()

            intent.isMultiFileProjectRequested -> """
                CRITICAL DIRECTIVE — COMPLETE PROJECT MODE:
                The user explicitly requested a complete multi-file project or application structure.
                Provide:
                ### 🏗 Project Overview & Architecture
                ### 📁 Folder & File Structure
                ### 📦 Dependencies & Installation
                ### 💻 Core Files Implementation (separate code blocks per file with headers)
                ### 🚀 Setup & Run Instructions
            """.trimIndent()

            else -> """
                CRITICAL DIRECTIVE — CODING MODE:
                - Generate clean, complete, working code matching the user's requested language (${intent.detectedLanguage ?: "appropriate language"}).
                - If the user requested Bash/Shell, provide Bash/Shell, NOT Python or Java!
                - If the user requested Python, provide Python!
                - DEFAULT TO A SINGLE COPY-PASTE-READY CODE BLOCK with:
                  1. Short explanation
                  2. Complete working code block
                  3. How to run commands
                - Do NOT generate multi-file folder structures or enterprise boilerplates unless explicitly requested.
            """.trimIndent()
        }

        val contextInfo = buildString {
            if (!projectContext.isNullOrBlank()) {
                append("\n[Active Project Context: $projectContext]\n")
            }
            if (!currentFileName.isNullOrBlank() && !currentFileContent.isNullOrBlank()) {
                append("\n[Active Working File: $currentFileName]\n```\n$currentFileContent\n```\n")
            }
        }

        return """
            You are "AI AGENT FOR DEVELOPERS by Rauf", an intelligent AI coding partner and software assistant.
            
            CORE OPERATING RULES:
            1. READ AND UNDERSTAND the user's exact message before responding.
            2. NEVER force every request into code review, project scaffold, or a fixed template.
            3. Respect requested programming languages strictly (Bash -> Bash, Python -> Python, JavaScript -> JavaScript, SQL -> SQL).
            4. If the user asks a general question or math (e.g. "What is 2+2?"), give a direct, simple, accurate answer.
            5. If the user asks for a prompt, return a copy-paste prompt specification, NOT executable code.
            6. Match the response size to the user request. Default to a single concise code block for standard coding queries.
            
            $levelGuide
            
            $typeDirective
            
            $contextInfo
        """.trimIndent()
    }
}

