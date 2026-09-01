package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Cyberpunk & Terminal Palette
val CyberBlack = Color(0xFF030705)
val CyberDarkBg = Color(0xFF070F0B)
val CyberPanelBg = Color(0xFF0C1812)
val CyberCardBg = Color(0xFF102018)
val CyberCardElevated = Color(0xFF152A20)

// Neon & Emerald Accents
val NeonGreen = Color(0xFF00FF88)
val EmeraldGreen = Color(0xFF10B981)
val DeepEmerald = Color(0xFF059669)
val CyberDarkEmerald = Color(0xFF023820)
val CyberBorder = Color(0xFF1B3D2B)
val CyberBorderGlow = Color(0xFF26563D)

// Terminal & State Colors
val TerminalAmber = Color(0xFFF59E0B)
val TerminalCyan = Color(0xFF06B6D4)
val TerminalPurple = Color(0xFFA855F7)
val AlertRed = Color(0xFFEF4444)
val SuccessGreen = Color(0xFF22C55E)

// Text Colors
val TextPrimary = Color(0xFFE6FFF2)
val TextSecondary = Color(0xFF8BAF9A)
val TextMuted = Color(0xFF557766)
val TextCode = Color(0xFFB4FAD4)

// Code Syntax Colors
val CodeKeyword = Color(0xFFFF7B72)
val CodeFunction = Color(0xFF79C0FF)
val CodeString = Color(0xFFA5D6FF)
val CodeComment = Color(0xFF7EE787)
val CodeNumber = Color(0xFFFFA657)
val CodeBackground = Color(0xFF040A07)
val CodeHeader = Color(0xFF0A1610)

// Theme Presets
enum class AppThemePreset(val displayName: String, val primary: Color, val background: Color, val surface: Color) {
    QUANTUM_GREEN("Quantum Green", NeonGreen, CyberDarkBg, CyberCardBg),
    MATRIX_GREEN("Matrix Green", Color(0xFF00FF41), Color(0xFF001100), Color(0xFF002200)),
    MIDNIGHT_CYBER("Midnight Cyber", Color(0xFF38BDF8), Color(0xFF050B14), Color(0xFF0A192F)),
    CYBER_DARK("Cyber Dark", Color(0xFF34D399), Color(0xFF0F172A), Color(0xFF1E293B)),
    OLED_BLACK("OLED Black", Color(0xFF00FF88), Color(0xFF000000), Color(0xFF0A0A0A))
}
