package com.codeclass.app.ui.screens.ranking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codeclass.app.data.api.RetrofitClient
import com.codeclass.app.data.local.TokenDataStore
import com.codeclass.app.data.model.LeaderboardEntry
import com.codeclass.app.ui.components.AppBottomBar
import com.codeclass.app.ui.components.AppTab
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.RankingViewModel

@Composable
fun RankingScreen(
    onChallengesClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMyExercisesClick: () -> Unit,
    onGoToLogin: () -> Unit = {},
    isTeacher: Boolean = false,
    isAuthenticated: Boolean = false,
) {
    val context = LocalContext.current
    val vm: RankingViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RankingViewModel(RetrofitClient.create(TokenDataStore(context))) as T
        }
    })

    val entries by vm.entries.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val currentUserId by vm.currentUserId.collectAsState()

    val top3   = entries.filter { it.position <= 3 }
    val others = entries.filter { it.position > 3 }

    Scaffold(
        containerColor = ColorBase,
        bottomBar = {
            AppBottomBar(
                currentTab         = AppTab.RANKING,
                onChallengesClick  = onChallengesClick,
                onRankingClick     = {},
                onProfileClick     = onProfileClick,
                onMyExercisesClick = onMyExercisesClick,
                isTeacher          = isTeacher,
                isAuthenticated    = isAuthenticated,
            )
        },
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorBase)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = ColorPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorBase)
                    .padding(innerPadding)
                    .systemBarsPadding()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text  = "Ranking",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize   = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color      = ColorTitle,
                            ),
                        )
                        if (!isAuthenticated) {
                            OutlinedButton(
                                onClick = onGoToLogin,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = ColorAccent,
                                ),
                                border = BorderStroke(1.dp, ColorPrimary.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            ) {
                                Text("Iniciar sesión", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                if (top3.size == 3) {
                    item {
                        PodiumSection(top3, currentUserId)
                        Spacer(Modifier.height(8.dp))
                    }
                }

                items(others) { entry ->
                    RankRow(entry, isCurrentUser = entry.userId == currentUserId)
                }

                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun PodiumSection(top3: List<LeaderboardEntry>, currentUserId: Int?) {
    val first  = top3.firstOrNull { it.position == 1 } ?: return
    val second = top3.firstOrNull { it.position == 2 } ?: return
    val third  = top3.firstOrNull { it.position == 3 } ?: return

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.Bottom,
    ) {
        PodiumCard(entry = second, cardHeight = 110.dp, currentUserId = currentUserId)
        PodiumCard(entry = first,  cardHeight = 140.dp, highlighted = true, currentUserId = currentUserId)
        PodiumCard(entry = third,  cardHeight = 110.dp, currentUserId = currentUserId)
    }
}

@Composable
private fun PodiumCard(
    entry: LeaderboardEntry,
    cardHeight: Dp,
    highlighted: Boolean = false,
    currentUserId: Int?,
) {
    val medalColor = when (entry.position) {
        1    -> Color(0xFFF59E0B)
        2    -> Color(0xFF9CA3AF)
        else -> Color(0xFFB45309)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.width(108.dp),
    ) {
        AvatarCircle(
            name          = entry.name,
            size          = 48.dp,
            highlighted   = highlighted,
            isCurrentUser = entry.userId == currentUserId,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text  = entry.name,
            style = MaterialTheme.typography.labelMedium.copy(
                color      = ColorTitle,
                fontWeight = FontWeight.SemiBold,
            ),
            maxLines = 1,
        )
        Text(
            text  = "${entry.totalPoints} pts",
            style = MaterialTheme.typography.labelSmall.copy(color = ColorBody),
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    if (highlighted)
                        Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF1E3A5F)))
                    else
                        Brush.verticalGradient(listOf(Color(0xFF1F2937), Color(0xFF111827)))
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier         = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ColorSurface),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = entry.position.toString(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color      = medalColor,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    }
}

@Composable
private fun AvatarCircle(
    name: String,
    size: Dp,
    highlighted: Boolean = false,
    isCurrentUser: Boolean = false,
) {
    val initial = name.firstOrNull()?.uppercaseChar() ?: '?'
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (highlighted) Color(0xFF1E40AF) else Color(0xFF1F2937))
            .then(
                if (isCurrentUser) Modifier.border(2.dp, ColorPrimary, CircleShape)
                else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = initial.toString(),
            style = MaterialTheme.typography.titleMedium.copy(
                color      = ColorTitle,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Composable
private fun RankRow(entry: LeaderboardEntry, isCurrentUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isCurrentUser) Color(0xFF1A2744) else ColorSurface)
            .then(
                if (isCurrentUser)
                    Modifier.border(
                        width = 1.dp,
                        color = ColorPrimary.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp),
                    )
                else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text     = entry.position.toString(),
            style    = MaterialTheme.typography.labelLarge.copy(color = ColorMuted),
            modifier = Modifier.width(22.dp),
        )
        Spacer(Modifier.width(12.dp))
        AvatarCircle(name = entry.name, size = 36.dp, isCurrentUser = isCurrentUser)
        Spacer(Modifier.width(12.dp))
        Row(
            modifier          = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text  = entry.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color      = ColorTitle,
                    fontWeight = FontWeight.Medium,
                ),
            )
            if (isCurrentUser) {
                Text(
                    text  = " · YOU",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color      = ColorPrimary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
        Text(
            text  = entry.totalPoints.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                color      = ColorAccent,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}
