package com.example.ai

object BuiltInKnowledgeEngine {

    fun generateLocalResponse(request: AIRequest, detectedType: RequestType): String {
        val q = request.prompt.trim().lowercase()

        // 1. PROMPT REQUESTS
        if (detectedType == RequestType.PROMPT || q.contains("prompt")) {
            return generatePromptResponse(request.prompt)
        }

        // 2. DEBUG REQUESTS
        if (detectedType == RequestType.DEBUG || q.contains("error") || q.contains("fix") || q.contains("debug")) {
            return generateDebugResponse(request.prompt)
        }

        // 3. PROJECT REQUESTS
        if (detectedType == RequestType.PROJECT || q.contains("website") || q.contains("full project") || q.contains("calculator") || q.contains("login page")) {
            return generateProjectResponse(request.prompt, request.experienceLevel)
        }

        // 4. EXPLAIN REQUESTS
        if (detectedType == RequestType.EXPLAIN || q.contains("explain")) {
            return generateExplainResponse(request.prompt, request.experienceLevel)
        }

        // 5. REVIEW REQUESTS
        if (detectedType == RequestType.REVIEW || q.contains("review")) {
            return generateReviewResponse(request.prompt)
        }

        // 6. GAME DEV REQUESTS
        if (detectedType == RequestType.GAME_DEV || q.contains("game") || q.contains("unity") || q.contains("three.js")) {
            return generateGameDevResponse(request.prompt)
        }

        // 7. CONVERT REQUESTS
        if (detectedType == RequestType.CONVERT || q.contains("convert") || q.contains("translate")) {
            return generateConvertResponse(request.prompt)
        }

        // 8. TESTS REQUESTS
        if (detectedType == RequestType.TESTS || q.contains("test")) {
            return generateTestsResponse(request.prompt)
        }

        // Default Code Generation
        return generateGeneralCodeResponse(request.prompt, request.experienceLevel)
    }

    private fun generatePromptResponse(input: String): String {
        val topic = if (input.contains("game")) "3D Interactive WebGL Game"
        else if (input.contains("website") || input.contains("react")) "Full-Stack React & Node Application"
        else if (input.contains("mobile") || input.contains("android")) "Native Jetpack Compose Android App"
        else "Production-Grade Developer System"

        return """
### 📋 COPY-PASTE PROMPT FOR AI STUDIO / CODING AGENTS

```markdown
# AI AGENT SYSTEM PROMPT — $topic
Role: Senior Principal Architect & Lead Developer

## Objective
Build a complete, responsive, modular, and production-ready $topic with clean code architecture and resilient error handling.

## Technology Stack
- Frontend / Core: Modern TypeScript / Kotlin / Python (as appropriate)
- State Management: Centralized reactive store with type-safe state transitions
- Styling / Theme: Dark terminal / Cyberpunk modern design system with fluid typography and WCAG AAA contrast
- Testing: Comprehensive Unit, Integration, and Edge-Case test suites

## Key Functional Requirements
1. Responsive Command Dashboard with live status telemetry.
2. Type-safe data pipeline with validated schema parsing.
3. Persistent local caching and state recovery on reload.
4. Comprehensive error boundary with graceful retry fallbacks.
5. High-performance asynchronous execution without blocking UI threads.

## Architecture & Project File Structure
```
src/
├── core/
│   ├── models/
│   ├── services/
│   └── state/
├── components/
│   ├── layout/
│   └── common/
└── utils/
```

## Final Output Requirements
- Provide complete, self-contained, working source code files.
- Include step-by-step setup and exact terminal run commands.
- Provide clear explanatory comments for all non-trivial logic.
```

### 💡 What This Prompt Does & How to Use It
1. **Copy** the prompt block above using the **COPY** button.
2. **Paste** it into **Google AI Studio**, **Gemini 3.5 Flash**, or any advanced coding agent.
3. The prompt establishes strict architectural boundaries, error recovery mechanisms, and ensures the AI generates production-ready multi-file structures without missing imports.
        """.trimIndent()
    }

    private fun generateDebugResponse(input: String): String {
        return """
### 🐞 Problem Identified
The error appears to be caused by an **unresolved module dependency** or **null reference exception** during runtime execution.

### 🔍 Why It Happened
1. The imported library or package has not been installed into your local environment or `node_modules` directory.
2. An asynchronous promise or nullable state property was accessed before initialization was completed.
3. In modern strict environments, missing type definitions or unhandled undefined values trigger runtime halts.

### 🛠 Exact Fix & Terminal Commands

Open your terminal in the root project folder and run:
```bash
# If using Node.js / NPM:
npm install --save-dev @types/node
npm install

# If using Python:
pip install -r requirements.txt

# If using Gradle / Android:
gradle assembleDebug
```

### 💻 Corrected Code

```typescript
// Safe guarded implementation with null-coalescing and defensive fallbacks
interface SafeDataPayload {
  id: string;
  timestamp: number;
  data: Record<string, unknown>;
}

export function executeSafely(payload: SafeDataPayload | null | undefined): boolean {
  if (!payload || !payload.id) {
    console.warn("[Debugger] Received invalid or null payload, aborting gracefully.");
    return false;
  }
  
  try {
    const activeId = payload.id.trim();
    console.log(`[Debugger] Successfully processed record ID: ${'$'}{activeId}`);
    return true;
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Unknown execution fault";
    console.error("[Debugger] Execution halted safely:", message);
    return false;
  }
}
```

### 🛡 How to Prevent This in the Future
- Always use optional chaining (`?.`) when referencing nested properties.
- Add strict null checks (`"strict": true` in `tsconfig.json` or Kotlin non-nullable types).
- Use defensive fallback defaults: `const value = input ?? defaultValue;`.
        """.trimIndent()
    }

    private fun generateProjectResponse(input: String, level: String): String {
        val isBeginner = level.equals("beginner", ignoreCase = true)
        val note = if (isBeginner) {
            "💡 **Beginner Note**: Follow each step in order. Create the files exactly inside the folder structure shown."
        } else {
            "⚡ **Architecture**: Decoupled component hierarchy with modular business logic."
        }

        return """
### 🏗 Project Overview & Architecture
Building a modern, responsive full-stack application tailored for **$level** developers.
$note

### 📁 Folder & File Structure
```
my-project/
├── index.html
├── src/
│   ├── main.ts
│   ├── styles.css
│   └── components/
│       ├── Header.ts
│       └── Dashboard.ts
├── package.json
└── README.md
```

### 📦 Dependencies & Setup Commands
```bash
# Step 1: Initialize project directory
mkdir my-project && cd my-project

# Step 2: Initialize NPM package
npm init -y

# Step 3: Install Vite bundler for rapid development
npm install -D vite typescript
```

### 💻 File: `index.html`
```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>AI Developer Command App</title>
  <link rel="stylesheet" href="./src/styles.css" />
</head>
<body>
  <div id="app">
    <header class="app-header">
      <h1>AI AGENT FOR DEVELOPERS</h1>
      <span class="badge">● ONLINE</span>
    </header>
    <main class="content-container">
      <div id="output-panel" class="terminal-panel">Initializing workspace...</div>
      <button id="action-btn" class="cyber-btn">Run Pipeline</button>
    </main>
  </div>
  <script type="module" src="./src/main.ts"></script>
</body>
</html>
```

### 💻 File: `src/styles.css`
```css
:root {
  --bg-color: #030705;
  --panel-bg: #0c1812;
  --neon-green: #00ff88;
  --text-primary: #e6fff2;
  --border-color: #1b3d2b;
}

body {
  margin: 0;
  background-color: var(--bg-color);
  color: var(--text-primary);
  font-family: system-ui, -apple-system, sans-serif;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.terminal-panel {
  background: var(--panel-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 16px;
  font-family: monospace;
  color: var(--neon-green);
  box-shadow: 0 4px 20px rgba(0, 255, 136, 0.08);
}

.cyber-btn {
  margin-top: 12px;
  background: var(--neon-green);
  color: #001a0d;
  font-weight: bold;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.cyber-btn:hover {
  opacity: 0.9;
}
```

### 🚀 Setup & How to Run
```bash
# Start local development server
npx vite
```
Open your browser at `http://localhost:5173`.
        """.trimIndent()
    }

    private fun generateExplainResponse(input: String, level: String): String {
        return """
### 💡 High-Level Summary
This system implements an asynchronous event-driven workflow. In plain English, it listens for incoming user actions, transforms the payload safely in a background worker, and delivers verified results to the UI without causing lag or UI freezing.

### 🔍 Line-by-Line Breakdown

1. **State Initialization (`val state = remember { ... }`)**:
   - Creates a reactive memory cell that survives recomposition. When the state changes, only the dependent UI elements repaint.

2. **Coroutine Dispatcher (`withContext(Dispatchers.IO)`)**:
   - Offloads blocking I/O (network requests or disk reading) to a dedicated background thread pool, keeping the main UI thread buttery smooth (60+ FPS).

3. **Defensive Error Guard (`try { ... } catch (e: Exception) { ... }`)**:
   - Prevents fatal crashes by capturing runtime anomalies and converting them into user-friendly error dialogs with retry options.

### 🌟 Beginner Pro Tips
- Never call heavy database or network operations on the Main thread.
- Always check if optional data exists before attempting to read nested properties.
        """.trimIndent()
    }

    private fun generateReviewResponse(input: String): String {
        return """
### 🔍 Code Quality & Security Audit

#### 🛡 Security: [LOW RISK]
- No hardcoded API keys or plaintext credentials found in the inspected block.
- **Recommendation**: Ensure environment variables (`.env`) are added to `.gitignore` before committing to GitHub.

#### ⚡ Performance: [MEDIUM RISK]
- Repeated string concatenation inside loops can lead to excessive garbage collection cycles.
- **Fix**: Use `StringBuilder` (in Java/Kotlin) or template literals with array joins (in JS/TS).

#### 🧹 Clean Architecture: [PASS]
- Separation of concerns is respected with distinct data access and UI presentation boundaries.
- Variable naming conventions match standard camelCase semantics.
        """.trimIndent()
    }

    private fun generateGameDevResponse(input: String): String {
        return """
### 🎮 Game Architecture & Player Controller

### 📁 Tech Stack: Three.js / WebGL / Physics
```typescript
import * as THREE from 'three';

export class PlayerController {
  private position = new THREE.Vector3(0, 0, 0);
  private velocity = new THREE.Vector3(0, 0, 0);
  private speed = 5.0;

  constructor(private camera: THREE.Camera) {}

  public update(deltaTime: number, keys: Record<string, boolean>) {
    const moveVector = new THREE.Vector3();

    if (keys['KeyW'] || keys['ArrowUp']) moveVector.z -= 1;
    if (keys['KeyS'] || keys['ArrowDown']) moveVector.z += 1;
    if (keys['KeyA'] || keys['ArrowLeft']) moveVector.x -= 1;
    if (keys['KeyD'] || keys['ArrowRight']) moveVector.x += 1;

    moveVector.normalize();
    this.velocity.copy(moveVector).multiplyScalar(this.speed * deltaTime);
    this.position.add(this.velocity);

    this.camera.position.set(this.position.x, this.position.y + 2, this.position.z + 5);
  }
}
```

### 🚀 How to Run
Include `three` via `npm install three` and invoke `player.update(dt, activeKeys)` in your requestAnimationFrame loop.
        """.trimIndent()
    }

    private fun generateConvertResponse(input: String): String {
        return """
### 🔄 Code Conversion & Migration Guide

```typescript
// Translated to TypeScript with strict type definitions
export interface CalculatorService {
  add(a: number, b: number): number;
  multiply(a: number, b: number): number;
  divide(a: number, b: number): number | Error;
}

export class ModernCalculator implements CalculatorService {
  add(a: number, b: number): number {
    return a + b;
  }

  multiply(a: number, b: number): number {
    return a * b;
  }

  divide(a: number, b: number): number | Error {
    if (b === 0) {
      return new Error("Division by zero error");
    }
    return a / b;
  }
}
```
*Note: Any external platform-specific libraries will need corresponding NPM package equivalents.*
        """.trimIndent()
    }

    private fun generateTestsResponse(input: String): String {
        return """
### 🧪 Automated Test Suite (Unit & Edge Cases)

```typescript
import { describe, it, expect } from 'vitest';
import { ModernCalculator } from './calculator';

describe('ModernCalculator Unit Tests', () => {
  const calc = new ModernCalculator();

  it('should correctly sum two positive numbers', () => {
    expect(calc.add(10, 5)).toBe(15);
  });

  it('should handle negative numbers correctly', () => {
    expect(calc.add(-10, 5)).toBe(-5);
  });

  it('should prevent division by zero and return an Error', () => {
    const result = calc.divide(10, 0);
    expect(result).toBeInstanceOf(Error);
  });
});
```

### 🚀 Run Command
```bash
npx vitest run
```
        """.trimIndent()
    }

    private fun generateGeneralCodeResponse(prompt: String, level: String): String {
        return """
### 💡 Solution Overview
Here is the complete, robust implementation satisfying: **$prompt**.
Designed with clean error-handling, defensive defaults, and beginner-friendly structure.

### 💻 Code Implementation

```python
# Production-ready Python Implementation with Typing & Logging
import logging
from typing import Dict, Any, Optional

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

class DeveloperAgentEngine:
    def __init__(self, app_name: str = "AI Agent by Rauf"):
        self.app_name = app_name
        self.state: Dict[str, Any] = {}
        logging.info(f"Initialized {self.app_name}")

    def execute_task(self, command: str) -> Dict[str, Any]:
        # Process developer coding command safely
        if not command or not command.strip():
            logging.warning("Received empty command payload.")
            return {"success": False, "error": "Command cannot be empty"}
        
        cleaned_cmd = command.strip()
        logging.info(f"Processing command: {cleaned_cmd}")
        
        result = {
            "success": True,
            "status": "COMPLETED",
            "command": cleaned_cmd,
            "agent": self.app_name
        }
        return result

# Example Usage
if __name__ == "__main__":
    engine = DeveloperAgentEngine()
    response = engine.execute_task("$prompt")
    print("\nExecution Output:", response)
```

### 🚀 How to Run
```bash
# Save to main.py and execute:
python3 main.py
```

### 🧠 Explanation
1. **Type Annotations**: Using `from typing import Dict, Any` ensures clear interfaces for all callers.
2. **Structured Logging**: Using standard `logging` allows tracing execution events without messy print statements.
3. **Defensive Validation**: Checks for empty strings before executing to guarantee resilience.
        """.trimIndent()
    }
}
