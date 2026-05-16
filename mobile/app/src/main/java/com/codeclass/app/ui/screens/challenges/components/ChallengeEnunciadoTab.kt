package com.codeclass.app.ui.screens.challenges.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.data.model.ExerciseDetail
import com.codeclass.app.data.model.TestCase
import com.codeclass.app.ui.theme.*

// Formatea un valor del input: 1.0 → "1", 3.14 → "3.14"
internal fun formatValue(v: Any?): String = when {
    v == null                          -> "null"
    v is Double && v % 1.0 == 0.0     -> v.toLong().toString()
    else                               -> v.toString()
}

// Pestaña de enunciado: descripción, ejemplo y casos de prueba visibles
@Composable
fun EnunciadoTab(exercise: ExerciseDetail) {
    val visibleTestCases = exercise.testCases.filter { !it.isHidden }
    val exampleCase      = visibleTestCases.firstOrNull()

    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        contentPadding  = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Autor del reto
        item {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AuthorAvatar(exercise.author ?: "?")
                Text("Creado por ", fontSize = 13.sp, color = ColorBody)
                Text(
                    exercise.author ?: "Unknown",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = ColorSubtitle,
                )
            }
        }

        // Descripción en Markdown
        if (!exercise.description.isNullOrBlank()) {
            item {
                SectionBlock(title = "DESCRIPTION") {
                    MarkdownText(exercise.description)
                }
            }
        }

        // Primer caso visible como ejemplo
        if (exampleCase != null) {
            item {
                SectionBlock(title = "EXAMPLE") {
                    ExampleBox(testCase = exampleCase, parameters = exercise.parameters)
                }
            }
        }

        // Resto de casos visibles
        if (visibleTestCases.isNotEmpty()) {
            item { SectionLabel("TEST CASES") }
            itemsIndexed(visibleTestCases) { index, tc ->
                TestCaseCard(index = index + 1, testCase = tc, parameters = exercise.parameters)
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// Contenedor de sección con etiqueta y contenido
@Composable
private fun SectionBlock(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel(title)
        content()
    }
}

// Etiqueta de sección en mayúsculas con letter spacing
@Composable
internal fun SectionLabel(title: String) {
    Text(
        text          = title,
        fontSize      = 10.sp,
        fontWeight    = FontWeight.SemiBold,
        color         = ColorMuted,
        letterSpacing = 1.5.sp,
    )
}

// Caja de ejemplo con inputs y output del primer caso visible
@Composable
private fun ExampleBox(testCase: TestCase, parameters: List<Map<String, String>>?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D1117))
            .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (!testCase.input.isNullOrEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Input:", fontSize = 13.sp, color = ColorMuted, fontFamily = JetBrainsMonoFamily)
                testCase.input.forEachIndexed { j, v ->
                    val paramName = parameters?.getOrNull(j)?.get("name") ?: "arg${j + 1}"
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(paramName,        fontSize = 13.sp, color = ColorAccent,   fontFamily = JetBrainsMonoFamily)
                        Text("=",              fontSize = 13.sp, color = ColorMuted,    fontFamily = JetBrainsMonoFamily)
                        Text(formatValue(v),   fontSize = 13.sp, color = ColorSubtitle, fontFamily = JetBrainsMonoFamily)
                    }
                }
            }
        }
        if (!testCase.expectedOutput.isNullOrBlank()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Output:", fontSize = 13.sp, color = ColorMuted, fontFamily = JetBrainsMonoFamily)
                Text(testCase.expectedOutput, fontSize = 13.sp, color = Color(0xFF22C55E), fontFamily = JetBrainsMonoFamily)
            }
        }
    }
}

// Tarjeta de caso de prueba con número, inputs y output
@Composable
private fun TestCaseCard(index: Int, testCase: TestCase, parameters: List<Map<String, String>>?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ColorSurface)
            .border(1.dp, Color.White.copy(0.06f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(ColorPrimary.copy(0.15f))
                    .border(1.dp, ColorPrimary.copy(0.3f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("$index", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorPrimary)
            }
            Text("Test case", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ColorSubtitle)
        }

        HorizontalDivider(color = Color.White.copy(0.05f))

        if (!testCase.input.isNullOrEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("INPUT", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = ColorMuted, letterSpacing = 1.sp)
                testCase.input.forEachIndexed { j, v ->
                    val paramName = parameters?.getOrNull(j)?.get("name") ?: "arg${j + 1}"
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(paramName,      fontSize = 13.sp, color = ColorAccent,   fontFamily = JetBrainsMonoFamily)
                        Text("=",            fontSize = 13.sp, color = ColorMuted,    fontFamily = JetBrainsMonoFamily)
                        Text(formatValue(v), fontSize = 13.sp, color = ColorSubtitle, fontFamily = JetBrainsMonoFamily)
                    }
                }
            }
        }

        if (!testCase.expectedOutput.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("OUTPUT", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = ColorMuted, letterSpacing = 1.sp)
                Text(testCase.expectedOutput, fontSize = 13.sp, color = Color(0xFF22C55E), fontFamily = JetBrainsMonoFamily)
            }
        }
    }
}

// Avatar circular con la inicial del nombre del autor
@Composable
internal fun AuthorAvatar(name: String) {
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(ColorPrimary.copy(0.15f))
            .border(1.dp, ColorPrimary.copy(0.3f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(initial, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorPrimary)
    }
}
