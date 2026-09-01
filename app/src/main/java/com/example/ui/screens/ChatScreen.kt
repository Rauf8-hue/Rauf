package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.RequestType
import com.example.data.models.ChatMessageEntity
import com.example.data.models.ChatSessionEntity
import com.example.ui.AppNavDestination
import com.example.ui.AppViewModel
import com.example.ui.components.CyberHeader
import com.example.ui.components.MarkdownViewer
import com.example.ui.components.ModeSelectorBar
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGlow
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberDarkEmerald
import com.example.ui.theme.CyberPanelBg
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TerminalAmber
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val messages by viewModel.currentMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val streamingText by viewModel.streamingText.collectAsState()
    val activeStreamingIntent by viewModel.activeStreamingIntent.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    val experienceLevel by viewModel.preferencesManager.experienceLevel.collectAsState()
    val activeProvider by viewModel.preferencesManager.activeProvider.collectAsState()
    val fontSizeSp by viewModel.preferencesManager.codeFontSize.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val activeSessionId by viewModel.activeSessionId.collectAsState()

    var showHistorySheet by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf<ChatSessionEntity?>(null) }
    var renameInput by remember { mutableStateOf("") }
    var showVoiceConfirmDialog by remember { mutableStateOf(false) }
    var voiceTranscript by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // Speech-to-Text Activity Result Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                voiceTranscript = spoken
                showVoiceConfirmDialog = true
            }
        }
    }

    // Scroll to bottom when new messages arrive or streaming updates
    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty() || streamingText.isNotBlank()) {
            listState.animateScrollToItem((messages.size + if (isGenerating) 1 else 0).coerceAtLeast(0))
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = CyberBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Session Title and History button
            CyberHeader(
                statusText = if (isGenerating) "● GENERATING..." else "● AI ONLINE",
                experienceLevel = experienceLevel.title,
                activeProviderName = activeProvider.displayName,
                onBrandClick = { showHistorySheet = true }
            )

            // Mode Selector Bar
            ModeSelectorBar(
                selectedMode = selectedMode,
                onModeSelected = { viewModel.setMode(it) }
            )

            // Chat Session Subheader Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberPanelBg)
                    .border(0.5.dp, CyberBorder, RoundedCornerShape(0.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val activeSession = sessions.firstOrNull { it.id == activeSessionId }
                    Text(
                        text = activeSession?.title ?: "New Session",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { viewModel.createNewSession() }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "New Session", tint = NeonGreen, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = { showHistorySheet = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = TerminalCyan, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = { viewModel.clearActiveChat() }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Message List Area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (messages.isEmpty() && !isGenerating) {
                    item {
                        EmptyChatState(
                            onQuickPrompt = { prompt, mode ->
                                viewModel.setMode(mode)
                                viewModel.sendChatMessage(prompt)
                            }
                        )
                    }
                }

                items(messages) { message ->
                    ChatMessageBubble(
                        message = message,
                        fontSizeSp = fontSizeSp,
                        onCopyCode = { code -> viewModel.copyToClipboard(code) },
                        onSaveCode = { code, lang ->
                            viewModel.addFileToActiveProject("src/generated_${System.currentTimeMillis() % 10000}.$lang", code, lang)
                            Toast.makeText(context, "Saved to Project Workspace!", Toast.LENGTH_SHORT).show()
                        },
                        onExplainCode = { code ->
                            viewModel.sendChatMessage("Explain this code:\n```\n$code\n```", RequestType.EXPLAIN)
                        },
                        onFixCode = { code ->
                            viewModel.sendChatMessage("Fix bugs and errors in this code:\n```\n$code\n```", RequestType.DEBUG)
                        },
                        onOptimizeCode = { code ->
                            viewModel.sendChatMessage("Optimize this code for performance:\n```\n$code\n```", RequestType.OPTIMIZE)
                        },
                        onRunInstructions = { code ->
                            viewModel.sendChatMessage("Give me exact step-by-step terminal run instructions for this code:\n```\n$code\n```", RequestType.TERMINAL)
                        }
                    )
                }

                // Live Streaming Bubble
                if (isGenerating) {
                    item {
                        AssistantStreamingBubble(
                            text = streamingText,
                            fontSizeSp = fontSizeSp,
                            intent = activeStreamingIntent,
                            onCopyCode = { code -> viewModel.copyToClipboard(code) }
                        )
                    }
                }
            }

            // Bottom Input Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberPanelBg)
                    .border(1.dp, CyberBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice Coding Button
                    IconButton(
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak coding command for AI Agent...")
                            }
                            try {
                                speechLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Speech recognition not available on device", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CyberCardBg)
                            .border(0.8.dp, CyberBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Coding",
                            tint = NeonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Text Input Field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "Command AI Agent (${selectedMode.label})...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CyberCardBg,
                            unfocusedContainerColor = CyberCardBg
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 4,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    Button(
                        onClick = {
                            if (inputText.isNotBlank() && !isGenerating) {
                                val textToSend = inputText
                                inputText = ""
                                viewModel.sendChatMessage(textToSend)
                            }
                        },
                        enabled = inputText.isNotBlank() && !isGenerating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor = Color(0xFF001A0D),
                            disabledContainerColor = CyberCardElevated,
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(44.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = NeonGreen
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Voice Confirmation Dialog
    if (showVoiceConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceConfirmDialog = false },
            title = {
                Text(
                    text = "🎙 Voice Command Detected",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NeonGreen
                )
            },
            text = {
                Column {
                    Text(
                        text = "Review and edit before executing:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = voiceTranscript,
                        onValueChange = { voiceTranscript = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showVoiceConfirmDialog = false
                        if (voiceTranscript.isNotBlank()) {
                            viewModel.sendChatMessage(voiceTranscript)
                            voiceTranscript = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D))
                ) {
                    Text("EXECUTE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showVoiceConfirmDialog = false }) {
                    Text("CANCEL", color = TextMuted)
                }
            },
            containerColor = CyberPanelBg,
            shape = RoundedCornerShape(14.dp)
        )
    }

    // History Sheet Dialog
    if (showHistorySheet) {
        HistoryModalSheet(
            sessions = sessions,
            activeSessionId = activeSessionId,
            onSelectSession = {
                viewModel.selectSession(it)
                showHistorySheet = false
            },
            onNewSession = {
                viewModel.createNewSession()
                showHistorySheet = false
            },
            onTogglePin = { viewModel.togglePinSession(it) },
            onRename = { session ->
                showRenameDialog = session
                renameInput = session.title
            },
            onDelete = { viewModel.deleteSession(it) },
            onDismiss = { showHistorySheet = false }
        )
    }

    // Rename Session Dialog
    if (showRenameDialog != null) {
        val s = showRenameDialog!!
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text("Rename Coding Session", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (renameInput.isNotBlank()) {
                            viewModel.renameSession(s.id, renameInput)
                        }
                        showRenameDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D))
                ) {
                    Text("SAVE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = null }) { Text("CANCEL", color = TextMuted) }
            },
            containerColor = CyberPanelBg
        )
    }
}

@Composable
fun EmptyChatState(
    onQuickPrompt: (String, RequestType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(CyberDarkEmerald)
                .border(1.dp, NeonGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                tint = NeonGreen,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "AI CODING WORKSTATION",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextPrimary
        )
        Text(
            text = "Select a quick action or enter your command below:",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickStartOption(
                title = "Write Python REST API with SQLite",
                badge = "</> CODE",
                onClick = { onQuickPrompt("Write a complete Python FastAPI REST service with SQLite database and CRUD endpoints.", RequestType.CODE) }
            )
            QuickStartOption(
                title = "Craft AI Studio Master Prompt for 3D Game",
                badge = "✦ PROMPT",
                onClick = { onQuickPrompt("Give me a prompt for building a 3D interactive physics-based web game using Three.js.", RequestType.PROMPT) }
            )
            QuickStartOption(
                title = "Fix JavaScript 'undefined is not a function'",
                badge = "🐞 DEBUG",
                onClick = { onQuickPrompt("Fix this bug: TypeError: undefined is not a function at Object.dispatch (store.js:42)", RequestType.DEBUG) }
            )
            QuickStartOption(
                title = "Explain React useEffect & dependency array",
                badge = "🧠 EXPLAIN",
                onClick = { onQuickPrompt("Explain how React useEffect cleanup and dependency array work with a simple analogy.", RequestType.EXPLAIN) }
            )
        }
    }
}

@Composable
fun QuickStartOption(title: String, badge: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CyberCardBg)
            .border(0.8.dp, CyberBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(CyberDarkEmerald)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = badge, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = NeonGreen)
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessageEntity,
    fontSizeSp: Int,
    onCopyCode: (String) -> Unit,
    onSaveCode: ((String, String) -> Unit)? = null,
    onExplainCode: ((String) -> Unit)? = null,
    onFixCode: ((String) -> Unit)? = null,
    onOptimizeCode: ((String) -> Unit)? = null,
    onRunInstructions: ((String) -> Unit)? = null
) {
    val isUser = message.role == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Author Tag
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isUser) "YOU" else "AI AGENT",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = if (isUser) NeonGreen else TerminalCyan
            )
            if (!isUser && message.requestType.isNotBlank()) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(CyberDarkEmerald)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "[ ${message.requestType} ]",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                        color = NeonGreen
                    )
                }
            }
        }

        // Bubble Body
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 12.dp
                    )
                )
                .background(if (isUser) Color(0xFF072416) else CyberPanelBg)
                .border(
                    width = 1.dp,
                    color = if (isUser) NeonGreen.copy(alpha = 0.5f) else CyberBorder,
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 12.dp
                    )
                )
                .padding(12.dp)
        ) {
            MarkdownViewer(
                content = message.content,
                fontSizeSp = fontSizeSp,
                onCopyCode = onCopyCode,
                onSaveCode = onSaveCode,
                onExplainCode = onExplainCode,
                onFixCode = onFixCode,
                onOptimizeCode = onOptimizeCode,
                onRunInstructions = onRunInstructions
            )
        }
    }
}

@Composable
fun AssistantStreamingBubble(
    text: String,
    fontSizeSp: Int,
    intent: com.example.ai.IntentAnalysis? = null,
    onCopyCode: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(10.dp),
                strokeWidth = 1.5.dp,
                color = NeonGreen
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = intent?.getFormattedModeIndicator() ?: "AI AGENT [ STREAMING ]",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = NeonGreen
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CyberPanelBg)
                .border(1.dp, NeonGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            MarkdownViewer(
                content = text.ifBlank { "Processing request..." },
                fontSizeSp = fontSizeSp,
                onCopyCode = onCopyCode
            )
        }
    }
}

@Composable
fun HistoryModalSheet(
    sessions: List<ChatSessionEntity>,
    activeSessionId: Long?,
    onSelectSession: (Long) -> Unit,
    onNewSession: () -> Unit,
    onTogglePin: (Long) -> Unit,
    onRename: (ChatSessionEntity) -> Unit,
    onDelete: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Coding Sessions History",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NeonGreen
                )
                IconButton(onClick = onNewSession, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "New Session", tint = NeonGreen)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (sessions.isEmpty()) {
                    item {
                        Text(
                            text = "No recorded sessions yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
                items(sessions) { session ->
                    val isActive = session.id == activeSessionId

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isActive) CyberDarkEmerald else CyberCardBg)
                            .border(1.dp, if (isActive) NeonGreen else CyberBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelectSession(session.id) }
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (session.isPinned) {
                                        Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = TerminalAmber, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = session.title,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = "Provider: ${session.providerId}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = TextMuted
                                )
                            }

                            Row {
                                IconButton(onClick = { onTogglePin(session.id) }, modifier = Modifier.size(24.dp)) {
                                    Icon(
                                        imageVector = if (session.isPinned) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Pin",
                                        tint = if (session.isPinned) TerminalAmber else TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                IconButton(onClick = { onRename(session) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Rename", tint = TextMuted, modifier = Modifier.size(14.dp))
                                }
                                IconButton(onClick = { onDelete(session.id) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyberDarkEmerald, contentColor = NeonGreen)
            ) {
                Text("CLOSE")
            }
        },
        containerColor = CyberPanelBg,
        shape = RoundedCornerShape(16.dp)
    )
}
