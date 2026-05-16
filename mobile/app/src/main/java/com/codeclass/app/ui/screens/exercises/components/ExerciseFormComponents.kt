package com.codeclass.app.ui.screens.exercises.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.PARAM_TYPES

// Devuelve el color asociado a cada nivel de dificultad
fun difficultyColor(dificultad: String): Color = when (dificultad) {
    "easy"   -> Color(0xFF34D399)
    "medium" -> Color(0xFFFBBF24)
    "hard"   -> Color(0xFFFB7185)
    "insane" -> Color(0xFFA78BFA)
    else     -> ColorPrimary
}

// Devuelve los puntos que otorga cada nivel de dificultad
fun difficultyPoints(dificultad: String) = when (dificultad) {
    "easy" -> 20; "medium" -> 50; "hard" -> 100; "insane" -> 200; else -> 0
}

// Cabecera reutilizable para cada paso del formulario
@Composable
fun StepHeader(titulo: String, subtitulo: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            titulo,
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            color      = ColorTitle,
            fontFamily = SpaceGroteskFamily,
        )
        Text(
            subtitulo,
            fontSize   = 13.sp,
            color      = ColorBody,
            fontFamily = SpaceGroteskFamily,
        )
    }
}

// Envuelve un campo de formulario con etiqueta y contador de caracteres opcional
@Composable
fun FormField(
    label: String,
    counter: String? = null,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                label.uppercase(),
                fontSize      = 10.sp,
                fontWeight    = FontWeight.Bold,
                color         = ColorMuted,
                letterSpacing = 0.8.sp,
                fontFamily    = SpaceGroteskFamily,
            )
            if (counter != null) {
                Text(counter, fontSize = 10.sp, color = ColorMuted)
            }
        }
        content()
    }
}

// Campo de texto estilizado para el formulario, con soporte monoespaciado y longitud máxima
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isMonospace: Boolean = false,
    maxLength: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    OutlinedTextField(
        value         = value,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        modifier      = modifier,
        placeholder   = {
            Text(
                placeholder,
                color      = ColorMuted,
                fontSize   = 13.sp,
                fontFamily = if (isMonospace) FontFamily.Monospace else SpaceGroteskFamily,
            )
        },
        textStyle = LocalTextStyle.current.copy(
            fontFamily = if (isMonospace) FontFamily.Monospace else SpaceGroteskFamily,
            fontSize   = 13.sp,
            color      = ColorTitle,
        ),
        singleLine = true,
        shape      = RoundedCornerShape(10.dp),
        colors     = formTextFieldColors(),
    )
}

// Dropdown para seleccionar el tipo de dato de un parámetro o retorno
@Composable
fun TypeDropdown(selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            shape   = RoundedCornerShape(10.dp),
            colors  = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White.copy(alpha = .04f),
                contentColor   = ColorSubtitle,
            ),
            border         = BorderStroke(1.dp, Color.White.copy(alpha = .1f)),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
            modifier       = Modifier.fillMaxWidth(),
        ) {
            Text(selected, fontSize = 12.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(14.dp))
        }
        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            containerColor   = Color(0xFF1A1A2E),
        ) {
            PARAM_TYPES.forEach { tipo ->
                DropdownMenuItem(
                    text = {
                        Text(
                            tipo,
                            fontSize   = 13.sp,
                            color      = if (selected == tipo) ColorAccent else ColorSubtitle,
                            fontFamily = FontFamily.Monospace,
                        )
                    },
                    onClick = { onSelect(tipo); expanded = false },
                )
            }
        }
    }
}

// Colores compartidos para todos los OutlinedTextField del formulario
@Composable
fun formTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor        = ColorTitle,
    unfocusedTextColor      = ColorTitle,
    focusedBorderColor      = ColorPrimary.copy(alpha = 0.5f),
    unfocusedBorderColor    = Color.White.copy(alpha = 0.1f),
    cursorColor             = ColorPrimary,
    focusedContainerColor   = Color.White.copy(alpha = .04f),
    unfocusedContainerColor = Color.White.copy(alpha = .04f),
)

// Placeholder de ejemplo según el tipo de dato esperado en el input/output
fun inputPlaceholder(tipo: String) = when (tipo) {
    "int"    -> "0"
    "float"  -> "3.14"
    "string" -> "\"hola\""
    "bool"   -> "true"
    "array"  -> "[1, 2, 3]"
    else     -> "..."
}
