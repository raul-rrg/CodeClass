package com.codeclass.app.ui.screens.exercises.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.ui.screens.exercises.components.*
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.CreateExerciseViewModel
import com.codeclass.app.viewmodel.ExerciseForm

// Paso 2: definición de la firma de la función (nombre, parámetros y tipo de retorno)
@Composable
fun Step2Content(form: ExerciseForm, vm: CreateExerciseViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            StepHeader("Template de función", "Define la firma de la función que resolverán los usuarios.")
        }

        // Nombre de la función en formato monoespaciado
        item {
            FormField(label = "Nombre de función") {
                FormTextField(
                    value         = form.functionName,
                    onValueChange = vm::updateFunctionName,
                    placeholder   = "twoSum",
                    isMonospace   = true,
                )
            }
        }

        // Lista de parámetros con nombre, tipo y botón de eliminar
        item {
            FormField(label = "Parámetros") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    form.parameters.forEachIndexed { i, param ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                        ) {
                            FormTextField(
                                value         = param.name,
                                onValueChange = { vm.updateParam(i, name = it) },
                                placeholder   = "param${i + 1}",
                                isMonospace   = true,
                                modifier      = Modifier.weight(1f),
                            )
                            TypeDropdown(
                                selected = param.type,
                                onSelect = { vm.updateParam(i, type = it) },
                                modifier = Modifier.width(110.dp),
                            )
                            // Botón eliminar parámetro (deshabilitado si solo queda uno)
                            IconButton(
                                onClick  = { vm.removeParam(i) },
                                enabled  = form.parameters.size > 1,
                                modifier = Modifier.size(32.dp),
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    tint     = if (form.parameters.size > 1) Color(0xFFFB7185) else ColorMuted.copy(.3f),
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                    // Botón añadir parámetro (máximo 5)
                    if (form.parameters.size < 5) {
                        TextButton(
                            onClick        = { vm.addParam() },
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Añadir parámetro", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Tipo de dato que devuelve la función
        item {
            FormField(label = "Tipo de retorno") {
                TypeDropdown(
                    selected = form.returnType,
                    onSelect = vm::updateReturnType,
                    modifier = Modifier.width(140.dp),
                )
            }
        }
    }
}
