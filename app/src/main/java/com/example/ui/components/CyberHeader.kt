package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.RequestType
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGlow
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberDarkEmerald
import com.example.ui.theme.CyberPanelBg
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CyberHeader(
    modifier: Modifier = Modifier,
    statusText: String = "● AI ONLINE",
    experienceLevel: String = "Beginner",
    activeProviderName: String = "Gemini",
    onBrandClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CyberPanelBg,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CyberBorder,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Branding Header
                Column(
                    modifier = Modifier.clickable(enabled = onBrandClick != null) { onBrandClick?.invoke() }
                ) {
                    Text(
                        text = "AI AGENT",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "FOR DEVELOPERS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = NeonGreen
                    )
                }

                // "by Rauf" Elegant Glowing Cyber Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(CyberDarkEmerald, Color(0xFF031A0F))
                            )
                        )
                        .border(1.dp, CyberBorderGlow, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "by ",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = "Rauf",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = NeonGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-bar with Status + Provider + Level Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Online Pulsing Indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(glowScale)
                            .clip(CircleShape)
                            .background(NeonGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = NeonGreen
                    )
                }

                // Provider & Level Pills
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Provider Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberCardBg)
                            .border(0.8.dp, CyberBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = activeProviderName,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    // Level Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberCardBg)
                            .border(0.8.dp, EmeraldGreen.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = experienceLevel.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModeSelectorBar(
    selectedMode: RequestType,
    onModeSelected: (RequestType) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf(
        RequestType.AUTO,
        RequestType.CODE,
        RequestType.PROMPT,
        RequestType.EXPLAIN,
        RequestType.DEBUG,
        RequestType.LEARN,
        RequestType.PROJECT,
        RequestType.REVIEW,
        RequestType.OPTIMIZE,
        RequestType.CONVERT,
        RequestType.TESTS
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        modes.forEach { mode ->
            val isSelected = selectedMode == mode
            val bgBrush = if (isSelected) {
                Brush.horizontalGradient(listOf(CyberDarkEmerald, Color(0xFF032212)))
            } else {
                Brush.horizontalGradient(listOf(CyberCardBg, CyberPanelBg))
            }
            val borderCol = if (isSelected) NeonGreen else CyberBorder

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgBrush)
                    .border(1.dp, borderCol, RoundedCornerShape(8.dp))
                    .clickable { onModeSelected(mode) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mode.badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = if (isSelected) NeonGreen else TextSecondary
                    )
                }
            }
        }
    }
}
