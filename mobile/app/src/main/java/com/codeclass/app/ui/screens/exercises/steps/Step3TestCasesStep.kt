package com.codeclass.app.ui.screens.exercises.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.codeclass.app.ui.screens.exercises.components.*
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.CreateExerciseViewModel
import com.codeclass.app.viewmodel.ExerciseForm

// Paso 3: definición de casos de prueba con inputs y output esperado
@Composable
fun Step3Content(form: ExerciseForm, vm: CreateExerciseViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            StepHeader("Casos de prueba", "Mínimo 2 casos: al menos 1 visible y 1 oculto.")
        }

        itemsIndexed(form.testCases) { i, tc ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = .02f)),
            ) {
                // Cabecera del caso: número, toggle oculto y botón eliminar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = .02f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        "Caso ${i + 1}",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = ColorMuted,
                        fontFamily = SpaceGroteskFamily,
                    )
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Toggle para marcar el caso como oculto (no visible para el usuario que resuelve)
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier              = Modifier.clickable { vm.toggleTestHidden(i) },
                        ) {
                            Text(
                                "Oculto",
                                fontSize   = 11.sp,
                                color      = ColorMuted,
                                fontFamily = SpaceGroteskFamily,
                            )
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(20.dp)
                                    .clip(CircleShape)
                                    .background(if (tc.isHidden) ColorPrimary else Color.White.copy(.12f)),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(
                                            start  = if (tc.isHidden) 18.dp else 2.dp,
                                            top    = 2.dp,
                                            bottom = 2.dp,
                                            end    = 2.dp,
                                        )
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                )
                            }
                        }
                        // Botón eliminar caso (solo visible si hay más de 2)
                        if (form.testCases.size > 2) {
                            IconButton(
                                onClick  = { vm.removeTestCase(i) },
                                modifier = Modifier.size(28.dp),
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint     = Color(0xFFFB7185).copy(.7f),
                                    modifier = Modifier.size(15.dp),
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = .05f))

                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Sección INPUT: un campo por cada parámetro definido en el template
                    Text(
                        "INPUT",
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = ColorMuted.copy(.6f),
                        letterSpacing = 1.sp,
                        fontFamily    = SpaceGroteskFamily,
                    )
                    form.parameters.forEachIndexed { j, param ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                param.name.ifEmpty { "arg${j + 1}" },
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = ColorAccent.copy(.8f),
                                fontFamily = FontFamily.Monospace,
                            )
                            FormTextField(
                                value         = tc.inputs.getOrElse(j) { "" },
                                onValueChange = { vm.updateTestInput(i, j, it) },
                                placeholder   = inputPlaceholder(param.type),
                                isMonospace   = true,
                                modifier      = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = .05f))

                    // Sección OUTPUT ESPERADO
                    Text(
                        "OUTPUT ESPERADO",
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = ColorMuted.copy(.6f),
                        letterSpacing = 1.sp,
                        fontFamily    = SpaceGroteskFamily,
                    )
                    FormTextField(
                        value         = tc.expectedOutput,
                        onValueChange = { vm.updateTestExpected(i, it) },
                        placeholder   = inputPlaceholder(form.returnType),
                        isMonospace   = true,
                        modifier      = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        // Botón para añadir un nuevo caso de prueba (máximo 15)
        if (form.testCases.size < 15) {
            item {
                OutlinedButton(
                    onClick  = { vm.addTestCase() },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    border   = BorderStroke(1.dp, Color.White.copy(alpha = .1f)),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = ColorMuted),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Añadir caso de prueba", fontSize = 13.sp)
                }
            }
        }
    }
}
