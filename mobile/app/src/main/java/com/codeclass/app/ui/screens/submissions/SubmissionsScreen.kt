package com.codeclass.app.ui.screens.submissions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codeclass.app.data.api.RetrofitClient
import com.codeclass.app.data.local.TokenDataStore
import com.codeclass.app.data.model.SubmissionEntry
import com.codeclass.app.ui.screens.submissions.components.SubmissionCard
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.SubmissionsViewModel

// Pestañas disponibles en la pantalla de envíos
private enum class SubTab { MY, GLOBAL }

@Composable
fun SubmissionsScreen(
    exerciseId: Int,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val vm: SubmissionsViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            val ds = TokenDataStore(context)
            @Suppress("UNCHECKED_CAST")
            return SubmissionsViewModel(RetrofitClient.create(ds), exerciseId) as T
        }
    })

    val state by vm.state.collectAsState()
    var tabSeleccionada by remember { mutableStateOf(SubTab.MY) }

    // Cargar envíos globales al cambiar a esa pestaña
    LaunchedEffect(tabSeleccionada) {
        if (tabSeleccionada == SubTab.GLOBAL) vm.loadGlobalSubmissions()
    }

    Scaffold(containerColor = ColorBase) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            SubHeader(
                titulo     = state.exercise?.title ?: "",
                dificultad = state.exercise?.difficulty,
                onBack     = onBack,
            )
            SubTabRow(seleccionada = tabSeleccionada, onSeleccionar = { tabSeleccionada = it })

            when (tabSeleccionada) {
                SubTab.MY -> ListaEnvios(
                    items          = state.mySubmissions,
                    cargando       = state.isLoadingMy,
                    error          = state.myError,
                    onReintentar   = vm::loadMySubmissions,
                    mostrarUsuario = false,
                )
                SubTab.GLOBAL -> ListaEnvios(
                    items          = state.globalSubmissions,
                    cargando       = state.isLoadingGlobal,
                    error          = state.globalError,
                    onReintentar   = vm::loadGlobalSubmissions,
                    mostrarUsuario = true,
                )
            }
        }
    }
}

// Cabecera con botón volver, badge de dificultad y título del ejercicio
@Composable
private fun SubHeader(titulo: String, dificultad: String?, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBase)
            .statusBarsPadding()
            .padding(horizontal = 12.dp)
            .padding(top = 10.dp, bottom = 16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color.White.copy(alpha = .05f))
                .border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(11.dp))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint     = Color.White.copy(alpha = .7f),
                modifier = Modifier.size(18.dp),
            )
        }

        Spacer(Modifier.height(14.dp))

        Column(
            modifier            = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (dificultad != null) {
                val colorPill = when (dificultad) {
                    "easy"   -> Color(0xFF22C55E)
                    "medium" -> Color(0xFFFBBF24)
                    "hard"   -> Color(0xFFEF4444)
                    "insane" -> Color(0xFFA855F7)
                    else     -> Color(0xFF60A5FA)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(colorPill.copy(alpha = .12f))
                        .border(1.dp, colorPill.copy(alpha = .4f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text          = dificultad.uppercase(),
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = colorPill,
                        fontFamily    = SpaceGroteskFamily,
                        letterSpacing = 0.5.sp,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            Text(
                text          = titulo,
                fontSize      = 20.sp,
                fontWeight    = FontWeight.Bold,
                color         = Color.White,
                fontFamily    = SpaceGroteskFamily,
                maxLines      = 2,
                overflow      = TextOverflow.Ellipsis,
                letterSpacing = (-0.5).sp,
            )
        }
    }
}

// Fila de pestañas: Mis envíos / Envíos globales
@Composable
private fun SubTabRow(seleccionada: SubTab, onSeleccionar: (SubTab) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            SubTabItem(
                etiqueta = "Mis envíos",
                activa   = seleccionada == SubTab.MY,
                onClick  = { onSeleccionar(SubTab.MY) },
                modifier = Modifier.weight(1f),
            )
            SubTabItem(
                etiqueta = "Envíos globales",
                activa   = seleccionada == SubTab.GLOBAL,
                onClick  = { onSeleccionar(SubTab.GLOBAL) },
                modifier = Modifier.weight(1f),
            )
        }
        HorizontalDivider(color = Color.White.copy(alpha = .07f))
    }
}

@Composable
private fun SubTabItem(etiqueta: String, activa: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text       = etiqueta,
            fontSize   = 14.sp,
            fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium,
            color      = if (activa) Color.White else Color.White.copy(alpha = .45f),
            fontFamily = SpaceGroteskFamily,
            modifier   = Modifier.padding(top = 10.dp, bottom = 12.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(if (activa) Color.White else Color.Transparent),
        )
    }
}

// Lista de envíos con manejo de estados: cargando, error, vacío o lista completa
@Composable
private fun ListaEnvios(
    items: List<SubmissionEntry>,
    cargando: Boolean,
    error: String?,
    onReintentar: () -> Unit,
    mostrarUsuario: Boolean,
) {
    when {
        cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ColorPrimary, strokeWidth = 2.dp)
        }
        error != null -> Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "Error cargando envíos",
                color      = Color.White.copy(alpha = .5f),
                fontSize   = 14.sp,
                fontFamily = SpaceGroteskFamily,
            )
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = onReintentar) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = ColorPrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Reintentar", color = ColorPrimary, fontSize = 13.sp, fontFamily = SpaceGroteskFamily)
            }
        }
        items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Aún no hay envíos.",
                color      = Color.White.copy(alpha = .4f),
                fontSize   = 14.sp,
                fontFamily = SpaceGroteskFamily,
            )
        }
        else -> LazyColumn(
            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items, key = { it.id }) { entry ->
                SubmissionCard(entry = entry, showUser = mostrarUsuario)
            }
        }
    }
}
