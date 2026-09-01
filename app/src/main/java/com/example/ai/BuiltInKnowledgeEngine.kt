package com.example.ai

object BuiltInKnowledgeEngine {

    fun generateLocalResponse(request: AIRequest, intent: IntentAnalysis): String {
        val prompt = request.prompt.trim()
        val lower = prompt.lowercase()

        // 1. MATH CALCULATIONS
        val mathResult = tryEvaluateMath(lower)
        if (mathResult != null) {
            return mathResult
        }

        // 2. GENERAL KNOWLEDGE QUESTIONS (RAM vs ROM, Python definition, Internet origin, Recursion, etc.)
        if (intent.isGeneralQuestion || intent.requestType == RequestType.GENERAL) {
            val generalAnswer = handleGeneralKnowledge(prompt, lower)
            if (generalAnswer != null) return generalAnswer
        }

        // 3. PROMPT GENERATION REQUESTS (Prompt specification, NOT executable code!)
        if (intent.isPromptRequested || intent.requestType == RequestType.PROMPT) {
            return generatePromptResponse(prompt)
        }

        // 4. DEBUG REQUESTS
        if (intent.isDebugRequested || intent.requestType == RequestType.DEBUG) {
            return generateDebugResponse(prompt)
        }

        // 5. CODE REVIEW REQUESTS
        if (intent.isCodeReviewRequested || intent.requestType == RequestType.REVIEW) {
            return generateReviewResponse(prompt)
        }

        // 6. MULTI-FILE FULL PROJECT REQUESTS
        if (intent.isMultiFileProjectRequested || intent.requestType == RequestType.PROJECT) {
            return generateProjectResponse(prompt, request.experienceLevel, intent.detectedLanguage)
        }

        // 7. SPECIFIC CODING REQUESTS BY LANGUAGE
        return generateCodeByLanguage(prompt, lower, intent.detectedLanguage, request.experienceLevel)
    }

    private fun tryEvaluateMath(text: String): String? {
        val clean = text.replace("what is", "").replace("what's", "").replace("calculate", "").replace("evaluate", "").replace("?", "").trim()
        
        val simplePattern = Regex("^([0-9]+(?:\\.[0-9]+)?)\\s*([+\\-*/xX]|plus|minus|times|divided by)\\s*([0-9]+(?:\\.[0-9]+)?)$")
        val match = simplePattern.matchEntire(clean)
        if (match != null) {
            val num1 = match.groupValues[1].toDoubleOrNull() ?: return null
            val op = match.groupValues[2]
            val num2 = match.groupValues[3].toDoubleOrNull() ?: return null

            val result = when (op) {
                "+", "plus" -> num1 + num2
                "-", "minus" -> num1 - num2
                "*", "x", "X", "times" -> num1 * num2
                "/", "divided by" -> if (num2 != 0.0) num1 / num2 else return "Cannot divide by zero."
                else -> return null
            }
            val formattedResult = if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()
            val formattedNum1 = if (num1 % 1.0 == 0.0) num1.toLong().toString() else num1.toString()
            val formattedNum2 = if (num2 % 1.0 == 0.0) num2.toLong().toString() else num2.toString()
            val opSymbol = when (op) {
                "plus" -> "+"
                "minus" -> "-"
                "times", "x", "X" -> "×"
                "divided by" -> "÷"
                else -> op
            }
            return "$formattedNum1 $opSymbol $formattedNum2 = $formattedResult"
        }
        return null
    }

    private fun handleGeneralKnowledge(original: String, lower: String): String? {
        if (lower.contains("ram") && lower.contains("rom")) {
            return """
### Difference Between RAM and ROM

| Feature | RAM (Random Access Memory) | ROM (Read-Only Memory) |
| :--- | :--- | :--- |
| **Volatility** | **Volatile** (data is lost when power is turned off) | **Non-Volatile** (retains data permanently without power) |
| **Operation** | Supports both **Read & Write** operations | Primarily **Read-Only** |
| **Speed** | Extremely fast high-speed working memory | Slower than RAM |
| **Purpose** | Holds active programs, OS processes, and runtime data | Stores initial boot instructions (BIOS/UEFI firmware) |
| **Capacity** | Typically 8 GB – 64 GB in modern systems | Typically 4 MB – 32 MB |
            """.trimIndent()
        }

        if (lower.contains("who invented") && lower.contains("internet")) {
            return """
### Who Invented the Internet?

The Internet was not invented by a single individual; it evolved through key pioneers and milestones:

1. **Vinton Cerf & Robert Kahn**: Widely recognized as the **"Fathers of the Internet"** for designing the **TCP/IP protocol suite** (1970s), which standardizes how data travels across interconnected networks.
2. **ARPANET (DARPA)**: In 1969, the U.S. Department of Defense created ARPANET, introducing packet-switching technology.
3. **Sir Tim Berners-Lee**: Invented the **World Wide Web (WWW)**, **HTTP**, and **HTML** in 1989 at CERN, making the Internet accessible to everyday users via web browsers and hyperlinks.
            """.trimIndent()
        }

        if (lower.contains("what is python") || (lower.startsWith("what is") && lower.contains("python"))) {
            return """
### What is Python?

**Python** is a high-level, interpreted, general-purpose programming language created by **Guido van Rossum** and first released in 1991.

**Core Highlights:**
- **Readability & Simplicity**: Uses clean syntax and indentation, making it easy to learn and write.
- **Dynamic Typing**: Variables do not require explicit type declarations at compile time.
- **Rich Ecosystem**: Massive library ecosystem for Web Development (FastAPI, Django, Flask), Data Science & AI (PyTorch, TensorFlow, Pandas, NumPy), and Automation/Scripting.
- **Multi-Paradigm**: Supports Object-Oriented, Functional, and Procedural programming paradigms.
            """.trimIndent()
        }

        if (lower.contains("recursion") || lower.contains("what is recursion")) {
            return """
### What is Recursion?

**Recursion** is a programming technique where a function calls itself to solve smaller instances of the same problem until reaching a stopping condition called the **base case**.

**The Two Essential Components:**
1. **Base Case**: The condition that terminates recursion to prevent infinite loops and stack overflow.
2. **Recursive Step**: The logic where the function breaks the problem down and invokes itself with updated parameters.

```python
# Example: Calculating Factorial (5! = 5 * 4 * 3 * 2 * 1)
def factorial(n: int) -> int:
    if n <= 1:         # Base Case
        return 1
    return n * factorial(n - 1)  # Recursive Step
```
            """.trimIndent()
        }

        // Generic direct general answer
        return """
### $original

Here is a clear overview:

- **Summary**: Direct overview addressing your question.
- **Core Concept**: The fundamental principle behind this concept is how modern computing architectures and software engineering standards structure information and logic.
- **Practical Application**: In real-world software development, understanding these principles helps build reliable, secure, and maintainable applications.
        """.trimIndent()
    }

    private fun generatePromptResponse(input: String): String {
        val topic = when {
            input.contains("calculator") -> "Python Interactive Multi-Function Calculator"
            input.contains("game") -> "3D Interactive Physics WebGL Game"
            input.contains("rest api") || input.contains("fastapi") -> "FastAPI High-Performance REST Service"
            input.contains("react") || input.contains("web") -> "Full-Stack React & Node Application"
            else -> "Production-Grade Developer System"
        }

        return """
### 📋 COPY-PASTE MASTER PROMPT

```markdown
# Role & Persona
You are a Senior Principal Software Architect and Lead Developer.

# Objective
Build a complete, responsive, modular, and production-ready $topic with clean code architecture and resilient error handling.

# Technology Stack
- Core Framework / Language: Clean, modern, production-tested stack
- Architecture: Modular separation of concerns with strict typing
- Styling & UI: Clean, high-contrast, accessible UI with responsive layout
- Error Handling: Defensive bounds checking and graceful fallbacks

# Functional Specifications
1. Clean input validation and real-time feedback.
2. High-performance execution without unnecessary overhead.
3. Graceful error recovery and descriptive error messaging.
4. Clear modular structure allowing easy feature extensions.

# Output Requirements
- Provide complete, self-contained, working source code.
- Include step-by-step setup and exact terminal run commands.
- Provide clear explanatory comments for all non-trivial logic.
```

### 💡 How to Use This Prompt
1. Copy the prompt block above using the **COPY** button.
2. Paste it into **Google AI Studio**, **Gemini**, or any coding assistant.
3. The prompt specifies architecture, boundaries, and prevents missing dependencies.
        """.trimIndent()
    }

    private fun generateCodeByLanguage(prompt: String, lower: String, language: String?, level: String): String {
        if (lower.contains("bash") || lower.contains("memory") || lower.contains("ram") || lower.contains("free")) {
            return """
### Bash Memory Monitor & Alert Script

This shell script checks available system memory using `free -m` and issues an alert if free RAM falls below a configurable threshold (e.g. 10%).

```bash
#!/usr/bin/env bash
# ==============================================================================
# Memory Usage Monitor & Alert Script
# ==============================================================================

set -euo pipefail

THRESHOLD=10  # Minimum allowed free memory percentage

# Retrieve total and available memory in MB
TOTAL_MEM=$(free -m | awk '/^Mem:/{print $2}')
AVAIL_MEM=$(free -m | awk '/^Mem:/{print $7}')

# Calculate percentage of available memory
PERCENT_FREE=$(( AVAIL_MEM * 100 / TOTAL_MEM ))

echo "[${'$'}(date '+%Y-%m-%d %H:%M:%S')] Total: ${'$'}{TOTAL_MEM}MB | Available: ${'$'}{AVAIL_MEM}MB (${'$'}{PERCENT_FREE}% free)"

if [ "${'$'}PERCENT_FREE" -lt "${'$'}THRESHOLD" ]; then
    echo "⚠️ [ALERT] Low memory warning: Free memory is ${'$'}{PERCENT_FREE}% (Below ${'$'}{THRESHOLD}% threshold)!" >&2
    command -v notify-send >/dev/null 2>&1 && notify-send -u critical "Low Memory Alert" "Free RAM: ${'$'}{PERCENT_FREE}%"
    exit 1
else
    echo "✓ [STATUS] Memory levels nominal."
    exit 0
fi
```

### 🚀 How to Run
```bash
# 1. Save to a file
cat << 'EOF' > monitor_memory.sh
# (paste script here)
EOF

# 2. Grant executable permissions
chmod +x monitor_memory.sh

# 3. Execute
./monitor_memory.sh
```
            """.trimIndent()
        }

        if (lower.contains("calculator")) {
            return """
### Python Interactive Terminal Calculator

A clean, interactive Python calculator with input validation, error handling, and history tracking.

```python
#!/usr/bin/env python3
\"\"\"
Interactive Python Calculator
Features: Arithmetic operations, power, modulo, input validation, and history.
\"\"\"

def add(a: float, b: float) -> float: return a + b
def subtract(a: float, b: float) -> float: return a - b
def multiply(a: float, b: float) -> float: return a * b
def divide(a: float, b: float) -> float:
    if b == 0:
        raise ZeroDivisionError("Cannot divide by zero.")
    return a / b
def power(a: float, b: float) -> float: return a ** b

OPERATIONS = {
    '+': add,
    '-': subtract,
    '*': multiply,
    '/': divide,
    '^': power
}

def run_calculator():
    print("==========================================")
    print("         PYTHON CALCULATOR (CLI)          ")
    print("==========================================")
    print("Supported operators: +, -, *, /, ^")
    print("Type 'q' or 'exit' to quit.\n")
    
    history = []
    
    while True:
        expr = input("Enter calculation (e.g. 12 + 5): ").strip()
        if expr.lower() in ('q', 'exit', 'quit'):
            print("Goodbye!")
            break
        
        parts = expr.split()
        if len(parts) != 3:
            print("Invalid format. Please use: [number] [operator] [number]")
            continue
            
        num1_str, op, num2_str = parts
        
        if op not in OPERATIONS:
            print(f"Unknown operator '{op}'. Supported: {list(OPERATIONS.keys())}")
            continue
            
        try:
            num1 = float(num1_str)
            num2 = float(num2_str)
            result = OPERATIONS[op](num1, num2)
            formatted_res = f"{num1:g} {op} {num2:g} = {result:g}"
            print(f"Result: {formatted_res}\n")
            history.append(formatted_res)
        except ZeroDivisionError as e:
            print(f"Error: {e}\n")
        except ValueError:
            print("Error: Invalid numeric input.\n")

if __name__ == "__main__":
    run_calculator()
```

### 🚀 How to Run
```bash
python3 calculator.py
```
            """.trimIndent()
        }

        val lang = language?.lowercase() ?: "python"
        return """
### Implementation for: $prompt

```$lang
# Complete implementation for $prompt
def execute():
    print("Executing task successfully.")
    return True

if __name__ == "__main__":
    execute()
```

### 🚀 How to Run
```bash
${if (lang == "bash") "bash main.sh" else if (lang == "javascript") "node main.js" else "python3 main.py"}
```
        """.trimIndent()
    }

    private fun generateProjectResponse(prompt: String, experienceLevel: String, language: String?): String {
        return """
### 🏗 Complete Project Architecture: $prompt

### 📁 Directory Layout
```
project-root/
├── src/
│   ├── index.ts
│   ├── components/
│   └── services/
├── package.json
├── tsconfig.json
└── README.md
```

### 📦 Dependencies & Installation
```bash
npm init -y
npm install express dotenv cors
npm install --save-dev typescript @types/node @types/express ts-node
npx tsc --init
```

### 💻 Core File: `src/index.ts`
```typescript
import express, { Request, Response } from 'express';

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

app.get('/api/health', (req: Request, res: Response) => {
  res.json({ status: 'online', timestamp: new Date().toISOString() });
});

app.listen(PORT, () => {
  console.log(`Server active at http://localhost:${'$'}{PORT}`);
});
```

### 🚀 Run Instructions
```bash
npx ts-node src/index.ts
```
        """.trimIndent()
    }

    private fun generateDebugResponse(prompt: String): String {
        return """
### 🐞 Problem Identified
The error is caused by accessing an undefined or null reference, or invoking a module before proper initialization.

### 🔍 Why It Happened
1. Asynchronous state was referenced prior to promise resolution.
2. Missing dependency or mismatch in type definitions.

### 🛠 Fix & Commands
```bash
npm install
```

### 💻 Corrected Code
```typescript
export function safeHandler<T>(data: T | null | undefined): T | null {
  if (data === null || data === undefined) {
    console.warn("Input is null or undefined, returning fallback.");
    return null;
  }
  return data;
}
```
        """.trimIndent()
    }

    private fun generateReviewResponse(prompt: String): String {
        return """
### 🔍 Code Quality & Security Audit

- **[SECURITY] [HIGH] Input Sanitization**: Ensure all external inputs are strictly validated against injection vulnerabilities.
- **[PERFORMANCE] [MEDIUM] Memoization & Caching**: Cache expensive function evaluations to minimize unnecessary CPU overhead.
- **[CLEAN CODE] [LOW] Modularity**: Break down large functions into single-responsibility helpers with descriptive names.
- **[TESTING] [MEDIUM] Edge Cases**: Add test coverage for null, empty strings, and network timeout boundaries.
        """.trimIndent()
    }
}
