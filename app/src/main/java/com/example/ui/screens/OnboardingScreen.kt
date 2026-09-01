package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AIProviderType
import com.example.data.DeveloperExperienceLevel
import com.example.data.PreferencesManager
import com.example.ui.AppViewModel
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

@Composable
fun OnboardingScreen(
    viewModel: AppViewModel,
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedProvider by remember { mutableStateOf(viewModel.preferencesManager.getActiveProvider()) }
    var apiKeyInput by remember { mutableStateOf(viewModel.preferencesManager.getApiKeyForProvider(selectedProvider.id)) }
    var selectedLevel by remember { mutableStateOf(viewModel.preferencesManager.getExperienceLevel()) }

    val isTesting by viewModel.isTestingConnection.collectAsState()
    val testResult by viewModel.connectionTestResult.collectAsState()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CyberBlack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Step Progress Indicator
            StepProgressBar(currentStep = currentStep, totalSteps = 5)

            Spacer(modifier = Modifier.height(16.dp))

            // Step Content
            when (currentStep) {
                1 -> Step1WelcomeHero(onNext = { currentStep = 2 })
                2 -> Step2ProviderSelection(
                    selectedProvider = selectedProvider,
                    onSelect = {
                        selectedProvider = it
                        viewModel.preferencesManager.setActiveProvider(it)
                        apiKeyInput = viewModel.preferencesManager.getApiKeyForProvider(it.id)
                        currentStep = 3
                    }
                )
                3 -> Step3ApiKeySetup(
                    provider = selectedProvider,
                    apiKey = apiKeyInput,
                    onKeyChange = { apiKeyInput = it },
                    isTesting = isTesting,
                    testResult = testResult,
                    onTest = { viewModel.testProviderConnection(selectedProvider, apiKeyInput) },
                    onOpenPortal = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedProvider.keyPortalUrl))
                        context.startActivity(intent)
                    },
                    onContinue = {
                        viewModel.preferencesManager.setApiKeyForProvider(selectedProvider.id, apiKeyInput)
                        currentStep = 4
                    },
                    onSkip = {
                        currentStep = 4
                    }
                )
                4 -> Step4ExperienceLevel(
                    selectedLevel = selectedLevel,
                    onSelect = {
                        selectedLevel = it
                        viewModel.preferencesManager.setExperienceLevel(it)
                        currentStep = 5
                    }
                )
                5 -> Step5ReadyToLaunch(
                    provider = selectedProvider,
                    level = selectedLevel,
                    hasKey = apiKeyInput.isNotBlank(),
                    onLaunch = {
                        viewModel.setOnboardingCompleted()
                        onComplete()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer branding
            Text(
                text = "AI AGENT FOR DEVELOPERS • by Rauf",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun StepProgressBar(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..totalSteps) {
            val isActive = i <= currentStep
            val isCurrent = i == currentStep

            Box(
                modifier = Modifier
                    .size(if (isCurrent) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCurrent) NeonGreen else if (isActive) EmeraldGreen else CyberBorder
                    )
            )
            if (i < totalSteps) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(2.dp)
                        .background(if (i < currentStep) EmeraldGreen else CyberBorder)
                )
            }
        }
    }
}

@Composable
fun Step1WelcomeHero(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing Icon Badge
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(CyberDarkEmerald, Color(0xFF03140C))
                    )
                )
                .border(2.dp, NeonGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = "AI Agent",
                tint = NeonGreen,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "AI AGENT",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            ),
            color = TextPrimary
        )
        Text(
            text = "FOR DEVELOPERS",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            ),
            color = NeonGreen
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Author Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CyberPanelBg)
                .border(1.dp, CyberBorderGlow, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = "by Rauf",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your AI Coding Partner",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TerminalCyan
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Build. Learn. Debug. Create.\nDesigned specifically for beginners, intermediate learners, and full-stack builders.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "INITIALIZE WORKSTATION",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next")
        }
    }
}

@Composable
fun Step2ProviderSelection(
    selectedProvider: AIProviderType,
    onSelect: (AIProviderType) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "CHOOSE YOUR AI ENGINE",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = NeonGreen
        )
        Text(
            text = "Select your preferred AI intelligence provider. You can switch models anytime in Settings.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        val providers = AIProviderType.values()
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            providers.forEach { provider ->
                val isSelected = selectedProvider == provider
                val isGemini = provider == AIProviderType.GEMINI

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) CyberDarkEmerald else CyberCardBg)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) NeonGreen else CyberBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelect(provider) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = provider.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                if (isGemini) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(NeonGreen)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "RECOMMENDED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF001A0D)
                                            )
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Default: ${provider.defaultModel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }

                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.ArrowForward,
                            contentDescription = "Select",
                            tint = if (isSelected) NeonGreen else TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Step3ApiKeySetup(
    provider: AIProviderType,
    apiKey: String,
    onKeyChange: (String) -> Unit,
    isTesting: Boolean,
    testResult: com.example.ai.ConnectionTestResult?,
    onTest: () -> Unit,
    onOpenPortal: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "CONNECT ${provider.displayName.uppercase()}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = NeonGreen
        )
        Text(
            text = "Your API key is stored securely on your device and never shared.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // API Key Input Field
        OutlinedTextField(
            value = apiKey,
            onValueChange = onKeyChange,
            label = { Text("${provider.displayName} API Key") },
            placeholder = { Text("Paste your API key here...") },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = NeonGreen) },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Visibility",
                        tint = TextMuted
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = CyberCardBg,
                unfocusedContainerColor = CyberCardBg
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons Row: Get API Key + Test Connection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onOpenPortal,
                colors = ButtonDefaults.buttonColors(containerColor = CyberCardElevated, contentColor = TerminalCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("GET KEY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }

            Button(
                onClick = onTest,
                enabled = !isTesting,
                colors = ButtonDefaults.buttonColors(containerColor = CyberDarkEmerald, contentColor = NeonGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (isTesting) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = NeonGreen)
                } else {
                    Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TEST PING", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // Test Result Banner
        if (testResult != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (testResult.isSuccess) Color(0xFF072415) else Color(0xFF280B0B))
                    .border(
                        1.dp,
                        if (testResult.isSuccess) NeonGreen else AlertRed,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (testResult.isSuccess) Icons.Default.CheckCircle else Icons.Default.Security,
                        contentDescription = null,
                        tint = if (testResult.isSuccess) NeonGreen else AlertRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = testResult.message,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (testResult.isSuccess) TextPrimary else AlertRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("CONTINUE TO PROFILE", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onSkip) {
            Text(
                text = "Use Built-In AI Engine for now",
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted
            )
        }
    }
}

@Composable
fun Step4ExperienceLevel(
    selectedLevel: DeveloperExperienceLevel,
    onSelect: (DeveloperExperienceLevel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "YOUR CODING EXPERIENCE",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = NeonGreen
        )
        Text(
            text = "AI Agent will calibrate its explanations, step-by-step guidance, and code depth to your skill level.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        val levels = DeveloperExperienceLevel.values()
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            levels.forEach { level ->
                val isSelected = selectedLevel == level
                val icon = when (level) {
                    DeveloperExperienceLevel.BEGINNER -> Icons.Default.Psychology
                    DeveloperExperienceLevel.INTERMEDIATE -> Icons.Default.Code
                    DeveloperExperienceLevel.ADVANCED -> Icons.Default.Speed
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) CyberDarkEmerald else CyberCardBg)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) NeonGreen else CyberBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelect(level) }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonGreen else CyberPanelBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF001A0D) else NeonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = level.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
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
    }
}

@Composable
fun Step5ReadyToLaunch(
    provider: AIProviderType,
    level: DeveloperExperienceLevel,
    hasKey: Boolean,
    onLaunch: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(CyberDarkEmerald)
                .border(2.dp, NeonGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.RocketLaunch,
                contentDescription = "Launch",
                tint = NeonGreen,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "WORKSTATION READY",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
            color = TextPrimary
        )
        Text(
            text = "AI AGENT FOR DEVELOPERS is configured and armed.",
            style = MaterialTheme.typography.bodyMedium,
            color = NeonGreen
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Summary Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberCardBg)
                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryItemRow(label = "AI Engine", value = provider.displayName)
                SummaryItemRow(label = "Default Model", value = provider.defaultModel)
                SummaryItemRow(label = "Profile", value = level.title)
                SummaryItemRow(label = "Authentication", value = if (hasKey) "API Key Configured" else "Built-in AI Fallback")
                SummaryItemRow(label = "Architect", value = "Rauf")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onLaunch,
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "LAUNCH COMMAND CENTER",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            )
        }
    }
}

@Composable
fun SummaryItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(text = value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
    }
}
