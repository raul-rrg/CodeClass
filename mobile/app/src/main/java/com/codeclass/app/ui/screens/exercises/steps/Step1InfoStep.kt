package com.codeclass.app.ui.screens.exercises.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.codeclass.app.viewmodel.EXERCISE_CATEGORIES
import com.codeclass.app.viewmodel.EXERCISE_DIFFICULTIES
import com.codeclass.app.viewmodel.ExerciseForm

// Paso 1: información básica del ejercicio (título, descripción corta, dificultad, categoría y descripción completa)
@Composable
fun Step1Content(form: ExerciseForm, vm: CreateExerciseViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            StepHeader("Información básica", "Título, descripción y categoría del reto.")
        }

        // Título del ejercicio
        item {
            FormField(label = "Título", counter = "${form.title.length}/80") {
                FormTextField(
                    value         = form.title,
                    onValueChange = vm::updateTitle,
                    placeholder   = "Suma de dos números",
                    maxLength     = 80,
                )
            }
        }

        // Descripción corta visible en el listado de ejercicios
        item {
            FormField(label = "Descripción corta", counter = "${form.shortDescription.length}/200") {
                FormTextField(
                    value         = form.shortDescription,
                    onValueChange = vm::updateShortDesc,
                    placeholder   = "Breve descripción del reto",
                    maxLength     = 200,
                )
            }
        }

        // Chips de selección de dificultad con color según nivel
        item {
            FormField(label = "Dificultad") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EXERCISE_DIFFICULTIES.forEach { d ->
                        val seleccionado = form.difficulty == d
                        val color        = difficultyColor(d)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (seleccionado) color.copy(alpha = .15f) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (seleccionado) color.copy(alpha = .5f) else Color.White.copy(alpha = .1f),
                                    RoundedCornerShape(8.dp),
                                )
                                .clickable { vm.updateDifficulty(d) }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                        ) {
                            Text(
                                d.replaceFirstChar { it.uppercase() },
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color      = if (seleccionado) color else ColorMuted,
                                fontFamily = SpaceGroteskFamily,
                            )
                        }
                    }
                }
            }
        }

        // Dropdown de categoría del ejercicio
        item {
            var expandido by remember { mutableStateOf(false) }
            FormField(label = "Categoría") {
                Box {
                    OutlinedButton(
                        onClick        = { expandido = true },
                        shape          = RoundedCornerShape(10.dp),
                        colors         = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White.copy(alpha = .04f),
                            contentColor   = if (form.category.isEmpty()) ColorMuted else ColorTitle,
                        ),
                        border         = BorderStroke(1.dp, Color.White.copy(alpha = .1f)),
                        modifier       = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    ) {
                        Text(
                            if (form.category.isEmpty()) "Seleccionar categoría"
                            else form.category.replaceFirstChar { it.uppercase() },
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f),
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(
                        expanded         = expandido,
                        onDismissRequest = { expandido = false },
                        containerColor   = Color(0xFF1A1A2E),
                    ) {
                        EXERCISE_CATEGORIES.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        cat.replaceFirstChar { it.uppercase() },
                                        fontSize = 13.sp,
                                        color    = if (form.category == cat) ColorAccent else ColorSubtitle,
                                    )
                                },
                                onClick = { vm.updateCategory(cat); expandido = false },
                            )
                        }
                    }
                }
            }
        }

        // Descripción completa en Markdown (visible para el usuario al resolver el reto)
        item {
            FormField(label = "Descripción completa (Markdown)", counter = "${form.description.length}/5000") {
                OutlinedTextField(
                    value         = form.description,
                    onValueChange = { if (it.length <= 5000) vm.updateDescription(it) },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    placeholder   = {
                        Text(
                            "## Descripción\n\nDefine el problema aquí...",
                            color  = ColorMuted,
                            fontSize = 12.sp,
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
