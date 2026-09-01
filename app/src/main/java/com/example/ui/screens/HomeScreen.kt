package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.RequestType
import com.example.ui.AppNavDestination
import com.example.ui.AppViewModel
import com.example.ui.components.CyberHeader
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
import com.example.ui.theme.TerminalPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class QuickActionCardItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val mode: RequestType,
    val defaultPrompt: String,
    val accentColor: Color
)

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    var quickQuery by remember { mutableStateOf("") }
    val selectedMode by viewModel.selectedMode.collectAsState()
    val experienceLevel by viewModel.preferencesManager.experienceLevel.collectAsState()
    val activeProvider by viewModel.preferencesManager.activeProvider.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val projects by viewModel.projects.collectAsState()

    val actionCards = remember {
        listOf(
            QuickActionCardItem(
                title = "WRITE CODE",
                description = "Generate clean, fully working code with setup instructions",
                icon = Icons.Default.Code,
                mode = RequestType.CODE,
                defaultPrompt = "Write a complete, responsive React component for a developer command palette with keyboard shortcuts.",
                accentColor = NeonGreen
            ),
            QuickActionCardItem(
                title = "EXPLAIN CODE",
                description = "Understand complex functions, algorithms & architectures",
                icon = Icons.Default.Psychology,
                mode = RequestType.EXPLAIN,
                defaultPrompt = "Explain how async/await and Promise.all work under the hood in JavaScript with a simple analogy.",
                accentColor = TerminalCyan
            ),
            QuickActionCardItem(
                title = "FIX BUG / DEBUG",
                description = "Paste error messages, logs & stack traces for instant fixes",
                icon = Icons.Default.BugReport,
                mode = RequestType.DEBUG,
                defaultPrompt = "Fix this error: TypeError: Cannot read properties of undefined (reading 'map')",
                accentColor = AlertRed
            ),
            QuickActionCardItem(
                title = "BUILD FULL PROJECT",
                description = "Scaffold complete multi-file applications & folder structures",
                icon = Icons.Default.FolderSpecial,
                mode = RequestType.PROJECT,
                defaultPrompt = "Build me a complete Python FastAPI backend with SQLite database, JWT auth, and CRUD endpoints.",
                accentColor = EmeraldGreen
            ),
            QuickActionCardItem(
                title = "GENERATE PROMPT",
                description = "Craft master system prompt specifications for Google AI Studio",
                icon = Icons.Default.AutoAwesome,
                mode = RequestType.PROMPT,
                defaultPrompt = "Give me a prompt for building a 3D interactive physics-based web game using Three.js and WebGL.",
                accentColor = TerminalPurple
            ),
            QuickActionCardItem(
                title = "LEARN CODING",
                description = "Interactive tracks, lessons, exercises & DSA roadmaps",
                icon = Icons.Default.Science,
                mode = RequestType.LEARN,
                defaultPrompt = "Teach me the Two Pointer technique in Python with step-by-step beginner examples.",
                accentColor = TerminalAmber
            ),
            QuickActionCardItem(
                title = "CODE REVIEW",
                description = "Audit security vulnerabilities, bottlenecks & clean code",
                icon = Icons.Default.DeveloperBoard,
                mode = RequestType.REVIEW,
                defaultPrompt = "Review this authentication middleware for security flaws and rate limiting vulnerabilities.",
                accentColor = NeonGreen
            ),
            QuickActionCardItem(
                title = "CONVERT CODE",
                description = "Translate logic between Python, JS, TypeScript, Kotlin, C++",
                icon = Icons.Default.SyncAlt,
                mode = RequestType.CONVERT,
                defaultPrompt = "Convert this Python data parsing script into modern idiomatic TypeScript with strict interfaces.",
                accentColor = TerminalCyan
            ),
            QuickActionCardItem(
                title = "GENERATE TESTS",
                description = "Unit tests, integration tests, mocks & edge cases",
                icon = Icons.Default.DataObject,
                mode = RequestType.TESTS,
                defaultPrompt = "Write complete Jest unit tests covering edge cases for an email and password validator.",
                accentColor = EmeraldGreen
            ),
            QuickActionCardItem(
                title = "OPTIMIZE CODE",
                description = "Refactor for maximum execution speed & memory efficiency",
                icon = Icons.Default.AutoFixHigh,
                mode = RequestType.OPTIMIZE,
                defaultPrompt = "Optimize this database query function to prevent N+1 query bottlenecks and reduce memory allocations.",
                accentColor = TerminalAmber
            )
        )
    }

    val quickPills = remember {
        listOf(
            "Build me a Python calculator",
            "Create a React login page",
            "Fix this JavaScript error",
            "Explain this code like I'm a beginner",
            "Give me a prompt for building a 3D game",
            "Create a complete HTML website"
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = CyberBlack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            CyberHeader(
                statusText = "● AI ONLINE",
                experienceLevel = experienceLevel.title,
                activeProviderName = activeProvider.displayName,
                onBrandClick = { viewModel.navigateTo(AppNavDestination.SETTINGS) }
            )

            // Mode Selector Bar
            ModeSelectorBar(
                selectedMode = selectedMode,
                onModeSelected = { viewModel.setMode(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Central Command Input Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberPanelBg)
                    .border(1.dp, CyberBorderGlow, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = quickQuery,
                            onValueChange = { quickQuery = it },
                            placeholder = {
                                Text(
                                    "Ask AI Agent (Code, Prompt, Debug, Explain)...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.weight(1f),
                            maxLines = 3
                        )

                        // Send Button
                        Button(
                            onClick = {
                                if (quickQuery.isNotBlank()) {
                                    viewModel.createNewSession(quickQuery) {
                                        viewModel.navigateTo(AppNavDestination.CHAT)
                                    }
                                    quickQuery = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen,
                                contentColor = Color(0xFF001A0D)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(44.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Prompt Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickPills.forEach { pill ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyberCardBg)
                                    .border(0.5.dp, CyberBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        viewModel.createNewSession(pill) {
                                            viewModel.navigateTo(AppNavDestination.CHAT)
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = pill,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Action Cards Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI DEVELOPER CAPABILITIES",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = NeonGreen
                )
                Text(
                    text = "10 CORE MODES",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Cards List / Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                actionCards.forEach { card ->
                    ActionCardRow(
                        item = card,
                        onClick = {
                            viewModel.setMode(card.mode)
                            viewModel.createNewSession(card.defaultPrompt) {
                                viewModel.navigateTo(AppNavDestination.CHAT)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Specialized Dev Tools Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberPanelBg)
                    .border(1.dp, CyberBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "SPECIALIZED DEVELOPER TOOLS",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = TerminalCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecializedToolButton(
                            icon = Icons.Default.Games,
                            label = "Game Dev",
                            onClick = {
                                viewModel.setMode(RequestType.GAME_DEV)
                                viewModel.createNewSession("Create a 3D player controller with WASD movement and jumping mechanics.") {
                                    viewModel.navigateTo(AppNavDestination.CHAT)
                                }
                            }
                        )
                        SpecializedToolButton(
                            icon = Icons.Default.Storage,
                            label = "Database",
                            onClick = {
                                viewModel.setMode(RequestType.DATABASE)
                                viewModel.createNewSession("Design a normalized PostgreSQL schema for a multi-tenant SaaS application.") {
                                    viewModel.navigateTo(AppNavDestination.CHAT)
                                }
                            }
                        )
                        SpecializedToolButton(
                            icon = Icons.Default.Hub,
                            label = "Git Helper",
                            onClick = {
                                viewModel.setMode(RequestType.GIT)
                                viewModel.createNewSession("How do I safely resolve a git merge conflict and undo the last 2 commits without losing work?") {
                                    viewModel.navigateTo(AppNavDestination.CHAT)
                                }
                            }
                        )
                        SpecializedToolButton(
                            icon = Icons.Default.Terminal,
                            label = "Terminal",
                            onClick = {
                                viewModel.setMode(RequestType.TERMINAL)
                                viewModel.createNewSession("Provide a bash script to monitor memory usage and alert when free RAM drops below 10%.") {
                                    viewModel.navigateTo(AppNavDestination.CHAT)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Stats Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberCardBg)
                    .border(0.8.dp, CyberBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatBadge(label = "ACTIVE SESSIONS", value = "${sessions.size}")
                    StatBadge(label = "LOCAL PROJECTS", value = "${projects.size}")
                    StatBadge(label = "ENGINE", value = activeProvider.id.uppercase())
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ActionCardRow(
    item: QuickActionCardItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberCardBg)
            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberDarkEmerald)
                    .border(0.8.dp, item.accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Execute",
                tint = NeonGreen.copy(alpha = 0.8f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SpecializedToolButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(CyberCardElevated)
                .border(1.dp, CyberBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = NeonGreen,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextSecondary
        )
    }
}

@Composable
fun StatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = NeonGreen)
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextMuted)
    }
}
