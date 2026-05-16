package com.codeclass.app.ui.screens.exercises.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.ui.screens.exercises.components.*
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.CreateExerciseViewModel
import com.codeclass.app.viewmodel.ExerciseForm
import com.codeclass.app.viewmodel.SOLUTION_LANGUAGES

// Paso 4: visibilidad del reto y solución de referencia opcional
@Composable
fun Step4Content(form: ExerciseForm, vm: CreateExerciseViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            StepHeader(
                if (vm.isEditMode) "Guardar cambios" else "Publicar reto",
                if (vm.isEditMode) "Revisa y guarda los cambios."
                else "Configura la visibilidad y añade una solución de referencia.",
            )
        }

        // Toggle público/privado del reto
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = .1f), RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = .025f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        if (form.isPublished) "Reto público" else "Reto privado",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = ColorTitle,
                        fontFamily = SpaceGroteskFamily,
                    )
                    Text(
                        if (form.isPublished) "Visible para todos los usuarios."
                        else "Solo tú puedes verlo.",
                        fontSize   = 12.sp,
                        color      = ColorBody,
                        fontFamily = SpaceGroteskFamily,
                    )
                }
                // Switch personalizado de visibilidad
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(26.dp)
                        .clip(CircleShape)
                        .background(if (form.isPublished) ColorPrimary else Color.White.copy(.12f))
                        .clickable { vm.updateIsPublished(!form.isPublished) },
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(
                                start  = if (form.isPublished) 24.dp else 2.dp,
                                top    = 2.dp,
                                bottom = 2.dp,
                                end    = 2.dp,
                            )
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                    )
                }
            }
        }

        // Bloque de solución de referencia (opcional, solo visible para el creador)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = .1f), RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = .025f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Solución de referencia",
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = ColorTitle,
                            fontFamily = SpaceGroteskFamily,
                        )
                        Text(
                            "Opcional. Solo visible para ti.",
                            fontSize   = 12.sp,
                            color      = ColorBody,
                            fontFamily = SpaceGroteskFamily,
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = .06f))

                // Chips para elegir el lenguaje de programación de la solución
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SOLUTION_LANGUAGES.forEach { lang ->
                        val seleccionado = form.solutionLanguage == lang
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (seleccionado) ColorPrimary.copy(.15f) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (seleccionado) ColorPrimary.copy(.4f) else Color.White.copy(.08f),
                                    RoundedCornerShape(8.dp),
                                )
                                .clickable { vm.updateSolutionLanguage(lang) }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                        ) {
                            Text(
                                lang.replaceFirstChar { it.uppercase() },
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = if (seleccionado) ColorAccent else ColorMuted,
                                fontFamily = SpaceGroteskFamily,
                            )
                        }
                    }
                }

                // Editor de código para escribir la solución de referencia
                OutlinedTextField(
                    value         = form.solutionCode,
                    onValueChange = vm::updateSolutionCode,
                    modifier      = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    placeholder   = {
                        Text(
                            "def ${form.functionName.ifEmpty { "solution" }}(...):\n    pass",
                            color      = ColorMuted,
                            fontSize   = 12.sp,
                            fontFamily = FontFamily.Monospace,
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize   = 12.sp,
                        color      = ColorTitle,
                    ),
                    shape           = RoundedCornerShape(10.dp),
                    colors          = formTextFieldColors(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
                )
            }
        }
    }
}
