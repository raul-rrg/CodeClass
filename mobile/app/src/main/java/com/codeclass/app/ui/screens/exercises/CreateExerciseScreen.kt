package com.codeclass.app.ui.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codeclass.app.data.api.RetrofitClient
import com.codeclass.app.data.local.TokenDataStore
import com.codeclass.app.ui.screens.exercises.components.StepIndicator
import com.codeclass.app.ui.screens.exercises.steps.*
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.CreateExerciseViewModel

// Etiquetas de los 4 pasos del formulario
private val STEP_LABELS = listOf("Información", "Template", "Casos de prueba", "Publicar")

// Pantalla principal para crear o editar un ejercicio, con formulario multipaso y vista previa
@Composable
fun CreateExerciseScreen(
    exerciseId: Int?,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
) {
    val context = LocalContext.current
    val vm: CreateExerciseViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CreateExerciseViewModel(RetrofitClient.create(TokenDataStore(context)), exerciseId) as T
        }
    })

    val form    by vm.form.collectAsState()
    val loading by vm.loading.collectAsState()
    val error   by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    var pasoActual      by remember { mutableIntStateOf(1) }
    var tabSeleccionada by remember { mutableIntStateOf(0) } // 0=Formulario, 1=Vista Previa

    // Navegar a éxito cuando el ejercicio se crea o guarda correctamente
    LaunchedEffect(success) { if (success) onSuccess() }

    // Limpiar el mensaje de error al cambiar de paso
    LaunchedEffect(pasoActual) { vm.clearError() }

    Scaffold(
        containerColor = ColorBase,
        topBar = {
            Column {
                // Barra superior: botón volver y título dinámico
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = ColorSubtitle)
                    }
                    Text(
                        if (vm.isEditMode) "Editar reto" else "Crear reto",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = ColorTitle,
                        fontFamily = SpaceGroteskFamily,
                    )
                    // Espaciador para centrar el título visualmente
                    Spacer(Modifier.size(48.dp))
                }

                // Tabs para alternar entre el formulario y la vista previa
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColorSurface)
                        .padding(4.dp),
                ) {
                    listOf("Formulario", "Vista Previa").forEachIndexed { index, label ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (tabSeleccionada == index) ColorPrimary else Color.Transparent)
                                .clickable { tabSeleccionada = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                label,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = if (tabSeleccionada == index) Color.White else ColorMuted,
                                fontFamily = SpaceGroteskFamily,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))

                // Indicador de pasos solo visible en la pestaña de formulario
                if (tabSeleccionada == 0) {
                    StepIndicator(
                        steps       = STEP_LABELS,
                        currentStep = pasoActual,
                        isStepValid = { vm.isStepValid(it) },
                        onStepClick = { destino ->
                            // Solo permite avanzar si todos los pasos anteriores son válidos
                            if (destino < pasoActual || (1 until destino).all { vm.isStepValid(it) }) {
                                pasoActual = destino
                            }
                        },
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = .07f), thickness = 1.dp)
            }
        },
    ) { innerPadding ->

        if (tabSeleccionada == 0) {
            // Contenido del formulario multipaso
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                // Banner de error cuando alguna operación falla
                if (error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFB7185).copy(alpha = .1f))
                            .border(1.dp, Color(0xFFFB7185).copy(alpha = .3f), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                    ) {
                        Text(error ?: "", fontSize = 13.sp, color = Color(0xFFFB7185))
                    }
                }

                // Contenido del paso actual
                Box(modifier = Modifier.weight(1f)) {
                    when (pasoActual) {
                        1 -> Step1Content(form, vm)
                        2 -> Step2Content(form, vm)
                        3 -> Step3Content(form, vm)
                        4 -> Step4Content(form, vm)
                    }
                }

                // Navegación inferior: atrás/cancelar, puntos de progreso y siguiente/enviar
                HorizontalDivider(color = Color.White.copy(alpha = .07f), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    // Botón izquierdo: cancelar en paso 1, atrás en el resto
                    if (pasoActual == 1) {
                        TextButton(onClick = onBack) {
                            Text("Cancelar", color = Color(0xFFFB7185).copy(alpha = .8f), fontSize = 13.sp)
                        }
                    } else {
                        TextButton(onClick = { pasoActual-- }) {
                            Icon(
                                Icons.Default.ChevronLeft,
                                contentDescription = null,
                                tint     = ColorSubtitle,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(2.dp))
                            Text("Atrás", color = ColorSubtitle, fontSize = 13.sp)
                        }
                    }

                    // Puntos de progreso: el activo es más ancho
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        STEP_LABELS.forEachIndexed { i, _ ->
                            val paso = i + 1
                            Box(
                                modifier = Modifier
                                    .width(if (paso == pasoActual) 20.dp else 6.dp)
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(if (paso == pasoActual) ColorPrimary else Color.White.copy(.15f)),
                            )
                        }
                    }

                    // Botón derecho: siguiente o crear/guardar en el último paso
                    if (pasoActual < STEP_LABELS.size) {
                        Button(
                            onClick        = { pasoActual++ },
                            enabled        = vm.isStepValid(pasoActual),
                            shape          = RoundedCornerShape(10.dp),
                            colors         = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text("Siguiente", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(2.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        Button(
                            onClick        = { vm.submit() },
                            enabled        = !loading,
                            shape          = RoundedCornerShape(10.dp),
                            colors         = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    color       = Color.White,
                                    modifier    = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Text(
                                    if (vm.isEditMode) "Guardar" else "Crear",
                                    fontSize   = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Pestaña de vista previa del reto
            PreviewContent(form = form, modifier = Modifier.padding(innerPadding))
        }
    }
}
