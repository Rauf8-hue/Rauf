package com.example.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.ChatMessageEntity
import com.example.data.models.ChatSessionEntity
import com.example.data.models.LearningProgressEntity
import com.example.data.models.ProjectEntity
import com.example.data.models.ProjectFileEntity
import com.example.data.models.SavedPromptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_sessions ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): ChatSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity): Long

    @Update
    suspend fun updateSession(session: ChatSessionEntity)

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun clearMessages(sessionId: Long)

    @Query("UPDATE chat_sessions SET isPinned = NOT isPinned WHERE id = :sessionId")
    suspend fun togglePinSession(sessionId: Long)

    @Query("UPDATE chat_sessions SET title = :newTitle, updatedAt = :timestamp WHERE id = :sessionId")
    suspend fun renameSession(sessionId: Long, newTitle: String, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectById(projectId: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProject(projectId: Long)

    @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY path ASC")
    fun getFilesForProject(projectId: Long): Flow<List<ProjectFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: ProjectFileEntity): Long

    @Update
    suspend fun updateFile(file: ProjectFileEntity)

    @Query("DELETE FROM project_files WHERE id = :fileId")
    suspend fun deleteFile(fileId: Long)

    @Query("UPDATE projects SET memoryNotes = :notes, updatedAt = :timestamp WHERE id = :projectId")
    suspend fun updateProjectMemory(projectId: Long, notes: String, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface LearningDao {
    @Query("SELECT * FROM learning_progress")
    fun getAllProgress(): Flow<List<LearningProgressEntity>>

    @Query("SELECT * FROM learning_progress WHERE trackId = :trackId")
    fun getProgressForTrack(trackId: String): Flow<List<LearningProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: LearningProgressEntity)

    @Query("DELETE FROM learning_progress WHERE trackId = :trackId")
    suspend fun resetTrack(trackId: String)
}

@Dao
interface SavedPromptDao {
    @Query("SELECT * FROM saved_prompts ORDER BY createdAt DESC")
    fun getAllPrompts(): Flow<List<SavedPromptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: SavedPromptEntity): Long

    @Query("DELETE FROM saved_prompts WHERE id = :promptId")
    suspend fun deletePrompt(promptId: Long)
}
