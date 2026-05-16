package com.codeclass.app.ui.screens.challenges

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
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
import com.codeclass.app.ui.screens.challenges.components.AuthModal
import com.codeclass.app.ui.screens.challenges.components.ChallengeCard
import com.codeclass.app.ui.screens.challenges.components.categoryLabel
import com.codeclass.app.ui.screens.challenges.components.difficultyLabel
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.ChallengesViewModel

private val DIFFICULTIES   = listOf("all", "easy", "medium", "hard", "insane")
private val CATEGORIES     = listOf("math", "strings", "arrays", "trees", "graphs", "sorting", "dynamic programming", "recursion")
private val STATUS_OPTIONS = listOf("all" to "Todos", "solved" to "Resueltos", "unsolved" to "Sin resolver")

@Composable
fun ChallengesScreen(
    onChallengeClick:   (Int) -> Unit,
    onSubmissionsClick: (Int) -> Unit,
    onRankingClick:     () -> Unit,
    onProfileClick:     () -> Unit,
    onMyExercisesClick: () -> Unit,
    onGoToLogin:        () -> Unit,
    onGoToRegister:     () -> Unit,
    isTeacher:          Boolean = false,
) {
    val context = LocalContext.current
    val vm: ChallengesViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            val ds = TokenDataStore(context)
            @Suppress("UNCHECKED_CAST")
            return ChallengesViewModel(RetrofitClient.create(ds), ds) as T
        }
    })

    val exercises       by vm.exercises.collectAsState()
    val isLoading       by vm.isLoading.collectAsState()
    val isLoadingMore   by vm.isLoadingMore.collectAsState()
    val hasMorePages    by vm.hasMorePages.collectAsState()
    val isAuthenticated by vm.isAuthenticated.collectAsState()
    val searchQuery     by vm.searchQuery.collectAsState()
    val difficulty      by vm.difficulty.collectAsState()
    val category        by vm.category.collectAsState()
    val statusFilter    by vm.statusFilter.collectAsState()

    LaunchedEffect(Unit) { vm.fetchExercises() }

    var showAuthModal          by remember { mutableStateOf(false) }
    var difficultyDropdownOpen by remember { mutableStateOf(false) }
    var categoryDropdownOpen   by remember { mutableStateOf(false) }
    var statusDropdownOpen     by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ColorBase,
        bottomBar = {
            AppBottomBar(
                currentTab         = AppTab.CHALLENGES,
                onChallengesClick  = {},
                onRankingClick     = onRankingClick,
                onProfileClick     = onProfileClick,
                onMyExercisesClick = onMyExercisesClick,
                isTeacher          = isTeacher,
                isAuthenticated    = isAuthenticated,
            )
        },
    ) { innerPadding ->

        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {

            // ── Cabecera: título y botón de login si no autenticado ──
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 20.dp),
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.Top,
                    ) {
                        Text(
                            "Retos de\nProgramación",
                            fontSize   = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color      = ColorTitle,
                            lineHeight = 36.sp,
                            modifier   = Modifier.weight(1f),
                        )
                        if (!isAuthenticated) {
                            OutlinedButton(
                                onClick        = onGoToLogin,
                                shape          = RoundedCornerShape(10.dp),
                                colors         = ButtonDefaults.outlinedButtonColors(contentColor = ColorAccent),
                                border         = BorderStroke(1.dp, ColorPrimary.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            ) {
                                Text("Iniciar sesión", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // ── Buscador ──
            item {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = vm::onSearchChange,
                    modifier      = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    placeholder   = { Text("Buscar reto...", color = ColorMuted) },
                    leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null, tint = ColorMuted) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedTextColor        = ColorTitle,
                        unfocusedTextColor      = ColorTitle,
                        focusedBorderColor      = ColorPrimary.copy(alpha = 0.5f),
                        unfocusedBorderColor    = Color.White.copy(alpha = 0.1f),
                        cursorColor             = ColorPrimary,
                        focusedContainerColor   = ColorSurface,
                        unfocusedContainerColor = ColorSurface,
                    ),
                )
                Spacer(Modifier.height(16.dp))
            }

            // ── Filtros de dificultad, estado y categoría ──
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        // Dificultad
                        Box {
                            FilterDropdownButton(
                                label   = difficultyLabel(difficulty),
                                active  = difficulty != "all",
                                onClick = { difficultyDropdownOpen = true },
                            )
                            DropdownMenu(
                                expanded         = difficultyDropdownOpen,
                                onDismissRequest = { difficultyDropdownOpen = false },
                                containerColor   = Color(0xFF1A1A2E),
                            ) {
                                DIFFICULTIES.forEach { d ->
                                    DropdownMenuItem(
                                        text    = { Text(difficultyLabel(d), fontSize = 13.sp, color = if (difficulty == d) ColorAccent else ColorSubtitle) },
                                        onClick = { vm.onDifficultyChange(d); difficultyDropdownOpen = false },
                                    )
                                }
                            }
                        }

                        // Estado
                        Box {
                            FilterDropdownButton(
                                label   = STATUS_OPTIONS.find { it.first == statusFilter }?.second ?: "Todos",
                                active  = statusFilter != "all",
                                onClick = { statusDropdownOpen = true },
                            )
                            DropdownMenu(
                                expanded         = statusDropdownOpen,
                                onDismissRequest = { statusDropdownOpen = false },
                                containerColor   = Color(0xFF1A1A2E),
                            ) {
                                STATUS_OPTIONS.forEach { (value, label) ->
                                    DropdownMenuItem(
                                        text    = { Text(label, fontSize = 13.sp, color = if (statusFilter == value) ColorAccent else ColorSubtitle) },
                                        onClick = { vm.onStatusFilterChange(value); statusDropdownOpen = false },
                                    )
                                }
                            }
                        }

                        // Categoría
                        Box {
                            FilterDropdownButton(
                                label   = if (category.isEmpty()) "Categorías" else categoryLabel(category),
                                active  = category.isNotEmpty(),
                                onClick = { categoryDropdownOpen = true },
                            )
                            DropdownMenu(
                                expanded         = categoryDropdownOpen,
                                onDismissRequest = { categoryDropdownOpen = false },
                                containerColor   = Color(0xFF1A1A2E),
                            ) {
                                DropdownMenuItem(
                                    text    = { Text("Categorías", fontSize = 13.sp, color = if (category.isEmpty()) ColorAccent else ColorSubtitle) },
                                    onClick = { vm.onCategoryChange(""); categoryDropdownOpen = false },
                                )
                                CATEGORIES.forEach { cat ->
                                    DropdownMenuItem(
                                        text    = { Text(categoryLabel(cat), fontSize = 13.sp, color = if (category == cat) ColorAccent else ColorSubtitle) },
                                        onClick = { vm.onCategoryChange(cat); categoryDropdownOpen = false },
                                    )
                                }
                            }
                        }
                    }

                    if (!isLoading) {
                        Spacer(Modifier.height(10.dp))
                        Text("${exercises.size} retos", fontSize = 11.sp, color = ColorMuted)
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // ── Skeleton de carga ──
            if (isLoading) {
                items(4) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 12.dp)
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ColorSurface),
                    )
                }
                return@LazyColumn
            }

            // ── Estado vacío ──
            if (exercises.isEmpty()) {
                item {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = ColorMuted.copy(0.4f), modifier = Modifier.size(48.dp))
                        Text("Sin resultados", fontSize = 14.sp, color = ColorMuted)
                        TextButton(onClick = {
                            vm.onDifficultyChange("all")
                            vm.onCategoryChange("")
                            vm.onSearchChange("")
                            vm.onStatusFilterChange("all")
                        }) {
                            Text("Limpiar filtros", fontSize = 13.sp, color = ColorAccent)
                        }
                    }
                }
                return@LazyColumn
            }

            // ── Lista de retos ──
            items(exercises, key = { it.id }) { exercise ->
                ChallengeCard(
                    exercise          = exercise,
                    isAuthenticated   = isAuthenticated,
                    modifier          = Modifier.padding(horizontal = 24.dp).padding(bottom = 12.dp),
                    onClick           = { if (isAuthenticated) onChallengeClick(exercise.id) else showAuthModal = true },
                    onViewSubmissions = { onSubmissionsClick(exercise.id) },
                )
            }

            // ── Cargar más / spinner ──
            if (hasMorePages || isLoadingMore) {
                item {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isLoadingMore) {
                            CircularProgressIndicator(color = ColorPrimary, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                        } else {
                            OutlinedButton(
                                onClick = { vm.loadNextPage() },
                                shape   = RoundedCornerShape(10.dp),
                                colors  = ButtonDefaults.outlinedButtonColors(contentColor = ColorAccent),
                                border  = BorderStroke(1.dp, ColorPrimary.copy(alpha = 0.4f)),
                            ) {
                                Text("Cargar más", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAuthModal) {
        AuthModal(
            onLogin    = { showAuthModal = false; onGoToLogin() },
            onRegister = { showAuthModal = false; onGoToRegister() },
            onDismiss  = { showAuthModal = false },
        )
    }
}

// Botón desplegable para los filtros con indicador de activo
@Composable
private fun FilterDropdownButton(label: String, active: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick        = onClick,
        shape          = RoundedCornerShape(10.dp),
        colors         = ButtonDefaults.outlinedButtonColors(
            containerColor = if (active) ColorPrimary.copy(0.1f) else ColorSurface,
            contentColor   = if (active) ColorAccent else ColorSubtitle,
        ),
        border         = BorderStroke(1.dp, if (active) ColorPrimary.copy(0.4f) else Color.White.copy(0.1f)),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.width(4.dp))
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
    }
}
