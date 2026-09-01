package com.example.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CodeBackground
import com.example.ui.theme.CodeComment
import com.example.ui.theme.CodeFunction
import com.example.ui.theme.CodeHeader
import com.example.ui.theme.CodeKeyword
import com.example.ui.theme.CodeNumber
import com.example.ui.theme.CodeString
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGlow
import com.example.ui.theme.CyberDarkEmerald
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TerminalAmber
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CodeBlockView(
    code: String,
    language: String = "text",
    filename: String? = null,
    fontSizeSp: Int = 12,
    onCopy: () -> Unit,
    onSaveToProject: (() -> Unit)? = null,
    onExplain: (() -> Unit)? = null,
    onFix: (() -> Unit)? = null,
    onOptimize: (() -> Unit)? = null,
    onRunInstructions: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val lines = code.trimEnd().lines()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        color = CodeBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Code Block Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CodeHeader)
                    .border(0.5.dp, CyberBorder, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Terminal window buttons + Language badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Terminal 3 dots
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(AlertRed.copy(alpha = 0.8f)))
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(TerminalAmber.copy(alpha = 0.8f)))
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(NeonGreen.copy(alpha = 0.8f)))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Language Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberDarkEmerald)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = (filename ?: language).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonGreen
                        )
                    }
                }

                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CodeActionButton(
                        icon = Icons.Default.ContentCopy,
                        label = "COPY",
                        onClick = onCopy
                    )
                    if (onSaveToProject != null) {
                        CodeActionButton(
                            icon = Icons.Default.SaveAlt,
                            label = "SAVE",
                            onClick = onSaveToProject
                        )
                    }
                    if (onExplain != null) {
                        CodeActionButton(
                            icon = Icons.Default.Psychology,
                            label = "EXPLAIN",
                            onClick = onExplain
                        )
                    }
                    if (onFix != null) {
                        CodeActionButton(
                            icon = Icons.Default.BugReport,
                            label = "FIX",
                            onClick = onFix
                        )
                    }
                    if (onOptimize != null) {
                        CodeActionButton(
                            icon = Icons.Default.AutoFixHigh,
                            label = "OPTIMIZE",
                            onClick = onOptimize
                        )
                    }
                    if (onRunInstructions != null) {
                        CodeActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = "RUN",
                            onClick = onRunInstructions
                        )
                    }
                }
            }

            // Code Content with Line Numbers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                // Line Numbers Column
                Column(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .border(
                            width = 0.5.dp,
                            color = CyberBorder.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(0.dp)
                        )
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    lines.indices.forEach { index ->
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = fontSizeSp.sp,
                                lineHeight = (fontSizeSp + 6).sp
                            ),
                            color = TextMuted
                        )
                    }
                }

                // Highlighted Code Column
                Column {
                    lines.forEach { line ->
                        Text(
                            text = highlightSyntax(line, language),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = fontSizeSp.sp,
                                lineHeight = (fontSizeSp + 6).sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CodeActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF0D1C14))
            .border(0.5.dp, CyberBorder, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = NeonGreen,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
        }
    }
}

// Basic Kotlin, Python, JS, TS, HTML, CSS syntax highlighter
fun highlightSyntax(line: String, language: String): AnnotatedString {
    return buildAnnotatedString {
        val trimmed = line.trimStart()

        // Comments
        if (trimmed.startsWith("//") || trimmed.startsWith("#") || trimmed.startsWith("/*") || trimmed.startsWith("*")) {
            append(line)
            addStyle(SpanStyle(color = CodeComment), 0, line.length)
            return@buildAnnotatedString
        }

        val keywords = setOf(
            "val", "var", "fun", "class", "interface", "import", "package", "return", "if", "else", "for", "while",
            "const", "let", "function", "export", "default", "from", "async", "await", "def", "lambda", "try", "catch",
            "public", "private", "protected", "override", "data", "sealed", "enum", "type", "struct", "impl"
        )

        val tokens = line.split(Regex("(?<=[^a-zA-Z0-9_])|(?=[^a-zA-Z0-9_])"))
        for (token in tokens) {
            val start = length
            append(token)
            val end = length

            when {
                token in keywords -> {
                    addStyle(SpanStyle(color = CodeKeyword, fontWeight = FontWeight.Bold), start, end)
                }
                token.startsWith("\"") || token.endsWith("\"") || token.startsWith("'") || token.endsWith("'") -> {
                    addStyle(SpanStyle(color = CodeString), start, end)
                }
                token.toIntOrNull() != null || token.toDoubleOrNull() != null -> {
                    addStyle(SpanStyle(color = CodeNumber), start, end)
                }
                token.firstOrNull()?.isUpperCase() == true -> {
                    addStyle(SpanStyle(color = TerminalCyan, fontWeight = FontWeight.SemiBold), start, end)
                }
                token.endsWith("(") -> {
                    addStyle(SpanStyle(color = CodeFunction), start, end)
                }
                else -> {
                    addStyle(SpanStyle(color = TextPrimary), start, end)
                }
            }
        }
    }
}
