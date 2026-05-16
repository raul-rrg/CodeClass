package com.codeclass.app.ui.screens.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codeclass.app.data.api.RetrofitClient
import com.codeclass.app.data.local.TokenDataStore
import com.codeclass.app.data.model.ExerciseDetail
import com.codeclass.app.ui.screens.challenges.components.CodigoTab
import com.codeclass.app.ui.screens.challenges.components.EnunciadoTab
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.ChallengeDetailViewModel

private enum class ChallengeTab { ENUNCIADO, CODIGO }

// Colores de dificultad para la vista de detalle (distintos a los del listado)
private fun difficultyColor(d: String?): Color = when (d) {
    "easy"   -> Color(0xFF22C55E)
    "medium" -> Color(0xFFF59E0B)
    "hard"   -> Color(0xFFEF4444)
    "insane" -> Color(0xFFA855F7)
    else     -> ColorPrimary
}

private fun difficultyPoints(d: String?): Int = when (d) {
    "easy"   -> 20
    "medium" -> 50
    "hard"   -> 100
    "insane" -> 200
    else     -> 0
}

@Composable
fun ChallengeScreen(exerciseId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val vm: ChallengeDetailViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            val ds = TokenDataStore(context)
            @Suppress("UNCHECKED_CAST")
            return ChallengeDetailViewModel(RetrofitClient.create(ds), exerciseId) as T
        }
    })

    val exercise  by vm.exercise.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error     by vm.error.collectAsState()

    var selectedTab by remember { mutableStateOf(ChallengeTab.ENUNCIADO) }

    Scaffold(containerColor = ColorBase) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            ChallengeTopBar(exercise = exercise, onBack = onBack)
            Spacer(Modifier.height(16.dp))
            ChallengeTabRow(selected = selectedTab, onSelect = { selectedTab = it })

            when {
                isLoading        -> ChallengeLoadingSkeleton()
                error != null    -> ChallengeErrorState(onRetry = vm::load)
                exercise != null -> when (selectedTab) {
                    ChallengeTab.ENUNCIADO -> EnunciadoTab(exercise!!)
                    ChallengeTab.CODIGO    -> CodigoTab(exercise = exercise!!, vm = vm)
                }
            }
        }
    }
}

// Barra superior con botón de volver, título y metadatos del reto
@Composable
private fun ChallengeTopBar(exercise: ExerciseDetail?, onBack: () -> Unit) {
    val diffColor = difficultyColor(exercise?.difficulty)
    val pts       = difficultyPoints(exercise?.difficulty)
    val diffLabel = exercise?.difficulty?.replaceFirstChar { it.uppercase() } ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBase)
            .statusBarsPadding()
            .padding(bottom = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 6.dp),
        ) {
            IconButton(
                onClick  = onBack,
                modifier = Modifier.align(Alignment.CenterStart).size(40.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint     = ColorBody,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text       = exercise?.title ?: "Cargando...",
            fontSize   = 26.sp,
            fontWeight = FontWeight.Bold,
            color      = ColorTitle,
            lineHeight = 32.sp,
            maxLines   = 2,
            textAlign  = TextAlign.Center,
            fontFamily = SpaceGroteskFamily,
            modifier   = Modifier.padding(horizontal = 52.dp),
        )

        if (exercise != null) {
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(7.dp))
                        .background(diffColor.copy(alpha = 0.12f))
                        .padding(horizontal = 9.dp, vertical = 4.dp),
                ) {
                    Text(diffLabel, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = diffColor, fontFamily = SpaceGroteskFamily)
                }

                if (pts > 0) {
                    Text("  ·  ", fontSize = 14.sp, color = ColorMuted)
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("$pts pts", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ColorAccent, fontFamily = SpaceGroteskFamily)
                }

                if (!exercise.category.isNullOrEmpty()) {
                    Text("  ·  ", fontSize = 14.sp, color = ColorMuted)
                    Text(exercise.category.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 14.sp, color = ColorBody, fontFamily = SpaceGroteskFamily)
                }

                if (exercise.isSolved) {
                    Text("  ·  ", fontSize = 14.sp, color = ColorMuted)
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}

// Selector de pestañas Enunciado / Código
@Composable
private fun ChallengeTabRow(selected: ChallengeTab, onSelect: (ChallengeTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 14.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D1117))
            .border(1.dp, Color.White.copy(0.06f), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        TabButton(label = "Enunciado", selected = selected == ChallengeTab.ENUNCIADO, onClick = { onSelect(ChallengeTab.ENUNCIADO) })
        TabButton(label = "Código",    selected = selected == ChallengeTab.CODIGO,    onClick = { onSelect(ChallengeTab.CODIGO) })
    }
}

@Composable
private fun RowScope.TabButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) ColorPrimary.copy(alpha = 0.85f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text          = label,
            fontSize      = 13.sp,
            fontWeight    = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color         = if (selected) Color.White else Color.White.copy(alpha = 0.35f),
            fontFamily    = SpaceGroteskFamily,
            letterSpacing = 0.2.sp,
        )
    }
}

// Placeholders de carga mientras se obtiene el detalle del reto
@Composable
private fun ChallengeLoadingSkeleton() {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(3) { i ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (i == 0) 60.dp else 120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorSurface),
            )
        }
    }
}

// Estado de error con botón de reintento
@Composable
private fun ChallengeErrorState(onRetry: () -> Unit) {
    Column(
        modifier            = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Error cargando el reto", color = ColorBody, fontSize = 14.sp)
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onRetry) {
            Text("Reintentar", color = ColorAccent, fontSize = 13.sp)
        }
    }
}
