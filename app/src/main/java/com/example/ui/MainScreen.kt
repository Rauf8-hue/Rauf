package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearningScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberDarkEmerald
import com.example.ui.theme.CyberPanelBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MainApp(
    viewModel: AppViewModel
) {
    val themePreset by viewModel.preferencesManager.themePreset.collectAsState()
    val showOnboarding by viewModel.showOnboarding.collectAsState()
    val currentDestination by viewModel.currentDestination.collectAsState()

    MyApplicationTheme(preset = themePreset) {
        if (showOnboarding) {
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {
                    viewModel.setOnboardingCompleted()
                }
            )
        } else {
            Scaffold(
                containerColor = CyberBlack,
                bottomBar = {
                    CyberBottomNavBar(
                        currentDestination = currentDestination,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AnimatedContent(
                        targetState = currentDestination,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screen_transition"
                    ) { destination ->
                        when (destination) {
                            AppNavDestination.HOME -> HomeScreen(viewModel = viewModel)
                            AppNavDestination.CHAT -> ChatScreen(viewModel = viewModel)
                            AppNavDestination.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                            AppNavDestination.LEARN -> LearningScreen(viewModel = viewModel)
                            AppNavDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CyberBottomNavBar(
    currentDestination: AppNavDestination,
    onNavigate: (AppNavDestination) -> Unit
) {
    Surface(
        color = CyberPanelBg,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                icon = Icons.Default.ElectricBolt,
                label = "HUB",
                isSelected = currentDestination == AppNavDestination.HOME,
                onClick = { onNavigate(AppNavDestination.HOME) }
            )
            NavTabItem(
                icon = Icons.Default.Forum,
                label = "AI AGENT",
                isSelected = currentDestination == AppNavDestination.CHAT,
                onClick = { onNavigate(AppNavDestination.CHAT) }
            )
            NavTabItem(
                icon = Icons.Default.FolderSpecial,
                label = "PROJECTS",
                isSelected = currentDestination == AppNavDestination.PROJECTS,
                onClick = { onNavigate(AppNavDestination.PROJECTS) }
            )
            NavTabItem(
                icon = Icons.Default.School,
                label = "LEARN",
                isSelected = currentDestination == AppNavDestination.LEARN,
                onClick = { onNavigate(AppNavDestination.LEARN) }
            )
            NavTabItem(
                icon = Icons.Default.Settings,
                label = "CONFIG",
                isSelected = currentDestination == AppNavDestination.SETTINGS,
                onClick = { onNavigate(AppNavDestination.SETTINGS) }
            )
        }
    }
}

@Composable
fun NavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isSelected) CyberDarkEmerald else Color.Transparent)
                .border(
                    width = if (isSelected) 1.dp else 0.dp,
                    color = if (isSelected) NeonGreen else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) NeonGreen else TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) NeonGreen else TextMuted
        )
    }
}
