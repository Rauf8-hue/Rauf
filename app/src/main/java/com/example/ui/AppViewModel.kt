package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AIProviderManager
import com.example.ai.AIRequest
import com.example.ai.ConnectionTestResult
import com.example.ai.RequestClassifier
import com.example.ai.RequestType
import com.example.data.AIProviderType
import com.example.data.AppDatabase
import com.example.data.DeveloperExperienceLevel
import com.example.data.LearningCatalog
import com.example.data.LearningLesson
import com.example.data.CodingChallenge
import com.example.data.PreferencesManager
import com.example.data.models.ChatMessageEntity
import com.example.data.models.ChatSessionEntity
import com.example.data.models.LearningProgressEntity
import com.example.data.models.ProjectEntity
import com.example.data.models.ProjectFileEntity
import com.example.data.models.SavedPromptEntity
import com.example.ui.theme.AppThemePreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppNavDestination {
    HOME,
    CHAT,
    PROJECTS,
    LEARN,
    SETTINGS
}

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val preferencesManager = PreferencesManager(application)
    val providerManager = AIProviderManager(preferencesManager)

    // Navigation State
    private val _currentDestination = MutableStateFlow(AppNavDestination.HOME)
    val currentDestination: StateFlow<AppNavDestination> = _currentDestination.asStateFlow()

    private val _showOnboarding = MutableStateFlow(!preferencesManager.isFirstLaunchCompleted())
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    // Mode Selector State
    private val _selectedMode = MutableStateFlow(RequestType.AUTO)
    val selectedMode: StateFlow<RequestType> = _selectedMode.asStateFlow()

    // Active Chat Session
    private val _activeSessionId = MutableStateFlow<Long?>(null)
    val activeSessionId: StateFlow<Long?> = _activeSessionId.asStateFlow()

    val sessions: StateFlow<List<ChatSessionEntity>> = db.chatDao().getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val currentMessages: StateFlow<List<ChatMessageEntity>> = _currentMessages.asStateFlow()

    // AI Generation State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText: StateFlow<String> = _streamingText.asStateFlow()

    private val _activeStreamingIntent = MutableStateFlow<com.example.ai.IntentAnalysis?>(null)
    val activeStreamingIntent: StateFlow<com.example.ai.IntentAnalysis?> = _activeStreamingIntent.asStateFlow()

    // Connection Test State
    private val _connectionTestResult = MutableStateFlow<ConnectionTestResult?>(null)
    val connectionTestResult: StateFlow<ConnectionTestResult?> = _connectionTestResult.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

    // Projects Workspace State
    val projects: StateFlow<List<ProjectEntity>> = db.projectDao().getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeProjectId = MutableStateFlow<Long?>(null)
    val activeProjectId: StateFlow<Long?> = _activeProjectId.asStateFlow()

    private val _projectFiles = MutableStateFlow<List<ProjectFileEntity>>(emptyList())
    val projectFiles: StateFlow<List<ProjectFileEntity>> = _projectFiles.asStateFlow()

    private val _selectedFile = MutableStateFlow<ProjectFileEntity?>(null)
    val selectedFile: StateFlow<ProjectFileEntity?> = _selectedFile.asStateFlow()

    // Saved Prompts & Learning
    val savedPrompts: StateFlow<List<SavedPromptEntity>> = db.savedPromptDao().getAllPrompts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val learningProgress: StateFlow<List<LearningProgressEntity>> = db.learningDao().getAllProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedLesson = MutableStateFlow<LearningLesson?>(null)
    val selectedLesson: StateFlow<LearningLesson?> = _selectedLesson.asStateFlow()

    private val _selectedChallenge = MutableStateFlow<CodingChallenge?>(null)
    val selectedChallenge: StateFlow<CodingChallenge?> = _selectedChallenge.asStateFlow()

    // Voice / Attachment State
    private val _attachedImageBase64 = MutableStateFlow<String?>(null)
    val attachedImageBase64: StateFlow<String?> = _attachedImageBase64.asStateFlow()

    init {
        initDefaultSampleData()
        observeActiveSession()
        observeActiveProject()
    }

    private fun observeActiveSession() {
        viewModelScope.launch {
            _activeSessionId.collectLatest { sessionId ->
                if (sessionId != null) {
                    db.chatDao().getMessagesForSession(sessionId).collectLatest { msgs ->
                        _currentMessages.value = msgs
                    }
                } else {
                    _currentMessages.value = emptyList()
                }
            }
        }
    }

    private fun observeActiveProject() {
        viewModelScope.launch {
            _activeProjectId.collectLatest { pId ->
                if (pId != null) {
                    db.projectDao().getFilesForProject(pId).collectLatest { files ->
                        _projectFiles.value = files
                        if (_selectedFile.value != null) {
                            _selectedFile.value = files.firstOrNull { it.id == _selectedFile.value?.id }
                        }
                    }
                } else {
                    _projectFiles.value = emptyList()
                    _selectedFile.value = null
                }
            }
        }
    }

    private fun initDefaultSampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Seed a default project if empty
            val existing = db.projectDao().getProjectById(1)
            if (existing == null) {
                val pId = db.projectDao().insertProject(
                    ProjectEntity(
                        name = "AI Developer Command Hub",
                        description = "Next-generation developer workbench with multi-file architecture.",
                        techStack = "React + TypeScript + Vite",
                        memoryNotes = "Preferred styling: Cyberpunk dark theme. Use strict TypeScript interfaces."
                    )
                )
                db.projectDao().insertFile(
                    ProjectFileEntity(
                        projectId = pId,
                        path = "src/App.tsx",
                        name = "App.tsx",
                        content = """
import React, { useState } from 'react';
import { TerminalHeader } from './components/TerminalHeader';

export default function App() {
  const [status, setStatus] = useState('● AI ONLINE');

  return (
    <div className="command-center">
      <TerminalHeader title="AI AGENT FOR DEVELOPERS" author="by Rauf" />
      <div className="status-badge">{status}</div>
    </div>
  );
}
                        """.trimIndent(),
                        language = "typescript"
                    )
                )
                db.projectDao().insertFile(
                    ProjectFileEntity(
                        projectId = pId,
                        path = "src/components/TerminalHeader.tsx",
                        name = "TerminalHeader.tsx",
                        content = """
import React from 'react';

interface Props {
  title: string;
  author: string;
}

export const TerminalHeader: React.FC<Props> = ({ title, author }) => (
  <header className="terminal-header">
    <h2>{title}</h2>
    <span className="author-tag">{author}</span>
  </header>
);
                        """.trimIndent(),
                        language = "typescript"
                    )
                )
                _activeProjectId.value = pId
            }
        }
    }

    fun navigateTo(destination: AppNavDestination) {
        _currentDestination.value = destination
    }

    fun setOnboardingCompleted() {
        preferencesManager.setFirstLaunchCompleted(true)
        _showOnboarding.value = false
    }

    fun setMode(mode: RequestType) {
        _selectedMode.value = mode
    }

    fun setSelectedLesson(lesson: LearningLesson?) {
        _selectedLesson.value = lesson
    }

    fun setSelectedChallenge(challenge: CodingChallenge?) {
        _selectedChallenge.value = challenge
    }

    fun markLessonCompleted(lessonId: String, trackId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.learningDao().saveProgress(
                LearningProgressEntity(
                    lessonId = lessonId,
                    trackId = trackId,
                    isCompleted = true,
                    score = 100
                )
            )
        }
    }

    fun setAttachedImage(base64: String?) {
        _attachedImageBase64.value = base64
    }

    fun selectProject(projectId: Long?) {
        _activeProjectId.value = projectId
    }

    fun selectFile(file: ProjectFileEntity?) {
        _selectedFile.value = file
    }

    fun createNewSession(initialPrompt: String? = null, onCreated: ((Long) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val title = if (!initialPrompt.isNullOrBlank()) {
                initialPrompt.take(28) + if (initialPrompt.length > 28) "..." else ""
            } else {
                "Coding Session #${System.currentTimeMillis() % 1000}"
            }
            val newId = db.chatDao().insertSession(
                ChatSessionEntity(
                    title = title,
                    providerId = preferencesManager.getActiveProvider().id,
                    modelId = preferencesManager.getModelForProvider(preferencesManager.getActiveProvider().id)
                )
            )
            _activeSessionId.value = newId
            withContext(Dispatchers.Main) {
                onCreated?.invoke(newId)
            }
            if (!initialPrompt.isNullOrBlank()) {
                sendChatMessage(initialPrompt)
            }
        }
    }

    fun selectSession(sessionId: Long) {
        _activeSessionId.value = sessionId
        _currentDestination.value = AppNavDestination.CHAT
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.chatDao().deleteSession(sessionId)
            if (_activeSessionId.value == sessionId) {
                _activeSessionId.value = null
            }
        }
    }

    fun renameSession(sessionId: Long, newTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.chatDao().renameSession(sessionId, newTitle)
        }
    }

    fun togglePinSession(sessionId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.chatDao().togglePinSession(sessionId)
        }
    }

    fun clearActiveChat() {
        val sId = _activeSessionId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            db.chatDao().clearMessages(sId)
        }
    }

    fun sendChatMessage(promptText: String, overrideMode: RequestType? = null) {
        if (promptText.isBlank()) return

        val targetMode = overrideMode ?: _selectedMode.value
        val intentAnalysis = RequestClassifier.analyzeIntent(promptText, targetMode)
        val imageBase64 = _attachedImageBase64.value
        _attachedImageBase64.value = null // consume

        viewModelScope.launch {
            var sId = _activeSessionId.value
            if (sId == null) {
                val newTitle = promptText.take(28) + if (promptText.length > 28) "..." else ""
                sId = db.chatDao().insertSession(
                    ChatSessionEntity(
                        title = newTitle,
                        providerId = preferencesManager.getActiveProvider().id,
                        modelId = preferencesManager.getModelForProvider(preferencesManager.getActiveProvider().id)
                    )
                )
                _activeSessionId.value = sId
            }

            // Save user message
            val userMsgId = db.chatDao().insertMessage(
                ChatMessageEntity(
                    sessionId = sId,
                    role = "user",
                    content = promptText,
                    requestType = intentAnalysis.displayBadge
                )
            )

            // Gather context
            val history = _currentMessages.value.map { Pair(it.role, it.content) }
            val activeProject = _activeProjectId.value?.let { db.projectDao().getProjectById(it) }
            val activeFile = _selectedFile.value

            val aiRequest = AIRequest(
                prompt = promptText,
                requestType = intentAnalysis.requestType,
                experienceLevel = preferencesManager.getExperienceLevel().name,
                projectContext = activeProject?.memoryNotes,
                currentFileContent = activeFile?.content,
                currentFileName = activeFile?.name,
                attachedImageBase64 = imageBase64,
                chatHistory = history,
                intentAnalysis = intentAnalysis
            )

            _isGenerating.value = true
            _streamingText.value = ""
            _activeStreamingIntent.value = intentAnalysis

            try {
                var finalOutput = ""
                providerManager.streamAIResponse(aiRequest).collect { chunk ->
                    _streamingText.value = chunk.text
                    finalOutput = chunk.text
                }

                // Save assistant message to Room
                db.chatDao().insertMessage(
                    ChatMessageEntity(
                        sessionId = sId,
                        role = "assistant",
                        content = finalOutput,
                        requestType = intentAnalysis.displayBadge
                    )
                )
                db.chatDao().updateSession(
                    ChatSessionEntity(
                        id = sId,
                        title = _currentMessages.value.firstOrNull()?.content?.take(28) ?: "Coding Session",
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                db.chatDao().insertMessage(
                    ChatMessageEntity(
                        sessionId = sId,
                        role = "assistant",
                        content = "⚠️ AI Connection encountered an error: ${e.message}\nCheck your API key in Settings or use Built-in mode.",
                        isError = true,
                        requestType = intentAnalysis.displayBadge
                    )
                )
            } finally {
                _isGenerating.value = false
                _streamingText.value = ""
                _activeStreamingIntent.value = null
            }
        }
    }

    fun testProviderConnection(provider: AIProviderType, customKey: String? = null, customModel: String? = null) {
        viewModelScope.launch {
            _isTestingConnection.value = true
            _connectionTestResult.value = null
            val result = providerManager.testConnection(provider, customKey, customModel)
            _connectionTestResult.value = result
            _isTestingConnection.value = false
        }
    }

    fun copyToClipboard(text: String, label: String = "Code") {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(getApplication(), "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun createNewProject(name: String, techStack: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val pId = db.projectDao().insertProject(
                ProjectEntity(
                    name = name,
                    description = description,
                    techStack = techStack,
                    memoryNotes = "Tech: $techStack. Requirements: $description"
                )
            )
            // Create default starter file
            db.projectDao().insertFile(
                ProjectFileEntity(
                    projectId = pId,
                    path = "src/main.ts",
                    name = "main.ts",
                    content = "// Entry point for $name\nconsole.log('$name initialized');\n",
                    language = "typescript"
                )
            )
            _activeProjectId.value = pId
        }
    }

    fun addFileToActiveProject(path: String, content: String, language: String) {
        val pId = _activeProjectId.value ?: return
        val fileName = path.substringAfterLast("/")
        viewModelScope.launch(Dispatchers.IO) {
            val fileId = db.projectDao().insertFile(
                ProjectFileEntity(
                    projectId = pId,
                    path = path,
                    name = fileName,
                    content = content,
                    language = language
                )
            )
        }
    }

    fun saveFileContent(fileId: Long, newContent: String) {
        val current = _selectedFile.value ?: return
        if (current.id == fileId) {
            viewModelScope.launch(Dispatchers.IO) {
                db.projectDao().updateFile(current.copy(content = newContent, updatedAt = System.currentTimeMillis()))
            }
        }
    }

    fun deleteFile(fileId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.projectDao().deleteFile(fileId)
            if (_selectedFile.value?.id == fileId) {
                _selectedFile.value = null
            }
        }
    }

    fun updateProjectMemory(notes: String) {
        val pId = _activeProjectId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            db.projectDao().updateProjectMemory(pId, notes)
        }
    }
}
