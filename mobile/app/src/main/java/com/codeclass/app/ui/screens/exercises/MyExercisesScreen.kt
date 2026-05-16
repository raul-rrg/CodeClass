package com.codeclass.app.ui.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
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
import com.codeclass.app.ui.components.AppBottomBar
import com.codeclass.app.ui.components.AppTab
import com.codeclass.app.ui.screens.exercises.components.DeleteConfirmDialog
import com.codeclass.app.ui.screens.exercises.components.MyExerciseCard
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.MyExercisesViewModel

@Composable
fun MyExercisesScreen(
    onChallengesClick: () -> Unit,
    onRankingClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCreateClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onChallengeClick: (Int) -> Unit = {},
    isTeacher: Boolean = true,
) {
    val context = LocalContext.current
    val vm: MyExercisesViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MyExercisesViewModel(RetrofitClient.create(TokenDataStore(context))) as T
        }
    })

    val exercises by vm.exercises.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val deleting  by vm.deleting.collectAsState()

    var pendingDeleteId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        containerColor = ColorBase,
        bottomBar = {
            AppBottomBar(
                currentTab         = AppTab.MY_EXERCISES,
                onChallengesClick  = onChallengesClick,
                onRankingClick     = onRankingClick,
                onProfileClick     = onProfileClick,
                onMyExercisesClick = {},
                isTeacher          = isTeacher,
                isAuthenticated    = true,
            )
        },
    ) { innerPadding ->

        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {

            // ── Cabecera: título y botón de crear ─────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            "Mis Retos",
                            fontSize   = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color      = ColorTitle,
                            fontFamily = SpaceGroteskFamily,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Retos que has creado",
                            fontSize   = 14.sp,
                            color      = ColorBody,
                            fontFamily = SpaceGroteskFamily,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ColorPrimary)
                            .clickable(onClick = onCreateClick)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("Crear", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = SpaceGroteskFamily)
                        }
                    }
                }
            }

            // ── Skeleton de carga: 3 placeholders mientras llegan datos ──
            if (isLoading) {
                items(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 12.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ColorSurface),
                    )
                }
                return@LazyColumn
            }

            // ── Estado vacío: icono + mensaje + botón de crear ────
            if (exercises.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            Icons.Default.Assignment,
                            contentDescription = null,
                            tint     = ColorMuted.copy(0.35f),
                            modifier = Modifier.size(52.dp),
                        )
                        Text("Sin retos creados", fontSize = 15.sp, color = ColorMuted, fontFamily = SpaceGroteskFamily)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(ColorPrimary)
                                .clickable(onClick = onCreateClick)
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                        ) {
                            Text("Crear primer reto", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = SpaceGroteskFamily)
                        }
                    }
                }
                return@LazyColumn
            }

            // ── Lista de ejercicios ───────────────────────────────
            items(exercises, key = { it.id }) { exercise ->
                MyExerciseCard(
                    exercise = exercise,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 12.dp),
                    onClick  = { onChallengeClick(exercise.id) },
                    onEdit   = { onEditClick(exercise.id) },
                    onDelete = { pendingDeleteId = exercise.id },
                )
            }
        }
    }

    // ── Diálogo de confirmación de borrado ────────────────────────
    if (pendingDeleteId != null) {
        DeleteConfirmDialog(
            deleting  = deleting,
            onConfirm = { vm.deleteExercise(pendingDeleteId!!) { pendingDeleteId = null } },
            onDismiss = { if (!deleting) pendingDeleteId = null },
        )
    }
}
