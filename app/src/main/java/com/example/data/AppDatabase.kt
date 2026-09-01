package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.daos.ChatDao
import com.example.data.daos.LearningDao
import com.example.data.daos.ProjectDao
import com.example.data.daos.SavedPromptDao
import com.example.data.models.ChatMessageEntity
import com.example.data.models.ChatSessionEntity
import com.example.data.models.LearningProgressEntity
import com.example.data.models.ProjectEntity
import com.example.data.models.ProjectFileEntity
import com.example.data.models.SavedPromptEntity

@Database(
    entities = [
        ChatSessionEntity::class,
        ChatMessageEntity::class,
        ProjectEntity::class,
        ProjectFileEntity::class,
        SavedPromptEntity::class,
        LearningProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun projectDao(): ProjectDao
    abstract fun learningDao(): LearningDao
    abstract fun savedPromptDao(): SavedPromptDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ai_agent_rauf_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
