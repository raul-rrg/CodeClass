package com.codeclass.app.ui.screens.submissions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.R
import com.codeclass.app.data.model.SubmissionEntry
import com.codeclass.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Colores de estado de envío y métricas de rendimiento
internal val AcceptedClr = Color(0xFF34D399)
internal val RejectedClr = Color(0xFFFB7185)
private  val TimeClr     = Color(0xFF38BDF8)
private  val MemoryClr   = Color(0xFFA78BFA)

// Formatos de fecha soportados por el servidor (en orden de prueba)
private val DATE_FORMATS = listOf(
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault()),
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",        Locale.getDefault()),
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",    Locale.getDefault()),
)

// Convierte una fecha ISO 8601 en texto relativo ("hace 3m", "hace 2h"...)
internal fun timeAgo(createdAt: String?): String {
    if (createdAt == null) return ""
    val fecha: Date = DATE_FORMATS.firstNotNullOfOrNull { f ->
        runCatching { f.parse(createdAt) }.getOrNull()
    } ?: return ""
    val diff     = System.currentTimeMillis() - fecha.time
    val segundos = diff / 1000
    val minutos  = segundos / 60
    val horas    = minutos / 60
    val dias     = horas / 24
    return when {
        segundos < 60 -> "hace ${segundos}s"
        minutos  < 60 -> "hace ${minutos}m"
        horas    < 24 -> "hace ${horas}h"
        else          -> "hace ${dias}d"
    }
}

// Datos de estilo visual para el badge de lenguaje
private data class EstiloLenguaje(
    val fondo:     Color,
    val texto:     Color,
    val etiqueta:  String,
    val iniciales: String,
    val iconRes:   Int? = null,
)

// Tarjeta de envío individual: estado, puntuación, usuario, métricas y lenguaje
@Composable
internal fun SubmissionCard(entry: SubmissionEntry, showUser: Boolean) {
    val aceptado       = entry.status == "accepted"
    val colorEstado    = when (entry.status) {
        "accepted"         -> AcceptedClr
        "rejected"         -> RejectedClr
        "error", "timeout" -> Color(0xFFFB923C)
        else               -> Color(0xFFFB923C)
    }
    val etiquetaEstado = when (entry.status) {
        "accepted" -> "Aceptada"
        "rejected" -> "Rechazada"
        "error"    -> "Error"
        "timeout"  -> "Timeout"
        "running"  -> "Evaluando"
        "pending"  -> "Pendiente"
        else       -> entry.status.replaceFirstChar { it.uppercase() }
    }
    val puntuacion     = if (entry.passedCount != null && entry.totalCount != null)
        "${entry.passedCount}/${entry.totalCount}" else null
    val nombreMostrado = entry.userName ?: if (showUser) "?" else "Tú"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = .025f))
            .border(1.dp, Color.White.copy(alpha = .07f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Fila 1: icono de estado + etiqueta + puntuación | badge de lenguaje
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector        = if (aceptado) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint               = colorEstado,
                modifier           = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text       = etiquetaEstado,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = colorEstado,
                fontFamily = SpaceGroteskFamily,
            )
            if (puntuacion != null) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text       = puntuacion,
                    fontSize   = 12.sp,
                    color      = Color.White.copy(alpha = .5f),
                    fontFamily = JetBrainsMonoFamily,
                )
            }
            Spacer(Modifier.weight(1f))
            LangPill(entry.language)
        }

        // Fila 2: avatar + nombre + tiempo de ejecución + memoria + hora relativa
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserAvatar(nombreMostrado)
            Spacer(Modifier.width(8.dp))
            Text(
                text       = nombreMostrado,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium,
                color      = Color.White.copy(alpha = .85f),
                fontFamily = SpaceGroteskFamily,
            )
            entry.executionTime?.let { rt ->
                Spacer(Modifier.width(8.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint     = TimeClr.copy(alpha = .75f),
                        modifier = Modifier.size(11.dp),
                    )
                    Text(
                        text       = rt,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TimeClr.copy(alpha = .75f),
                        fontFamily = JetBrainsMonoFamily,
                    )
                }
            }
            entry.memoryUsed?.let { mem ->
                Spacer(Modifier.width(8.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Icon(
                        Icons.Outlined.Memory,
                        contentDescription = null,
                        tint     = MemoryClr.copy(alpha = .75f),
                        modifier = Modifier.size(11.dp),
                    )
                    Text(
                        text       = mem,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MemoryClr.copy(alpha = .75f),
                        fontFamily = JetBrainsMonoFamily,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                text       = timeAgo(entry.createdAt),
                fontSize   = 11.sp,
                color      = Color.White.copy(alpha = .45f),
                fontFamily = SpaceGroteskFamily,
                maxLines   = 1,
            )
        }
    }
}

// Badge de lenguaje con icono SVG si disponible, o iniciales como fallback
@Composable
private fun LangPill(language: String) {
    val estilo = when (language.lowercase()) {
        "javascript" -> EstiloLenguaje(Color(0xFFF7DF1E), Color(0xFF1A1A1A), "JavaScript", "JS", R.drawable.ic_lang_js)
        "python"     -> EstiloLenguaje(Color(0xFF3776AB), Color.White,       "Python",     "Py", R.drawable.ic_lang_python)
        "java"       -> EstiloLenguaje(Color(0xFFED8B00), Color.White,       "Java",       "Ja", R.drawable.ic_lang_java)
        else         -> EstiloLenguaje(
            Color(0xFF374151), Color.White,
            language.replaceFirstChar { it.uppercase() },
            language.take(2).replaceFirstChar { it.uppercase() },
        )
    }
    Row(
        modifier = Modifier
            .height(22.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(estilo.fondo)
            .padding(start = 4.dp, end = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (estilo.iconRes != null) {
            Icon(
                painter            = painterResource(estilo.iconRes),
                contentDescription = null,
                tint               = estilo.texto,
                modifier           = Modifier.size(14.dp),
            )
        } else {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(estilo.texto.copy(alpha = .15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = estilo.iniciales,
                    fontSize   = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color      = estilo.texto,
                    fontFamily = JetBrainsMonoFamily,
                )
            }
        }
        Text(
            text       = estilo.etiqueta,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            color      = estilo.texto,
            fontFamily = SpaceGroteskFamily,
        )
    }
}

// Avatar circular con la inicial del nombre del usuario
@Composable
private fun UserAvatar(nombre: String) {
    val inicial = nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(ColorPrimary.copy(alpha = .30f))
            .border(1.dp, ColorPrimary.copy(alpha = .55f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = inicial,
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            color      = Color.White,
            fontFamily = JetBrainsMonoFamily,
        )
    }
}
