package com.codeclass.app.ui.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.ui.screens.exercises.components.MyChipTag
import com.codeclass.app.ui.screens.exercises.components.difficultyColor
import com.codeclass.app.ui.screens.exercises.components.difficultyPoints
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.ExerciseForm

// Etiqueta de sección en mayúsculas para la vista previa
@Composable
private fun PreviewSectionLabel(label: String) {
    Text(
        label.uppercase(),
        fontSize      = 10.sp,
        fontWeight    = FontWeight.Bold,
        color         = ColorMuted,
        letterSpacing = 1.sp,
        fontFamily    = SpaceGroteskFamily,
    )
}

// Vista previa del reto tal como lo verán los usuarios al resolverlo
@Composable
fun PreviewContent(form: ExerciseForm, modifier: Modifier = Modifier) {
    val colorDificultad = difficultyColor(form.difficulty)
    val puntos          = difficultyPoints(form.difficulty)

    LazyColumn(
        modifier            = modifier.fillMaxSize(),
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Título y metadatos: categoría, dificultad y puntos
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text       = form.title.ifEmpty { "Sin título" },
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (form.title.isEmpty()) ColorMuted else ColorTitle,
                    fontFamily = SpaceGroteskFamily,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    if (form.category.isNotEmpty()) {
                        MyChipTag(
                            label       = form.category.replaceFirstChar { it.uppercase() },
                            textColor   = Color.White.copy(.8f),
                            bgColor     = Color.White.copy(.05f),
                            borderColor = Color.White.copy(.12f),
                        )
                    }
                    MyChipTag(
                        label       = form.difficulty.replaceFirstChar { it.uppercase() },
                        textColor   = colorDificultad,
                        bgColor     = colorDificultad.copy(.12f),
                        borderColor = colorDificultad.copy(.35f),
                    )
                    if (puntos > 0) {
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
                                "$puntos pts",
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color      = ColorAccent,
                                fontFamily = JetBrainsMonoFamily,
                            )
                        }
                    }
                }
            }
        }

        item { HorizontalDivider(color = Color.White.copy(alpha = .08f)) }

        // Descripción del reto (mostrada en texto plano aunque esté en Markdown)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PreviewSectionLabel("Descripción")
                if (form.description.isNotEmpty()) {
                    Text(
                        form.description,
                        fontSize   = 13.sp,
                        color      = ColorBody,
                        fontFamily = SpaceGroteskFamily,
                        lineHeight = 20.sp,
                    )
                } else {
                    Text("Sin descripción.", fontSize = 13.sp, color = ColorMuted)
                }
            }
        }

        // Firma de la función si el nombre está definido
        if (form.functionName.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PreviewSectionLabel("Función")
                    val parametros = form.parameters.joinToString(", ") {
                        "${it.name.ifEmpty { "?" }}: ${it.type}"
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = .04f))
                            .border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                    ) {
                        Text(
                            "${form.functionName}($parametros) → ${form.returnType}",
                            fontSize   = 13.sp,
                            color      = ColorAccent.copy(.9f),
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
        }

        // Ejemplos visibles: casos de prueba que no están marcados como ocultos
        val casosPublicos = form.testCases.filter { !it.isHidden }
        if (casosPublicos.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PreviewSectionLabel("Ejemplos (${casosPublicos.size})")
                    casosPublicos.forEachIndexed { idx, tc ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = .02f))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                "Ejemplo ${idx + 1}",
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = ColorMuted,
                                fontFamily = SpaceGroteskFamily,
                            )
                            // Inputs del ejemplo
                            form.parameters.forEachIndexed { j, param ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        param.name.ifEmpty { "arg${j + 1}" },
                                        fontSize   = 12.sp,
                                        color      = ColorAccent.copy(.7f),
                                        fontFamily = FontFamily.Monospace,
                                        modifier   = Modifier.width(60.dp),
                                    )
                                    Text(" = ", fontSize = 12.sp, color = ColorMuted)
                                    Text(
                                        tc.inputs.getOrElse(j) { "?" },
                                        fontSize   = 12.sp,
                                        color      = ColorSubtitle,
                                        fontFamily = FontFamily.Monospace,
                                    )
                                }
                            }
                            // Output esperado del ejemplo
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "→",
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = Color(0xFF34D399).copy(.8f),
                                    modifier   = Modifier.width(60.dp),
                                )
                                Text(
                                    tc.expectedOutput.ifEmpty { "?" },
                                    fontSize   = 12.sp,
                                    color      = Color(0xFF34D399),
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
