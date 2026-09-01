package com.example.ai

object RequestClassifier {

    fun detectRequestType(input: String, selectedMode: RequestType): RequestType {
        if (selectedMode != RequestType.AUTO) {
            return selectedMode
        }

        val text = input.trim().lowercase()

        // 1. Check for explicit PROMPT request (Critical: Never confuse prompt with code)
        if (text.startsWith("give me a prompt") ||
            text.startsWith("create a prompt") ||
            text.startsWith("write a prompt") ||
            text.startsWith("generate a prompt") ||
            text.startsWith("prompt for") ||
            text.contains("system prompt") ||
            text.contains("ai studio prompt") ||
            text.contains("master prompt") ||
            text.contains("prompt to build") ||
            text.contains("prompt to create")
        ) {
            return RequestType.PROMPT
        }

        // 2. Check for DEBUG / FIX BUG
        if (text.contains("error") ||
            text.contains("exception") ||
            text.contains("stack trace") ||
            text.contains("fix this bug") ||
            text.contains("fix error") ||
            text.contains("failed to compile") ||
            text.contains("crash") ||
            text.contains("not defined") ||
            text.contains("nullpointer") ||
            text.contains("syntaxerror") ||
            text.startsWith("debug") ||
            text.startsWith("fix")
        ) {
            return RequestType.DEBUG
        }

        // 3. Check for EXPLAIN
        if (text.startsWith("explain") ||
            text.startsWith("what does this code do") ||
            text.startsWith("how does this work") ||
            text.contains("explain line by line") ||
            text.contains("explain like i'm a beginner") ||
            text.contains("what is the difference between") ||
            text.contains("explain this concept")
        ) {
            return RequestType.EXPLAIN
        }

        // 4. Check for BUILD PROJECT
        if (text.startsWith("build me a project") ||
            text.startsWith("create a project") ||
            text.startsWith("scaffold") ||
            text.startsWith("full project") ||
            text.startsWith("build me a full") ||
            text.startsWith("build a complete website") ||
            text.contains("folder structure") ||
            text.contains("multi-file project")
        ) {
            return RequestType.PROJECT
        }

        // 5. Check for CODE CONVERSION
        if (text.contains("convert to") ||
            text.contains("translate to") ||
            text.contains("rewrite in") ||
            text.contains("from python to") ||
            text.contains("from javascript to") ||
            text.contains("from java to") ||
            text.contains("to typescript") ||
            text.contains("to kotlin") ||
            text.contains("to react")
        ) {
            return RequestType.CONVERT
        }

        // 6. Check for CODE REVIEW
        if (text.startsWith("review") ||
            text.contains("code review") ||
            text.contains("security audit") ||
            text.contains("check my code") ||
            text.contains("find vulnerabilities")
        ) {
            return RequestType.REVIEW
        }

        // 7. Check for OPTIMIZE
        if (text.startsWith("optimize") ||
            text.startsWith("refactor") ||
            text.contains("make it faster") ||
            text.contains("reduce memory") ||
            text.contains("improve performance")
        ) {
            return RequestType.OPTIMIZE
        }

        // 8. Check for TESTS
        if (text.startsWith("test") ||
            text.contains("write unit tests") ||
            text.contains("generate tests") ||
            text.contains("test cases") ||
            text.contains("jest tests") ||
            text.contains("pytest") ||
            text.contains("junit")
        ) {
            return RequestType.TESTS
        }

        // 9. Check for GAME DEV
        if (text.contains("game") ||
            text.contains("unity") ||
            text.contains("unreal") ||
            text.contains("godot") ||
            text.contains("three.js") ||
            text.contains("webgl") ||
            text.contains("player controller")
        ) {
            return RequestType.GAME_DEV
        }

        // 10. Check for GIT
        if (text.startsWith("git ") ||
            text.contains("merge conflict") ||
            text.contains("git commit") ||
            text.contains("git push") ||
            text.contains("git rebase") ||
            text.contains("github pull request")
        ) {
            return RequestType.GIT
        }

        // 11. Check for TERMINAL
        if (text.startsWith("terminal") ||
            text.startsWith("command to") ||
            text.startsWith("bash script") ||
            text.contains("shell command") ||
            text.contains("npm run") ||
            text.contains("how to run in terminal")
        ) {
            return RequestType.TERMINAL
        }

        // 12. Check for DATABASE
        if (text.contains("sql query") ||
            text.contains("database schema") ||
            text.contains("mongodb") ||
            text.contains("postgresql") ||
            text.contains("sqlite") ||
            text.contains("firestore")
        ) {
            return RequestType.DATABASE
        }

        // 13. Check for API
        if (text.contains("rest api") ||
            text.contains("fastapi") ||
            text.contains("express endpoint") ||
            text.contains("crud endpoints") ||
            text.contains("swagger")
        ) {
            return RequestType.API
        }

        // 14. Check for LEARN
        if (text.startsWith("learn") ||
            text.startsWith("teach me") ||
            text.startsWith("roadmap for") ||
            text.contains("beginner tutorial") ||
            text.contains("lesson on")
        ) {
            return RequestType.LEARN
        }

        return RequestType.CODE
    }

    fun buildSystemInstruction(
        type: RequestType,
        experienceLevel: String,
        projectContext: String? = null,
        currentFileContent: String? = null,
        currentFileName: String? = null
    ): String {
        val levelGuide = when (experienceLevel.lowercase()) {
            "beginner" -> """
                - PRIMARY AUDIENCE: BEGINNER DEVELOPER.
                - Explain technical concepts and jargon simply with relatable analogies.
                - Provide clear step-by-step instructions.
                - Tell the user EXACTLY where to paste the code, how to create the file, and how to install dependencies.
                - Provide clear terminal commands with explanation of what each flag or tool does (e.g. explain `npm install`).
                - Point out common pitfalls and beginner traps.
            """.trimIndent()
            "intermediate" -> """
                - AUDIENCE: INTERMEDIATE DEVELOPER.
                - Provide clean architecture, modular file structures, and robust typing.
                - Include performance considerations, API error handling, and testability.
                - Keep explanations concise and focused on design choices.
            """.trimIndent()
            else -> """
                - AUDIENCE: ADVANCED DEVELOPER.
                - Focus on high performance, enterprise architecture, security hardening, and clean abstractions.
                - Minimal boilerplate explanation; focus on architectural trade-offs and edge cases.
            """.trimIndent()
        }

        val typeGuide = when (type) {
            RequestType.PROMPT -> """
                CRITICAL DIRECTIVE: The user asked for a PROMPT (Prompt Engineering Request).
                DO NOT return executable code.
                Instead, construct a comprehensive, master copy-paste prompt specification with the following structured sections:
                
                ### 📋 COPY-PASTE PROMPT
                ```markdown
                # System Role & Persona
                [Specific AI Role]
                
                # Objective
                [Core Goal]
                
                # Technology Stack
                [Languages, Frameworks, Libraries]
                
                # Functional Specifications & Features
                [Bullet list of required features]
                
                # Architecture & Project File Structure
                [Proposed directory layout]
                
                # UI/UX & Styling Requirements
                [Visual guidelines, responsiveness, palette]
                
                # Error Handling & Edge Cases
                [Resilience guidelines]
                
                # Final Output Requirements
                [Exact delivery format]
                ```
                
                ### 💡 What This Prompt Does & How to Use It
                [Explain where to paste this prompt in Google AI Studio, Gemini, or Coding Agents]
            """.trimIndent()

            RequestType.DEBUG -> """
                The user has an ERROR / DEBUGGING request. Structure your answer strictly as:
                ### 🐞 Problem Identified
                [Clear 1-sentence statement of the issue]

                ### 🔍 Why It Happened
                [Root cause analysis in plain, accessible terms]

                ### 🛠 Exact Fix & Terminal Commands
                [Step-by-step commands to install missing modules, fix permissions, or configure paths]

                ### 💻 Corrected Code
                ```[language]
                [Clean working code snippet]
                ```

                ### 🛡 How to Prevent This in Future
                [Best practice preventive guidelines]
            """.trimIndent()

            RequestType.PROJECT -> """
                The user wants to BUILD A FULL PROJECT. Provide a complete architectural breakdown:
                ### 🏗 Project Overview & Architecture
                [Tech stack selection and key components]

                ### 📁 Folder & File Structure
                ```
                project-root/
                ├── ...
                ```

                ### 📦 Dependencies & Installation
                [Exact terminal commands]

                ### 💻 Core Files Implementation
                [Provide separate, complete code blocks for each essential file with file header `### File: path/name.ext`]

                ### 🚀 Setup & Run Instructions
                [Step-by-step execution guide]

                ### 🔮 Next Steps & Enhancements
                [Recommended follow-up features]
            """.trimIndent()

            RequestType.EXPLAIN -> """
                The user wants a CODE EXPLANATION. Provide:
                ### 💡 High-Level Summary
                [What the code does in 2-3 simple sentences]

                ### 🔍 Line-by-Line / Component Breakdown
                [Walkthrough of the key sections]

                ### ⚠️ Potential Bottlenecks or Edge Cases
                [What could break or cause unexpected behavior]

                ### 🌟 Pro Tips & Improvements
                [How to make it cleaner, safer, or more idiomatic]
            """.trimIndent()

            RequestType.REVIEW -> """
                The user wants a CODE REVIEW. Audit for:
                - Security vulnerabilities & sensitive data leaks
                - Performance bottlenecks & unnecessary re-renders/allocations
                - Maintainability, naming & clean code principles
                - Error handling & edge cases
                Format findings with Severity Tags: [CRITICAL], [HIGH], [MEDIUM], [LOW].
            """.trimIndent()

            RequestType.OPTIMIZE -> """
                The user wants CODE OPTIMIZATION & REFACTORING.
                Provide:
                ### ⏱ Before vs After Analysis
                ### 💻 Refactored Code
                ```[language]
                [Optimized code]
                ```
                ### 📈 Key Improvements & Benchmarks
                [Explain why the new version is faster/cleaner without breaking functionality]
            """.trimIndent()

            RequestType.CONVERT -> """
                The user wants CODE CONVERSION.
                Provide the translated code with equivalent idiomatic constructs, note any external library differences, and highlight manual adjustments needed.
            """.trimIndent()

            RequestType.TESTS -> """
                The user wants AUTOMATED TESTS.
                Generate comprehensive Unit & Integration test suites, covering happy paths, edge cases, error conditions, and mock dependencies.
            """.trimIndent()

            else -> """
                The user wants CODE GENERATION.
                Provide:
                ### 💡 Solution Overview
                ### 💻 Complete Working Code
                [Include full practical code with imports and comments]
                ### 🚀 How to Run
                [Exact commands and step-by-step instructions]
                ### 🧠 Explanation
                [How it works]
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
            You are "AI AGENT FOR DEVELOPERS by Rauf", an expert AI coding assistant, software architect, and patient coding mentor.
            Your mission is to empower developers of all experience levels to build, debug, understand, and ship software.
            
            $levelGuide
            
            $typeGuide
            
            $contextInfo
            
            Always format code blocks with triple backticks and language identifiers (e.g. ```typescript, ```python, ```html).
            Never give vague answers or half-completed snippets without explanation.
        """.trimIndent()
    }
}
