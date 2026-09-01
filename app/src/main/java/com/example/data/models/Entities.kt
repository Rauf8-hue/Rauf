package com.example.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val defaultMode: String = "AUTO",
    val providerId: String = "gemini",
    val modelId: String = "gemini-3.5-flash"
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val requestType: String = "CODE", // "CODE", "PROMPT", "EXPLAIN", "DEBUG", "PROJECT", "LEARN", "REVIEW", "OPTIMIZE", "CONVERT", "TESTS"
    val timestamp: Long = System.currentTimeMillis(),
    val codeSnippet: String? = null,
    val language: String? = null,
    val promptGenerated: String? = null,
    val tokenUsage: String? = null,
    val isError: Boolean = false
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val techStack: String, // e.g. "React + TypeScript", "Python FastAPI", "Kotlin Android"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val memoryNotes: String = "" // AI Project Memory
)

@Entity(
    tableName = "project_files",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class ProjectFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val path: String, // e.g. "src/App.tsx"
    val name: String, // e.g. "App.tsx"
    val content: String,
    val language: String, // "typescript", "python", "html", etc.
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_prompts")
data class SavedPromptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Web", "Game Dev", "Architecture", "AI Studio", "Mobile", "Backend"
    val targetTool: String, // "Google AI Studio", "Gemini", "Coding Agent"
    val promptText: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "learning_progress")
data class LearningProgressEntity(
    @PrimaryKey val lessonId: String,
    val trackId: String, // "python", "javascript", "react", "html_css", "dsa", "cpp"
    val isCompleted: Boolean = false,
    val score: Int = 0,
    val completedAt: Long = System.currentTimeMillis()
)
