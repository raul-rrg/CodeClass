package com.codeclass.app.ui.screens.challenges.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.ui.theme.*

// Bloques de Markdown soportados: párrafo, ítem de lista y bloque de código
internal sealed class MdBlock {
    data class Paragraph(val text: String) : MdBlock()
    data class BulletItem(val text: String) : MdBlock()
    data class CodeBlock(val code: String) : MdBlock()
}

// Parsea texto Markdown en una lista de bloques estructurados
internal fun parseMarkdownBlocks(input: String): List<MdBlock> {
    val result = mutableListOf<MdBlock>()
    val lines  = input.lines()
    var i      = 0
    while (i < lines.size) {
        val line = lines[i]
        when {
            line.trimStart().startsWith("```") -> {
                val code = buildString {
                    i++
                    while (i < lines.size && !lines[i].trimStart().startsWith("```")) {
                        if (isNotEmpty()) append('\n')
                        append(lines[i])
                        i++
                    }
                    i++
                }
                result.add(MdBlock.CodeBlock(code))
            }
            line.startsWith("- ") || line.startsWith("* ") -> {
                result.add(MdBlock.BulletItem(line.drop(2)))
                i++
            }
            line.isBlank() -> i++
            else -> {
                val sb = StringBuilder(line)
                i++
                while (i < lines.size && lines[i].isNotBlank()
                    && !lines[i].startsWith("- ") && !lines[i].startsWith("* ")
                    && !lines[i].trimStart().startsWith("```")) {
                    sb.append(' ').append(lines[i])
                    i++
                }
                result.add(MdBlock.Paragraph(sb.toString()))
            }
        }
    }
    return result
}

// Aplica estilos inline: **negrita** y `código`
internal fun buildInlineStyledText(raw: String) = buildAnnotatedString {
    var j = 0
    while (j < raw.length) {
        when {
            raw.startsWith("**", j) -> {
                val end = raw.indexOf("**", j + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = ColorSubtitle)) {
                        append(raw.substring(j + 2, end))
                    }
                    j = end + 2
                } else { append(raw[j]); j++ }
            }
            raw[j] == '`' -> {
                val end = raw.indexOf('`', j + 1)
                if (end != -1) {
                    withStyle(SpanStyle(color = Color(0xFF60A5FA), fontFamily = JetBrainsMonoFamily)) {
                        append(raw.substring(j + 1, end))
                    }
                    j = end + 1
                } else { append(raw[j]); j++ }
            }
            else -> { append(raw[j]); j++ }
        }
    }
}

// Renderiza texto Markdown con soporte de párrafos, listas y bloques de código
@Composable
internal fun MarkdownText(text: String, modifier: Modifier = Modifier) {
    val blocks = remember(text) { parseMarkdownBlocks(text) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        blocks.forEach { block ->
            when (block) {
                is MdBlock.Paragraph -> Text(
                    text = buildInlineStyledText(block.text),
                    fontSize = 14.sp, color = ColorBody, lineHeight = 22.sp,
                )
                is MdBlock.BulletItem -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("•", fontSize = 14.sp, color = ColorMuted, lineHeight = 22.sp)
                    Text(
                        text = buildInlineStyledText(block.text),
                        fontSize = 14.sp, color = ColorBody, lineHeight = 22.sp,
                    )
                }
                is MdBlock.CodeBlock -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D1117))
                        .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                ) {
                    Text(
                        text = block.code,
                        fontSize = 12.sp, color = ColorBody,
                        fontFamily = JetBrainsMonoFamily, lineHeight = 18.sp,
                    )
                }
            }
        }
    }
}
