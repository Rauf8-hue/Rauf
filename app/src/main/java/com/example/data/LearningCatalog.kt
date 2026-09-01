package com.example.data

data class LearningLesson(
    val id: String,
    val trackId: String,
    val title: String,
    val estimatedMinutes: Int,
    val summary: String,
    val conceptExplanation: String,
    val codeExample: String,
    val language: String,
    val practiceChallenge: String,
    val hint: String,
    val starterCode: String,
    val solutionCode: String
)

data class CodingChallenge(
    val id: String,
    val title: String,
    val difficulty: String, // "BEGINNER", "EASY", "INTERMEDIATE", "ADVANCED"
    val language: String,
    val description: String,
    val exampleInput: String,
    val exampleOutput: String,
    val hints: List<String>,
    val starterCode: String,
    val solutionCode: String,
    val explanation: String
)

object LearningCatalog {

    val tracks = listOf(
        Pair("python", "Python Zero to Hero"),
        Pair("javascript", "JavaScript & TypeScript"),
        Pair("react", "React & Modern Web"),
        Pair("html_css", "HTML5 & Cyber CSS"),
        Pair("dsa", "Algorithms & Data Structures"),
        Pair("cpp", "C++ Systems & Memory")
    )

    val lessons = listOf(
        LearningLesson(
            id = "py_01",
            trackId = "python",
            title = "1. Variables & Dynamic Typing",
            estimatedMinutes = 5,
            summary = "Learn how Python stores integers, floats, strings, and booleans without type declarations.",
            conceptExplanation = "In Python, variables are dynamically assigned. You don't need to specify `int` or `String`. The Python interpreter infers types at runtime.",
            codeExample = """
# Python Variables & Types
user_name = "Rauf"
developer_level = 10
is_quantum_ready = True
learning_rate = 0.001

print(f"Developer: {user_name} | Level: {developer_level}")
print(f"Type of user_name: {type(user_name)}")
            """.trimIndent(),
            language = "python",
            practiceChallenge = "Create 3 variables for a cyberpunk agent (name, energy_level, is_active) and print a formatted status string.",
            hint = "Use f-strings: `f'Agent: {name}'`",
            starterCode = "# Write your solution here:\n",
            solutionCode = """
agent_name = "CyberBot"
energy_level = 95.5
is_active = True

print(f"Agent [{agent_name}] Status: Active={is_active}, Energy={energy_level}%")
            """.trimIndent()
        ),
        LearningLesson(
            id = "py_02",
            trackId = "python",
            title = "2. Functions & Decorators",
            estimatedMinutes = 8,
            summary = "Master `def`, type hints, `*args`, `**kwargs`, and function return signatures.",
            conceptExplanation = "Functions modularize code into reusable units. Using type hints like `def process(x: int) -> str:` helps prevent bugs and enhances IDE autocompletion.",
            codeExample = """
from typing import List

def calculate_code_velocity(lines_committed: List[int], days: int) -> float:
    \"\"\"Calculates average lines of code written per day.\"\"\"
    if days <= 0:
        return 0.0
    return sum(lines_committed) / days

weekly_commits = [120, 85, 340, 210, 95]
avg = calculate_code_velocity(weekly_commits, days=5)
print(f"Average LOC/day: {avg:.2f}")
            """.trimIndent(),
            language = "python",
            practiceChallenge = "Write a function `filter_critical_errors(logs: List[str]) -> List[str]` that returns only logs starting with '[CRITICAL]'.",
            hint = "Use list comprehension: `[x for x in logs if x.startswith('[CRITICAL]')]`",
            starterCode = "def filter_critical_errors(logs: list) -> list:\n    # TODO\n    pass",
            solutionCode = """
def filter_critical_errors(logs: list) -> list:
    return [log for log in logs if log.startswith("[CRITICAL]")]

test_logs = ["[INFO] Boot", "[CRITICAL] Memory leak", "[WARN] Slow query", "[CRITICAL] Port blocked"]
print(filter_critical_errors(test_logs))
            """.trimIndent()
        ),
        LearningLesson(
            id = "js_01",
            trackId = "javascript",
            title = "1. Modern ES6+ & Destructuring",
            estimatedMinutes = 6,
            summary = "Learn `const`, `let`, arrow functions, spread syntax, and object destructuring.",
            conceptExplanation = "ES6 modernized JavaScript with block-scoped bindings, concise arrow syntax, and expressive destructuring patterns.",
            codeExample = """
const developerProfile = {
  name: "Rauf",
  role: "AI Product Engineer",
  skills: ["Kotlin", "React", "Python", "TypeScript"],
  stats: { projectsBuilt: 42, commits: 1337 }
};

// Object & Array Destructuring
const { name, role, stats: { projectsBuilt } } = developerProfile;
const [primarySkill, ...otherSkills] = developerProfile.skills;

console.log(`${'$'}{name} built ${'$'}{projectsBuilt} projects using ${'$'}{primarySkill}!`);
            """.trimIndent(),
            language = "javascript",
            practiceChallenge = "Extract `title` and `apiKey` from an agent configuration object using destructuring.",
            hint = "Use `const { title, apiKey = 'default' } = config;`",
            starterCode = "const config = { title: 'AI Agent', apiKey: 'sk-123' };\n// Destructure below:\n",
            solutionCode = """
const config = { title: 'AI Agent', apiKey: 'sk-123' };
const { title, apiKey } = config;
console.log(`Configured ${'$'}{title} with key: ${'$'}{apiKey}`);
            """.trimIndent()
        ),
        LearningLesson(
            id = "react_01",
            trackId = "react",
            title = "1. Components & Custom Hooks",
            estimatedMinutes = 10,
            summary = "Understand pure components, `useState`, `useEffect`, and creating clean custom hooks.",
            conceptExplanation = "React components are pure functions of state and props. Custom hooks let you extract and reuse stateful logic cleanly across views.",
            codeExample = """
import React, { useState, useEffect } from 'react';

// Custom Hook for Terminal Typing Animation
function useTerminalTyping(fullText: string, speedMs: number = 30) {
  const [displayedText, setDisplayedText] = useState('');

  useEffect(() => {
    let index = 0;
    const interval = setInterval(() => {
      if (index < fullText.length) {
        setDisplayedText((prev) => prev + fullText.charAt(index));
        index++;
      } else {
        clearInterval(interval);
      }
    }, speedMs);
    return () => clearInterval(interval);
  }, [fullText, speedMs]);

  return displayedText;
}
            """.trimIndent(),
            language = "typescript",
            practiceChallenge = "Create a custom hook `useCounter(initialValue: number)` with `increment`, `decrement`, and `reset` functions.",
            hint = "Return an object `{ count, increment, decrement, reset }`",
            starterCode = "function useCounter(initial = 0) {\n  // TODO\n}",
            solutionCode = """
function useCounter(initial = 0) {
  const [count, setCount] = React.useState(initial);
  const increment = () => setCount(c => c + 1);
  const decrement = () => setCount(c => c - 1);
  const reset = () => setCount(initial);
  return { count, increment, decrement, reset };
}
            """.trimIndent()
        )
    )

    val challenges = listOf(
        CodingChallenge(
            id = "ch_01",
            title = "Two Sum Algorithm",
            difficulty = "BEGINNER",
            language = "python",
            description = "Given an array of integers `nums` and an integer `target`, return the indices of the two numbers that add up to `target`.",
            exampleInput = "nums = [2, 7, 11, 15], target = 9",
            exampleOutput = "[0, 1] (because 2 + 7 == 9)",
            hints = listOf(
                "Can you use a HashMap/Dictionary to store numbers you've already visited?",
                "For each number `x`, look for `target - x` in your dictionary."
            ),
            starterCode = """
def two_sum(nums: list[int], target: int) -> list[int]:
    # Write your solution below:
    pass
            """.trimIndent(),
            solutionCode = """
def two_sum(nums: list[int], target: int) -> list[int]:
    seen = {}
    for i, num in enumerate(nums):
        complement = target - num
        if complement in seen:
            return [seen[complement], i]
        seen[num] = i
    return []

# Test:
print(two_sum([2, 7, 11, 15], 9)) # Output: [0, 1]
            """.trimIndent(),
            explanation = "By using a Hash Map, we check for the required complement in O(1) time, giving an overall O(N) time complexity and O(N) space complexity."
        ),
        CodingChallenge(
            id = "ch_02",
            title = "Valid Parentheses & Syntax Validator",
            difficulty = "EASY",
            language = "typescript",
            description = "Given a string `s` containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid syntax.",
            exampleInput = "s = \"{ [ ( ) ] }\"",
            exampleOutput = "true",
            hints = listOf(
                "Use a Stack data structure.",
                "Push opening brackets onto the stack. When a closing bracket appears, check if it matches the top element."
            ),
            starterCode = """
function isValidSyntax(s: string): boolean {
  // Write your solution here:
  return false;
}
            """.trimIndent(),
            solutionCode = """
function isValidSyntax(s: string): boolean {
  const stack: string[] = [];
  const map: Record<string, string> = {
    ')': '(',
    '}': '{',
    ']': '['
  };

  for (const char of s) {
    if (char === '(' || char === '{' || char === '[') {
      stack.push(char);
    } else if (map[char]) {
      if (stack.pop() !== map[char]) {
        return false;
      }
    }
  }

  return stack.length === 0;
}
            """.trimIndent(),
            explanation = "Using a Last-In First-Out (LIFO) stack ensures every innermost bracket is closed before outer brackets."
        ),
        CodingChallenge(
            id = "ch_03",
            title = "Token Bucket Rate Limiter",
            difficulty = "INTERMEDIATE",
            language = "python",
            description = "Implement a Token Bucket Rate Limiter algorithm that refills `refill_rate` tokens per second up to `capacity`.",
            exampleInput = "capacity = 5, refill_rate = 2 tokens/sec, consume(3)",
            exampleOutput = "True (2 tokens remaining)",
            hints = listOf(
                "Keep track of the `last_refill_time` using `time.time()`.",
                "Calculate newly added tokens as `(now - last_refill_time) * refill_rate`."
            ),
            starterCode = """
import time

class TokenBucket:
    def __init__(self, capacity: int, refill_rate: float):
        self.capacity = capacity
        self.refill_rate = refill_rate
        self.tokens = capacity
        self.last_refill = time.time()

    def allow_request(self, tokens: int = 1) -> bool:
        # TODO: Implement refill and check logic
        pass
            """.trimIndent(),
            solutionCode = """
import time

class TokenBucket:
    def __init__(self, capacity: int, refill_rate: float):
        self.capacity = capacity
        self.refill_rate = refill_rate
        self.tokens = float(capacity)
        self.last_refill = time.time()

    def allow_request(self, tokens: int = 1) -> bool:
        now = time.time()
        elapsed = now - self.last_refill
        self.tokens = min(self.capacity, self.tokens + elapsed * self.refill_rate)
        self.last_refill = now

        if self.tokens >= tokens:
            self.tokens -= tokens
            return True
        return False
            """.trimIndent(),
            explanation = "The Token Bucket algorithm handles traffic bursts while enforcing strict average throughput limits."
        ),
        CodingChallenge(
            id = "ch_04",
            title = "Async Event Bus with Backpressure",
            difficulty = "ADVANCED",
            language = "typescript",
            description = "Design a TypeScript Event Bus supporting multiple subscribers, typed payloads, error isolation, and unsubscribe handlers.",
            exampleInput = "bus.on('compile', (payload) => ...); bus.emit('compile', { file: 'main.ts' })",
            exampleOutput = "Event delivered to all active listeners",
            hints = listOf(
                "Store listeners in a `Map<string, Set<Function>>`.",
                "Return an unsubscribe function from `on()`."
            ),
            starterCode = """
type Callback<T = any> = (data: T) => void | Promise<void>;

class EventBus {
  // TODO: implement on, emit, off
}
            """.trimIndent(),
            solutionCode = """
type Callback<T = any> = (data: T) => void | Promise<void>;

export class EventBus {
  private listeners = new Map<string, Set<Callback>>();

  public on<T = any>(event: string, callback: Callback<T>): () => void {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, new Set());
    }
    this.listeners.get(event)!.add(callback as Callback);

    return () => {
      this.listeners.get(event)?.delete(callback as Callback);
    };
  }

  public async emit<T = any>(event: string, data: T): Promise<void> {
    const callbacks = this.listeners.get(event);
    if (!callbacks || callbacks.size === 0) return;

    const promises = Array.from(callbacks).map(async (cb) => {
      try {
        await cb(data);
      } catch (err) {
        console.error(`[EventBus] Error in handler for '${'$'}{event}':`, err);
      }
    });

    await Promise.all(promises);
  }
}
            """.trimIndent(),
            explanation = "Isolating handler errors inside Promise.all prevents one failing subscriber from halting the execution of others."
        )
    )
}
