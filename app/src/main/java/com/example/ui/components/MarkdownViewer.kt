package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGlow
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberDarkEmerald
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TerminalAmber
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TextCode
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MarkdownViewer(
    content: String,
    fontSizeSp: Int = 12,
    onCopyCode: (String) -> Unit,
    onSaveCode: ((String, String) -> Unit)? = null,
    onExplainCode: ((String) -> Unit)? = null,
    onFixCode: ((String) -> Unit)? = null,
    onOptimizeCode: ((String) -> Unit)? = null,
    onRunInstructions: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val segments = parseMarkdownSegments(content)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (segment in segments) {
            when (segment) {
                is MarkdownSegment.Heading -> {
                    val color = when (segment.level) {
                        1 -> NeonGreen
                        2 -> TerminalCyan
                        else -> TextPrimary
                    }
                    val size = when (segment.level) {
                        1 -> 18.sp
                        2 -> 16.sp
                        else -> 14.sp
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = segment.text,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = size,
                            letterSpacing = 0.5.sp
                        ),
                        color = color
                    )
                }

                is MarkdownSegment.CodeBlock -> {
                    // Check if it's a special PROMPT template
                    if (segment.language.equals("prompt", ignoreCase = true) || segment.language.equals("markdown", ignoreCase = true) && segment.code.contains("System Role")) {
                        PromptSpecBlockView(
                            prompt = segment.code,
                            onCopy = { onCopyCode(segment.code) }
                        )
                    } else {
                        CodeBlockView(
                            code = segment.code,
                            language = segment.language,
                            fontSizeSp = fontSizeSp,
                            onCopy = { onCopyCode(segment.code) },
                            onSaveToProject = if (onSaveCode != null) { { onSaveCode(segment.code, segment.language) } } else null,
                            onExplain = if (onExplainCode != null) { { onExplainCode(segment.code) } } else null,
                            onFix = if (onFixCode != null) { { onFixCode(segment.code) } } else null,
                            onOptimize = if (onOptimizeCode != null) { { onOptimizeCode(segment.code) } } else null,
                            onRunInstructions = if (onRunInstructions != null) { { onRunInstructions(segment.code) } } else null
                        )
                    }
                }

                is MarkdownSegment.BulletItem -> {
                    Row(
                        modifier = Modifier.padding(start = (segment.indent * 12).dp, top = 2.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "▸ ",
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        FormattedTextLine(text = segment.text)
                    }
                }

                is MarkdownSegment.Paragraph -> {
                    FormattedTextLine(text = segment.text)
                }
            }
        }
    }
}

@Composable
fun PromptSpecBlockView(
    prompt: String,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F2018), Color(0xFF07140E))
                )
            )
            .border(1.dp, NeonGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
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
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Prompt",
                        tint = NeonGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MASTER SYSTEM PROMPT SPECIFICATION",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = NeonGreen
                    )
                }

                Button(
                    onClick = onCopy,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF001A0D)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "COPY PROMPT",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = prompt,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                ),
                color = TextCode
            )
        }
    }
}

@Composable
fun FormattedTextLine(text: String) {
    val annotated = buildAnnotatedString {
        var cursor = 0
        val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
        val codeRegex = Regex("`([^`]+)`")

        // Parse bold and inline code
        val matches = (boldRegex.findAll(text).map { Triple(it.range, "bold", it.groupValues[1]) } +
                codeRegex.findAll(text).map { Triple(it.range, "code", it.groupValues[1]) })
            .sortedBy { it.first.first }

        for (match in matches) {
            val (range, type, matchText) = match
            if (range.first > cursor) {
                append(text.substring(cursor, range.first))
            }
            val start = length
            append(matchText)
            val end = length

            if (type == "bold") {
                addStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextPrimary), start, end)
            } else if (type == "code") {
                addStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        color = NeonGreen,
                        background = CyberDarkEmerald.copy(alpha = 0.5f)
                    ),
                    start,
                    end
                )
            }
            cursor = range.last + 1
        }
        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }

    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary
    )
}

sealed class MarkdownSegment {
    data class Heading(val level: Int, val text: String) : MarkdownSegment()
    data class Paragraph(val text: String) : MarkdownSegment()
    data class BulletItem(val indent: Int, val text: String) : MarkdownSegment()
    data class CodeBlock(val language: String, val code: String) : MarkdownSegment()
}

fun parseMarkdownSegments(content: String): List<MarkdownSegment> {
    val segments = mutableListOf<MarkdownSegment>()
    val lines = content.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        // Code Block start
        if (line.trimStart().startsWith("```")) {
            val lang = line.trimStart().removePrefix("```").trim()
            val codeBuilder = StringBuilder()
            i++
            while (i < lines.size && !lines[i].trimStart().startsWith("```")) {
                codeBuilder.appendLine(lines[i])
                i++
            }
            segments.add(MarkdownSegment.CodeBlock(lang, codeBuilder.toString().trimEnd()))
            i++
            continue
        }

        // Headings
        if (line.startsWith("### ")) {
            segments.add(MarkdownSegment.Heading(3, line.removePrefix("### ").trim()))
            i++
            continue
        }
        if (line.startsWith("## ")) {
            segments.add(MarkdownSegment.Heading(2, line.removePrefix("## ").trim()))
            i++
            continue
        }
        if (line.startsWith("# ")) {
            segments.add(MarkdownSegment.Heading(1, line.removePrefix("# ").trim()))
            i++
            continue
        }

        // Bullet Items
        if (line.trimStart().startsWith("- ") || line.trimStart().startsWith("* ") || line.trimStart().startsWith("▸ ")) {
            val leadingSpaces = line.length - line.trimStart().length
            val indent = leadingSpaces / 2
            val text = line.trimStart().removePrefix("- ").removePrefix("* ").removePrefix("▸ ").trim()
            segments.add(MarkdownSegment.BulletItem(indent, text))
            i++
            continue
        }

        // Paragraph
        if (line.isNotBlank()) {
            segments.add(MarkdownSegment.Paragraph(line))
        }

        i++
    }

    return segments
}
