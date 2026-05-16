package com.codeclass.app.ui.screens.exercises.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.codeclass.app.data.model.Exercise
import com.codeclass.app.ui.theme.*

// Formatea la categoría capitalizando cada palabra
private fun categoryLabel(c: String) = c.split(" ")
    .joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercase() } }

// Chip de etiqueta reutilizable con color de texto, fondo y borde personalizables
@Composable
fun MyChipTag(label: String, textColor: Color, bgColor: Color, borderColor: Color) {
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

// Tarjeta de ejercicio propio con cuerpo clickable y botones de editar/eliminar
@Composable
fun MyExerciseCard(
    exercise: Exercise,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val diffColor = difficultyColor(exercise.difficulty)
    val pts       = difficultyPoints(exercise.difficulty)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = .07f), RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = .025f)),
    ) {
        // Cuerpo de la tarjeta: título, tags, descripción, autor y puntos
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Fila 1: título + badge "Oculto" si no está publicado
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
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
                    modifier      = Modifier.weight(1f).padding(end = 8.dp),
                    maxLines      = 2,
                    overflow      = TextOverflow.Ellipsis,
                )
                if (!exercise.isPublished) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = .06f))
                            .border(1.dp, Color.White.copy(alpha = .12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint     = ColorMuted,
                            modifier = Modifier.size(11.dp),
                        )
                        Text(
                            "Oculto",
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = ColorMuted,
                            fontFamily = SpaceGroteskFamily,
                        )
                    }
                }
            }

            // Fila 2: chips de categoría y dificultad
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (!exercise.category.isNullOrEmpty()) {
                    MyChipTag(
                        label       = categoryLabel(exercise.category),
                        textColor   = Color.White.copy(alpha = .8f),
                        bgColor     = Color.White.copy(alpha = .05f),
                        borderColor = Color.White.copy(alpha = .12f),
                    )
                }
                MyChipTag(
                    label       = exercise.difficulty.replaceFirstChar { it.uppercase() },
                    textColor   = diffColor,
                    bgColor     = diffColor.copy(alpha = .12f),
                    borderColor = diffColor.copy(alpha = .35f),
                )
            }

            // Fila 3: descripción corta truncada a 2 líneas
            val desc = exercise.shortDescription ?: exercise.description?.trimEnd()
            if (!desc.isNullOrEmpty()) {
                Text(
                    text       = desc,
                    fontSize   = 13.sp,
                    color      = Color.White.copy(alpha = .6f),
                    fontFamily = SpaceGroteskFamily,
                    lineHeight = (13 * 1.4f).sp,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                )
            }

            // Fila 4: avatar + autor a la izquierda, puntos a la derecha
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(ColorPrimary.copy(alpha = .30f))
                            .border(1.dp, ColorPrimary.copy(alpha = .55f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text       = exercise.author?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White,
                            fontFamily = SpaceGroteskFamily,
                        )
                    }
                    Text(
                        "Creado por ",
                        fontSize   = 12.sp,
                        color      = Color.White.copy(alpha = .55f),
                        fontFamily = SpaceGroteskFamily,
                    )
                    Text(
                        exercise.author ?: "—",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White.copy(alpha = .85f),
                        fontFamily = SpaceGroteskFamily,
                    )
                }
                if (pts > 0) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint     = ColorAccent,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            "$pts pts",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color      = ColorAccent,
                            fontFamily = JetBrainsMonoFamily,
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = .06f), thickness = 1.dp)

        // Botones de acción: editar (izquierda) y eliminar (derecha)
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onEdit)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("Editar", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ColorAccent, fontFamily = SpaceGroteskFamily)
            }

            VerticalDivider(color = Color.White.copy(alpha = .06f), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onDelete)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFB7185), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("Eliminar", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFB7185), fontFamily = SpaceGroteskFamily)
            }
        }
    }
}

// Diálogo de confirmación antes de eliminar un ejercicio de forma permanente
@Composable
internal fun DeleteConfirmDialog(
    deleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .clickable(enabled = false, onClick = {}),
                shape = RoundedCornerShape(20.dp),
                color = ColorSurface,
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFB7185).copy(alpha = .12f))
                                .border(1.dp, Color(0xFFFB7185).copy(alpha = .3f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint     = Color(0xFFFB7185),
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Column {
                            Text("Eliminar reto", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ColorTitle, fontFamily = SpaceGroteskFamily)
                            Text("Esta acción no se puede deshacer.", fontSize = 13.sp, color = ColorBody, fontFamily = SpaceGroteskFamily)
                        }
                    }

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(
                            onClick  = onDismiss,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape    = RoundedCornerShape(10.dp),
                            enabled  = !deleting,
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = ColorSubtitle),
                            border   = BorderStroke(1.dp, Color.White.copy(0.15f)),
                        ) {
                            Text("Cancelar", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick  = onConfirm,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape    = RoundedCornerShape(10.dp),
                            enabled  = !deleting,
                            colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB7185).copy(alpha = .85f)),
                        ) {
                            if (deleting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Eliminar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
