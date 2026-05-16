package com.codeclass.app.ui.screens.exercises.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.ui.theme.*

// Indicador visual de progreso entre pasos del formulario multipaso
@Composable
fun StepIndicator(
    steps: List<String>,
    currentStep: Int,
    isStepValid: (Int) -> Boolean,
    onStepClick: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        steps.forEachIndexed { i, label ->
            val paso       = i + 1
            val esCurrent  = paso == currentStep
            val esDone     = paso < currentStep && isStepValid(paso)
            val tieneError = paso < currentStep && !isStepValid(paso)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onStepClick(paso) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Círculo con estado: actual, completado, error o pendiente
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                esCurrent  -> ColorPrimary
                                esDone     -> Color(0xFF34D399).copy(alpha = .15f)
                                tieneError -> Color(0xFFFB7185).copy(alpha = .12f)
                                else       -> Color.White.copy(alpha = .06f)
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                esDone     -> Color(0xFF34D399).copy(alpha = .4f)
                                tieneError -> Color(0xFFFB7185).copy(alpha = .3f)
                                else       -> Color.Transparent
                            },
                            CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        esDone -> Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint     = Color(0xFF34D399),
                            modifier = Modifier.size(14.dp),
                        )
                        tieneError -> Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint     = Color(0xFFFB7185),
                            modifier = Modifier.size(12.dp),
                        )
                        else -> Text(
                            "$paso",
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color      = if (esCurrent) Color.White else ColorMuted,
                        )
                    }
                }

                Text(
                    label,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (esCurrent) Color.White else ColorMuted,
                    fontFamily = SpaceGroteskFamily,
                )
            }

            // Línea separadora entre pasos
            if (i < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .height(1.dp)
                        .align(Alignment.CenterVertically)
                        .background(Color.White.copy(alpha = .12f))
                )
            }
        }
    }
}
