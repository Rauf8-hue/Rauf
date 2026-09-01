package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun getCustomColorScheme(preset: AppThemePreset = AppThemePreset.QUANTUM_GREEN) = darkColorScheme(
    primary = preset.primary,
    onPrimary = Color(0xFF001A0D),
    primaryContainer = CyberDarkEmerald,
    onPrimaryContainer = Color(0xFF99FFCC),
    secondary = EmeraldGreen,
    onSecondary = Color(0xFF002213),
    secondaryContainer = Color(0xFF0A2B1D),
    onSecondaryContainer = Color(0xFFA3E635),
    tertiary = TerminalCyan,
    onTertiary = Color(0xFF002530),
    background = preset.background,
    onBackground = TextPrimary,
    surface = preset.surface,
    onSurface = TextPrimary,
    surfaceVariant = CyberCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = CyberBorder,
    outlineVariant = CyberBorderGlow,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    preset: AppThemePreset = AppThemePreset.QUANTUM_GREEN,
    content: @Composable () -> Unit
) {
    val colorScheme = getCustomColorScheme(preset)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
