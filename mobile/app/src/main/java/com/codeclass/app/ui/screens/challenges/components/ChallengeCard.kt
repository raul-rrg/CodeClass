package com.codeclass.app.ui.screens.challenges.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.data.model.Exercise
import com.codeclass.app.ui.theme.*

// Helpers de dificultad y categoría usados en tarjeta y filtros
fun difficultyLabel(d: String) = when (d) {
    "all"    -> "Todos"
    "easy"   -> "Fácil"
    "medium" -> "Medio"
    "hard"   -> "Difícil"
    "insane" -> "Insane"
    else     -> d.replaceFirstChar { it.uppercase() }
}

fun categoryLabel(c: String) = when (c.lowercase()) {
    "math"                                             -> "Matemáticas"
    "strings", "string"                                -> "Cadenas"
    "arrays", "array"                                  -> "Arrays"
    "trees", "tree"                                    -> "Árboles"
    "graphs", "graph"                                  -> "Grafos"
    "sorting", "sort"                                  -> "Ordenación"
    "dynamic programming", "dynamic_programming", "dp" -> "Prog. dinámica"
    "recursion"                                        -> "Recursión"
    "other"                                            -> "Otro"
    else -> c.split(" ").joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercase() } }
}

fun difficultyColor(d: String): Color = when (d) {
    "easy"   -> Color(0xFF34D399)
    "medium" -> Color(0xFFFBBF24)
    "hard"   -> Color(0xFFFB7185)
    "insane" -> Color(0xFFA78BFA)
    else     -> ColorPrimary
}

fun difficultyPoints(d: String): Int = when (d) {
    "easy"   -> 20
    "medium" -> 50
    "hard"   -> 100
    "insane" -> 200
    else     -> 0
}

// Tarjeta de reto en el listado: título, tags, descripción, autor y puntos
@Composable
fun ChallengeCard(
    exercise:         Exercise,
    isAuthenticated:  Boolean,
    modifier:         Modifier = Modifier,
    onClick:          () -> Unit,
    onViewSubmissions: () -> Unit,
) {
    val diffColor = difficultyColor(exercise.difficulty)
    val pts       = difficultyPoints(exercise.difficulty)
    val isSolved  = exercise.isSolved

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = .07f), RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = .025f))
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {

            // Título + badge de resuelto o candado si no autenticado
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.Top,
            ) {
                Text(
                    text          = exercise.title,
                    fontSize      = 16.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = Color.White,
                    fontFamily    = SpaceGroteskFamily,
                    letterSpacing = (-0.3).sp,
                    lineHeight    = 20.sp,
                    modifier      = Modifier.weight(1f),
                    maxLines      = 2,
                    overflow      = TextOverflow.Ellipsis,
                )
                when {
                    isSolved -> Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF34D399).copy(alpha = .14f))
                            .border(1.dp, Color(0xFF34D399).copy(alpha = .4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(11.dp))
                        Text("Solved", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF34D399), fontFamily = SpaceGroteskFamily)
                    }
                    !isAuthenticated -> Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint     = Color.White.copy(alpha = .45f),
                        modifier = Modifier.size(16.dp).padding(top = 2.dp),
                    )
                }
            }

            // Chips de categoría y dificultad
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (!exercise.category.isNullOrEmpty()) {
                    ChipTag(
                        label       = categoryLabel(exercise.category),
                        textColor   = Color.White.copy(alpha = .8f),
                        bgColor     = Color.White.copy(alpha = .05f),
                        borderColor = Color.White.copy(alpha = .12f),
                    )
                }
                ChipTag(
                    label       = difficultyLabel(exercise.difficulty),
                    textColor   = diffColor,
                    bgColor     = diffColor.copy(alpha = .12f),
                    borderColor = diffColor.copy(alpha = .35f),
                )
            }

            // Descripción corta truncada a 2 líneas
            val description = exercise.shortDescription ?: exercise.description?.trimEnd()
            if (!description.isNullOrEmpty()) {
                Text(
                    text       = description,
                    fontSize   = 13.sp,
                    color      = Color.White.copy(alpha = .6f),
                    fontFamily = SpaceGroteskFamily,
                    lineHeight = (13 * 1.4f).sp,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                )
            }

            // Avatar + nombre del autor
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CreatorAvatar(exercise.author ?: "?")
                Row {
                    Text("Creado por ", fontSize = 12.sp, color = Color.White.copy(alpha = .55f), fontFamily = SpaceGroteskFamily)
                    Text(exercise.author ?: "Unknown", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = .85f), fontFamily = SpaceGroteskFamily)
                }
            }

            // Footer: botón de entregas (si autenticado) y puntos
            if (pts > 0) {
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = if (isAuthenticated) Arrangement.SpaceBetween else Arrangement.End,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    if (isAuthenticated) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFA78BFA).copy(alpha = .12f))
                                .border(1.dp, Color(0xFFA78BFA).copy(alpha = .3f), RoundedCornerShape(8.dp))
                                .clickable { onViewSubmissions() }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(12.dp))
                            Text("Ver entregas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFA78BFA), fontFamily = SpaceGroteskFamily)
                        }
                    }
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(14.dp))
                        Text("$pts pts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ColorAccent, fontFamily = JetBrainsMonoFamily)
                    }
                }
            }
        }

        // Barra de gradiente verde en el borde izquierdo cuando el reto está resuelto
        if (isSolved) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .width(3.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0x0034D399), Color(0xFF34D399), Color(0x0034D399)))
                    ),
            )
        }
    }
}

// Chip de etiqueta con colores personalizables
@Composable
private fun ChipTag(label: String, textColor: Color, bgColor: Color, borderColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp),
    ) {
        Text(
            text          = label,
            fontSize      = 11.sp,
            fontWeight    = FontWeight.SemiBold,
            color         = textColor,
            fontFamily    = SpaceGroteskFamily,
            letterSpacing = 0.2.sp,
        )
    }
}

// Avatar circular con la inicial del autor del reto
@Composable
private fun CreatorAvatar(name: String) {
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(ColorPrimary.copy(alpha = .30f))
            .border(1.dp, ColorPrimary.copy(alpha = .55f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(initial, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = SpaceGroteskFamily)
    }
}
