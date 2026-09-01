package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.RequestType
import com.example.data.models.ProjectEntity
import com.example.data.models.ProjectFileEntity
import com.example.ui.AppNavDestination
import com.example.ui.AppViewModel
import com.example.ui.components.CodeBlockView
import com.example.ui.components.CyberHeader
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
import com.example.ui.theme.TextCode
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProjectsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val projects by viewModel.projects.collectAsState()
    val activeProjectId by viewModel.activeProjectId.collectAsState()
    val projectFiles by viewModel.projectFiles.collectAsState()
    val selectedFile by viewModel.selectedFile.collectAsState()
    val experienceLevel by viewModel.preferencesManager.experienceLevel.collectAsState()
    val activeProvider by viewModel.preferencesManager.activeProvider.collectAsState()
    val fontSizeSp by viewModel.preferencesManager.codeFontSize.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Files & Editor, 1: AI Project Memory, 2: Project Switcher
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showAddFileDialog by remember { mutableStateOf(false) }
    var isEditingContent by remember { mutableStateOf(false) }
    var editedFileText by remember { mutableStateOf("") }
    var newProjectName by remember { mutableStateOf("") }
    var newProjectTech by remember { mutableStateOf("React + TypeScript + Vite") }
    var newProjectDesc by remember { mutableStateOf("") }
    var newFilePath by remember { mutableStateOf("") }
    var newFileLang by remember { mutableStateOf("typescript") }

    val activeProject = projects.firstOrNull { it.id == activeProjectId }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = CyberBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            CyberHeader(
                statusText = "● WORKSPACE ACTIVE",
                experienceLevel = experienceLevel.title,
                activeProviderName = activeProvider.displayName
            )

            // Tabs: Files | AI Memory | Projects
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CyberPanelBg,
                contentColor = NeonGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = NeonGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            "FILES & CODE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            "AI MEMORY",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Text(
                            "PROJECTS (${projects.size})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )
            }

            when (selectedTabIndex) {
                0 -> {
                    // Files & Editor Pane
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        // Project Title & Add File Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = activeProject?.name ?: "Default Workspace",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = activeProject?.techStack ?: "TypeScript",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldGreen
                                )
                            }

                            Row {
                                Button(
                                    onClick = { showAddFileDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyberDarkEmerald,
                                        contentColor = NeonGreen
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                        horizontal = 10.dp,
                                        vertical = 4.dp
                                    )
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ADD FILE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // File Tree / Selector Pills
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberCardBg)
                                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                                .padding(6.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                            ) {
                                items(projectFiles) { file ->
                                    val isSelected = selectedFile?.id == file.id

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) CyberDarkEmerald else Color.Transparent)
                                            .clickable {
                                                viewModel.selectFile(file)
                                                editedFileText = file.content
                                                isEditingContent = false
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                tint = if (isSelected) NeonGreen else TextMuted,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = file.path,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isSelected) TextPrimary else TextSecondary
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteFile(file.id) },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete File",
                                                tint = AlertRed.copy(alpha = 0.6f),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Active File Content Viewer & Editor
                        if (selectedFile != null) {
                            val f = selectedFile!!

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                // Editor Toolbar
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CyberPanelBg)
                                        .border(0.5.dp, CyberBorder, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = f.path,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                        color = NeonGreen
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        if (isEditingContent) {
                                            Button(
                                                onClick = {
                                                    viewModel.saveFileContent(f.id, editedFileText)
                                                    isEditingContent = false
                                                    Toast.makeText(context, "Saved file!", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D)),
                                                shape = RoundedCornerShape(4.dp),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(26.dp)
                                            ) {
                                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("SAVE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                                            }
                                        } else {
                                            IconButton(
                                                onClick = {
                                                    editedFileText = f.content
                                                    isEditingContent = true
                                                },
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TerminalCyan, modifier = Modifier.size(14.dp))
                                            }
                                        }

                                        // Quick AI Actions on this File
                                        IconButton(
                                            onClick = {
                                                viewModel.createNewSession("Explain this file `${f.path}`:\n```\n${f.content}\n```") {
                                                    viewModel.navigateTo(AppNavDestination.CHAT)
                                                }
                                            },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Icon(Icons.Default.Psychology, contentDescription = "Explain File", tint = TerminalAmber, modifier = Modifier.size(14.dp))
                                        }
                                        IconButton(
                                            onClick = {
                                                viewModel.createNewSession("Fix bugs and optimize `${f.path}`:\n```\n${f.content}\n```") {
                                                    viewModel.navigateTo(AppNavDestination.CHAT)
                                                }
                                            },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Icon(Icons.Default.BugReport, contentDescription = "Fix File", tint = AlertRed, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }

                                if (isEditingContent) {
                                    OutlinedTextField(
                                        value = editedFileText,
                                        onValueChange = { editedFileText = it },
                                        textStyle = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = fontSizeSp.sp,
                                            lineHeight = (fontSizeSp + 6).sp
                                        ),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NeonGreen,
                                            unfocusedBorderColor = CyberBorder,
                                            focusedTextColor = TextCode,
                                            unfocusedTextColor = TextCode,
                                            focusedContainerColor = CyberBlack,
                                            unfocusedContainerColor = CyberBlack
                                        ),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(top = 2.dp)
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        CodeBlockView(
                                            code = f.content,
                                            language = f.language,
                                            filename = f.name,
                                            fontSizeSp = fontSizeSp,
                                            onCopy = { viewModel.copyToClipboard(f.content) },
                                            onExplain = {
                                                viewModel.createNewSession("Explain this file `${f.path}`:\n```\n${f.content}\n```") {
                                                    viewModel.navigateTo(AppNavDestination.CHAT)
                                                }
                                            },
                                            onFix = {
                                                viewModel.createNewSession("Fix bugs in `${f.path}`:\n```\n${f.content}\n```") {
                                                    viewModel.navigateTo(AppNavDestination.CHAT)
                                                }
                                            },
                                            onOptimize = {
                                                viewModel.createNewSession("Refactor and optimize `${f.path}`:\n```\n${f.content}\n```") {
                                                    viewModel.navigateTo(AppNavDestination.CHAT)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Select a file from the list above to view or edit.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // AI Project Memory
                    var memoryNotesText by remember { mutableStateOf(activeProject?.memoryNotes ?: "") }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI PROJECT CONTEXT & MEMORY",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AI Agent uses this context across all coding conversations for this project.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = memoryNotesText,
                            onValueChange = { memoryNotesText = it },
                            label = { Text("Project Architectural Memory & Guidelines") },
                            placeholder = { Text("e.g. Using Zustand for state, Tailwind for styling, strict typing, dark cyberpunk palette.") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = CyberCardBg,
                                unfocusedContainerColor = CyberCardBg
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.updateProjectMemory(memoryNotesText)
                                Toast.makeText(context, "AI Project Memory updated!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("SAVE PROJECT MEMORY", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                2 -> {
                    // Projects Manager List
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "YOUR LOCAL PROJECTS",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )

                            Button(
                                onClick = { showNewProjectDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("NEW PROJECT", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(projects) { project ->
                                val isCurrent = project.id == activeProjectId

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isCurrent) CyberDarkEmerald else CyberCardBg)
                                        .border(1.dp, if (isCurrent) NeonGreen else CyberBorder, RoundedCornerShape(10.dp))
                                        .clickable {
                                            viewModel.selectProject(project.id)
                                            selectedTabIndex = 0
                                        }
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = if (isCurrent) Icons.Default.FolderOpen else Icons.Default.Folder,
                                                    contentDescription = null,
                                                    tint = if (isCurrent) NeonGreen else TextMuted
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = project.name,
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = TextPrimary
                                                )
                                            }
                                            if (isCurrent) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(NeonGreen)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "ACTIVE",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = Color(0xFF001A0D)
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = project.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Stack: ${project.techStack}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TerminalCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // New Project Dialog
    if (showNewProjectDialog) {
        AlertDialog(
            onDismissRequest = { showNewProjectDialog = false },
            title = { Text("Scaffold New Project", color = NeonGreen, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newProjectName,
                        onValueChange = { newProjectName = it },
                        label = { Text("Project Name") },
                        placeholder = { Text("e.g. CyberDesk App") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = newProjectTech,
                        onValueChange = { newProjectTech = it },
                        label = { Text("Tech Stack") },
                        placeholder = { Text("e.g. Next.js + Tailwind / Python FastAPI") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = newProjectDesc,
                        onValueChange = { newProjectDesc = it },
                        label = { Text("Description & Requirements") },
                        placeholder = { Text("Briefly describe what this project will build...") },
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
                        if (newProjectName.isNotBlank()) {
                            viewModel.createNewProject(newProjectName, newProjectTech, newProjectDesc)
                            newProjectName = ""
                            newProjectDesc = ""
                            showNewProjectDialog = false
                            selectedTabIndex = 0
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D))
                ) {
                    Text("CREATE PROJECT", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProjectDialog = false }) { Text("CANCEL", color = TextMuted) }
            },
            containerColor = CyberPanelBg
        )
    }

    // Add File Dialog
    if (showAddFileDialog) {
        AlertDialog(
            onDismissRequest = { showAddFileDialog = false },
            title = { Text("Create File in Project", color = NeonGreen, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newFilePath,
                        onValueChange = { newFilePath = it },
                        label = { Text("File Path") },
                        placeholder = { Text("e.g. src/services/api.ts") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = newFileLang,
                        onValueChange = { newFileLang = it },
                        label = { Text("Language") },
                        placeholder = { Text("e.g. typescript, python, html, css") },
                        singleLine = true,
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
                        if (newFilePath.isNotBlank()) {
                            viewModel.addFileToActiveProject(newFilePath, "// File: $newFilePath\n", newFileLang)
                            newFilePath = ""
                            showAddFileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D))
                ) {
                    Text("CREATE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFileDialog = false }) { Text("CANCEL", color = TextMuted) }
            },
            containerColor = CyberPanelBg
        )
    }
}
