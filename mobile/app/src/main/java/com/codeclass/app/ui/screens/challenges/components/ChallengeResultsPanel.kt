package com.codeclass.app.ui.screens.challenges.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.data.model.ExerciseDetail
import com.codeclass.app.data.model.RunResult
import com.codeclass.app.data.model.RunTestResult
import com.codeclass.app.data.model.SubmissionDetail
import com.codeclass.app.data.model.SubmissionResultDetail
import com.codeclass.app.data.model.TestCase
import com.codeclass.app.ui.theme.*

// Panel terminal con resultados de ejecución o de envío
@Composable
internal fun ResultsPanel(
    runResult:        RunResult?,
    submissionDetail: SubmissionDetail?,
    isEvaluating:     Boolean,
    actionError:      String?,
    exercise:         ExerciseDetail,
    onDismiss:        () -> Unit,
) {
    val subResults   = submissionDetail?.submissionResults ?: emptyList()
    val subAccepted  = submissionDetail?.status == "accepted"
    val subHasFatal  = subResults.firstOrNull()?.status == "compile_error"
    val runAllPassed = runResult != null && runResult.errorType == null && runResult.passedCount == runResult.totalCount

    val accentColor = when {
        isEvaluating                 -> ColorPrimary
        actionError != null          -> Color(0xFFEF4444)
        runResult?.errorType != null -> Color(0xFFEF4444)
        runAllPassed                 -> Color(0xFF22C55E)
        runResult != null            -> Color(0xFFEF4444)
        subAccepted                  -> Color(0xFF22C55E)
        submissionDetail != null     -> Color(0xFFEF4444)
        else                         -> ColorMuted
    }

    val errorText: String? = when {
        actionError != null          -> actionError
        runResult?.errorType != null -> runResult.compileOutput?.takeIf { it.isNotBlank() }
            ?: when (runResult.errorType) {
                "compile_error"       -> "Error de compilación"
                "runtime_error"       -> "Error en ejecución"
                "time_limit_exceeded" -> "Tiempo límite excedido"
                else                  -> runResult.errorType
            }
        subHasFatal -> submissionDetail?.compileOutput?.takeIf { it.isNotBlank() }
            ?: when (subResults.firstOrNull()?.status) {
                "compile_error"       -> "Error de compilación"
                "time_limit_exceeded" -> "Tiempo límite excedido"
                "runtime_error"       -> subResults.firstOrNull()?.error?.takeIf { it.isNotBlank() } ?: "Error en ejecución"
                else                  -> null
            }
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.44f)
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color(0xFF0A0C10))
            .border(1.dp, Color.White.copy(0.07f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
    ) {
        // Cabecera con etiqueta y botón de cerrar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D1117))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    ">_",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = accentColor,
                    fontFamily = JetBrainsMonoFamily,
                )
                Text(
                    if (submissionDetail != null || isEvaluating) "SUBMIT" else "SALIDA",
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.SemiBold,
                    color         = ColorMuted,
                    letterSpacing = 1.2.sp,
                    fontFamily    = SpaceGroteskFamily,
                )
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(22.dp)) {
                Icon(Icons.Default.Close, contentDescription = null, tint = ColorMuted, modifier = Modifier.size(14.dp))
            }
        }

        HorizontalDivider(color = Color.White.copy(0.06f))

        // Contenido scrollable: carga, errores y filas de resultados
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (isEvaluating) {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        CircularProgressIndicator(color = ColorPrimary, strokeWidth = 2.dp, modifier = Modifier.size(26.dp))
                        Text("Evaluando casos de prueba...", fontSize = 12.sp, color = ColorMuted, fontFamily = JetBrainsMonoFamily)
                    }
                }
            }

            if (errorText != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF4444).copy(0.08f))
                        .border(1.dp, Color(0xFFEF4444).copy(0.2f), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                ) {
                    Text(text = errorText, fontSize = 11.sp, color = Color(0xFFEF4444).copy(0.9f), fontFamily = JetBrainsMonoFamily, lineHeight = 18.sp)
                }
            }

            // Resultados de ejecución (run)
            if (runResult != null && runResult.errorType == null) {
                runResult.results.forEachIndexed { i, r ->
                    val tc = exercise.testCases.find { it.id == r.testCaseId }
                    TerminalTestRow(index = i + 1, result = r, params = exercise.parameters ?: emptyList(), testCase = tc)
                }
            }

            // Resultados de envío (submit)
            if (submissionDetail != null && !subHasFatal) {
                var visibleIdx = 0
                subResults.forEach { r ->
                    val exerciseTc = exercise.testCases.find { it.id == r.testCaseId }
                    val isHidden   = r.testCase?.isHidden ?: exerciseTc?.isHidden ?: false
                    SubmissionTerminalRow(
                        visibleIndex = if (!isHidden) ++visibleIdx else null,
                        result       = r,
                        params       = exercise.parameters ?: emptyList(),
                        testCase     = exerciseTc,
                    )
                }
            }
        }

        // Pie de panel con resumen de casos pasados
        val showRunFooter = runResult != null && runResult.errorType == null
        val showSubFooter = submissionDetail != null
        if (showRunFooter || showSubFooter) {
            HorizontalDivider(color = Color.White.copy(0.06f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1117))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (showRunFooter) {
                    Icon(
                        if (runAllPassed) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = null,
                        tint     = accentColor,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        "${runResult!!.passedCount}/${runResult.totalCount} casos de prueba",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = accentColor,
                        fontFamily = SpaceGroteskFamily,
                    )
                } else if (showSubFooter) {
                    val passed = subResults.count { it.passed }
                    val total  = subResults.size
                    Icon(
                        if (subAccepted) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = null,
                        tint     = accentColor,
                        modifier = Modifier.size(14.dp),
                    )
                    if (subAccepted) {
                        Text("ACEPTADO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentColor, fontFamily = SpaceGroteskFamily)
                        Text("·", fontSize = 12.sp, color = ColorMuted)
                    }
                    Text(
                        "$passed/$total casos de prueba",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = accentColor,
                        fontFamily = SpaceGroteskFamily,
                    )
                }
            }
        }
    }
}

// Fila de resultado de un caso de prueba al ejecutar (run)
@Composable
private fun TerminalTestRow(
    index:    Int,
    result:   RunTestResult,
    params:   List<Map<String, String>>,
    testCase: TestCase?,
) {
    val passed      = result.passed
    val statusColor = if (passed) Color(0xFF22C55E) else Color(0xFFEF4444)
    val timeLabel   = result.time?.let { " ${"%.3f".format(it)}s" } ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(statusColor.copy(0.04f))
            .border(1.dp, statusColor.copy(0.18f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text("caso $index", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ColorBody, fontFamily = JetBrainsMonoFamily)
            if (passed) Text("OK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor, fontFamily = JetBrainsMonoFamily)
            Text(timeLabel, fontSize = 11.sp, color = ColorMuted, fontFamily = JetBrainsMonoFamily)
        }

        val inputs = testCase?.input
        if (!inputs.isNullOrEmpty()) {
            Row(
                modifier              = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                inputs.forEachIndexed { j, value ->
                    val name = params.getOrNull(j)?.get("name") ?: "arg${j + 1}"
                    Text("$name=${value}", fontSize = 11.sp, color = ColorMuted, fontFamily = JetBrainsMonoFamily)
                }
                Text("→", fontSize = 11.sp, color = ColorMuted, fontFamily = JetBrainsMonoFamily)
                Text("salida=${result.output?.trim() ?: "-"}", fontSize = 11.sp, color = statusColor, fontFamily = JetBrainsMonoFamily)
            }
        }

        if (!passed && result.expectedOutput != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Esperado =", fontSize = 11.sp, color = ColorMuted, fontFamily = JetBrainsMonoFamily)
                Text(result.expectedOutput, fontSize = 11.sp, color = Color(0xFF22C55E), fontFamily = JetBrainsMonoFamily)
            }
        }
    }
}

// Fila de resultado al enviar: visible con detalle u oculto con pill
@Composable
private fun SubmissionTerminalRow(
    visibleIndex: Int?,
    result:       SubmissionResultDetail,
    params:       List<Map<String, String>>,
    testCase:     TestCase? = null,
) {
    val passed   = result.passed
    val isHidden = visibleIndex == null

    if (isHidden) {
        val pillBg     = if (passed) Color(0xFF22C55E).copy(alpha = 0.06f) else Color(0xFFA855F7).copy(alpha = 0.06f)
        val pillBorder = if (passed) Color(0xFF22C55E).copy(alpha = 0.15f) else Color(0xFFA855F7).copy(alpha = 0.15f)
        val pillFg     = if (passed) Color(0xFF22C55E).copy(alpha = 0.60f) else Color(0xFFA855F7).copy(alpha = 0.50f)
        val statusFg   = if (passed) Color(0xFF22C55E).copy(alpha = 0.80f) else Color(0xFFEF4444).copy(alpha = 0.60f)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(pillBg)
                .border(1.dp, pillBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = pillFg, modifier = Modifier.size(12.dp))
            Text("Caso oculto", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = pillFg, fontFamily = JetBrainsMonoFamily)
            Box(modifier = Modifier.width(1.dp).height(12.dp).background(pillBorder))
            Text(if (passed) "Pasó" else "Falló", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusFg, fontFamily = JetBrainsMonoFamily)
        }
    } else {
        val statusColor = if (passed) Color(0xFF22C55E) else Color(0xFFEF4444)
        val timeLabel   = result.executionTime?.let { "${"%.3f".format(it)}s" } ?: ""

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(statusColor.copy(0.04f))
                .border(1.dp, statusColor.copy(0.18f), RoundedCornerShape(8.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text("caso $visibleIndex", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ColorBody, fontFamily = JetBrainsMonoFamily)
                if (passed) Text("OK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor, fontFamily = JetBrainsMonoFamily)
                if (timeLabel.isNotEmpty()) Text(timeLabel, fontSize = 11.sp, color = ColorMuted, fontFamily = JetBrainsMonoFamily)
            }

            val inputs = testCase?.input
            if (!inputs.isNullOrEmpty()) {
                Row(
                    modifier              = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    inputs.forEachIndexed { j, v ->
                        val name = params.getOrNull(j)?.get("name") ?: "arg${j + 1}"
                        Text("$name=${formatValue(v)}", fontSize = 11.sp, color = ColorMuted, fontFamily = JetBrainsMonoFamily)
                    }
                    Text("→", fontSize = 11.sp, color = ColorMuted, fontFamily = JetBrainsMonoFamily)
                    Text("salida=${result.output?.trim() ?: "-"}", fontSize = 11.sp, color = statusColor, fontFamily = JetBrainsMonoFamily)
                }
            }

            if (!passed && !testCase?.expectedOutput.isNullOrBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Esperado =", fontSize = 11.sp, color = ColorMuted, fontFamily = JetBrainsMonoFamily)
                    Text(testCase!!.expectedOutput!!, fontSize = 11.sp, color = Color(0xFF22C55E), fontFamily = JetBrainsMonoFamily)
                }
            }
        }
    }
}
