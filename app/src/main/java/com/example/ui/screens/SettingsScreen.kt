package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.ConnectionTestResult
import com.example.data.AIProviderType
import com.example.data.DeveloperExperienceLevel
import com.example.data.PreferencesManager
import com.example.ui.AppViewModel
import com.example.ui.components.CyberHeader
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AppThemePreset
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

@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeProvider by viewModel.preferencesManager.activeProvider.collectAsState()
    val experienceLevel by viewModel.preferencesManager.experienceLevel.collectAsState()
    val currentTheme by viewModel.preferencesManager.themePreset.collectAsState()
    val streamingEnabled by viewModel.preferencesManager.streamingEnabled.collectAsState()
    val codeFontSize by viewModel.preferencesManager.codeFontSize.collectAsState()
    val isTesting by viewModel.isTestingConnection.collectAsState()
    val testResult by viewModel.connectionTestResult.collectAsState()

    var activeKeyInput by remember(activeProvider) {
        mutableStateOf(viewModel.preferencesManager.getApiKeyForProvider(activeProvider.id))
    }
    var isKeyVisible by remember { mutableStateOf(false) }
    var selectedModel by remember(activeProvider) {
        mutableStateOf(viewModel.preferencesManager.getModelForProvider(activeProvider.id))
    }
    var customBaseUrlInput by remember {
        mutableStateOf(viewModel.preferencesManager.getCustomBaseUrl())
    }

    var showModelDropdown by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

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
                statusText = "● SETTINGS & KEYS",
                experienceLevel = experienceLevel.title,
                activeProviderName = activeProvider.displayName
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Active AI Provider
                SettingsSectionHeader(
                    title = "AI PROVIDER & ENGINE",
                    icon = Icons.Default.Tune,
                    color = NeonGreen
                )

                // Provider Switcher Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AIProviderType.values().forEach { provider ->
                        val isSelected = activeProvider == provider
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyberDarkEmerald else CyberCardBg)
                                .border(1.dp, if (isSelected) NeonGreen else CyberBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.preferencesManager.setActiveProvider(provider)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = provider.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) NeonGreen else TextSecondary
                            )
                        }
                    }
                }

                // Model Selection Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberCardBg)
                        .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                        .clickable { showModelDropdown = true }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Active Model", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(selectedModel, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        }
                        Text("CHANGE ▾", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonGreen)
                    }

                    DropdownMenu(
                        expanded = showModelDropdown,
                        onDismissRequest = { showModelDropdown = false },
                        modifier = Modifier.background(CyberPanelBg)
                    ) {
                        activeProvider.supportedModels.forEach { modelName ->
                            DropdownMenuItem(
                                text = { Text(modelName, color = TextPrimary) },
                                onClick = {
                                    selectedModel = modelName
                                    viewModel.preferencesManager.setModelForProvider(activeProvider.id, modelName)
                                    showModelDropdown = false
                                }
                            )
                        }
                    }
                }

                // If Custom Provider: Base URL
                if (activeProvider == AIProviderType.CUSTOM) {
                    OutlinedTextField(
                        value = customBaseUrlInput,
                        onValueChange = {
                            customBaseUrlInput = it
                            viewModel.preferencesManager.setCustomBaseUrl(it)
                        },
                        label = { Text("Custom API Base URL") },
                        placeholder = { Text("http://localhost:8000/v1/") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CyberCardBg,
                            unfocusedContainerColor = CyberCardBg
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Section 2: API Key Configuration
                SettingsSectionHeader(
                    title = "API KEY CONFIGURATION",
                    icon = Icons.Default.Key,
                    color = TerminalCyan
                )

                // Key Status Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberCardBg)
                        .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Configured Key for ${activeProvider.displayName}:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = PreferencesManager.maskApiKey(activeKeyInput),
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = if (activeKeyInput.isNotBlank()) NeonGreen else TerminalAmber
                        )

                        OutlinedTextField(
                            value = activeKeyInput,
                            onValueChange = { activeKeyInput = it },
                            label = { Text("Enter / Update API Key") },
                            visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                                    Icon(
                                        imageVector = if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle",
                                        tint = TextMuted
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = CyberPanelBg,
                                unfocusedContainerColor = CyberPanelBg
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.preferencesManager.setApiKeyForProvider(activeProvider.id, activeKeyInput)
                                    Toast.makeText(context, "API Key saved securely!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SAVE KEY", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(activeProvider.keyPortalUrl))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCardElevated, contentColor = TerminalCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("GET KEY", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Test Connection Button
                        Button(
                            onClick = {
                                viewModel.testProviderConnection(activeProvider, activeKeyInput, selectedModel)
                            },
                            enabled = !isTesting,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberDarkEmerald, contentColor = NeonGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isTesting) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = NeonGreen)
                            } else {
                                Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("TEST CONNECTION (LIVE PING)", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (testResult != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (testResult!!.isSuccess) Color(0xFF072415) else Color(0xFF280B0B))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = testResult!!.message,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (testResult!!.isSuccess) NeonGreen else AlertRed
                                )
                            }
                        }
                    }
                }

                // Section 3: Developer Experience Level
                SettingsSectionHeader(
                    title = "DEVELOPER PROFILE & LEVEL",
                    icon = Icons.Default.Psychology,
                    color = TerminalAmber
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DeveloperExperienceLevel.values().forEach { level ->
                        val isSelected = experienceLevel == level

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyberDarkEmerald else CyberCardBg)
                                .border(1.dp, if (isSelected) NeonGreen else CyberBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.preferencesManager.setExperienceLevel(level) }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Code,
                                    contentDescription = null,
                                    tint = if (isSelected) NeonGreen else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = level.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = level.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 4: Visual Theme Presets
                SettingsSectionHeader(
                    title = "CYBERPUNK THEME PRESETS",
                    icon = Icons.Default.Palette,
                    color = TerminalPurple
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemePreset.values().forEach { preset ->
                        val isSelected = currentTheme == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyberDarkEmerald else CyberCardBg)
                                .border(1.dp, if (isSelected) NeonGreen else CyberBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.preferencesManager.setThemePreset(preset) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(preset.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = preset.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) NeonGreen else TextSecondary
                                )
                            }
                        }
                    }
                }

                // Section 5: Preferences & Toggles
                SettingsSectionHeader(
                    title = "EDITOR PREFERENCES",
                    icon = Icons.Default.FormatSize,
                    color = NeonGreen
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberCardBg)
                        .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Streaming Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Real-time Token Streaming", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                Text("Stream AI code responses chunk-by-chunk", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                            Switch(
                                checked = streamingEnabled,
                                onCheckedChange = { viewModel.preferencesManager.setStreamingEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF001A0D),
                                    checkedTrackColor = NeonGreen,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = CyberPanelBg
                                )
                            )
                        }

                        HorizontalDivider(color = CyberBorder)

                        // Code Font Size Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Code Font Size", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(10, 12, 14, 16).forEach { size ->
                                    val isSelected = codeFontSize == size
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSelected) NeonGreen else CyberPanelBg)
                                            .clickable { viewModel.preferencesManager.setCodeFontSize(size) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${size}sp",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color(0xFF001A0D) else TextSecondary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 6: About & Branding
                SettingsSectionHeader(
                    title = "ABOUT AI AGENT",
                    icon = Icons.Default.Info,
                    color = EmeraldGreen
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberPanelBg)
                        .border(1.dp, CyberBorderGlow, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "AI AGENT FOR DEVELOPERS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                            color = TextPrimary
                        )
                        Text(
                            text = "Your AI Coding Partner",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = TerminalCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Architect & Lead Developer: Rauf",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "A complete, production-ready AI developer workstation built with Jetpack Compose, Room Database, Multi-Provider LLM Streaming, and interactive coding mentor roadmaps.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = color
        )
    }
}
