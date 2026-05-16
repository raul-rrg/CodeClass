package com.codeclass.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.ui.theme.ColorMuted
import com.codeclass.app.ui.theme.ColorPrimary
import com.codeclass.app.ui.theme.ColorSurface

enum class AppTab { CHALLENGES, RANKING, PROFILE, MY_EXERCISES }

@Composable
fun AppBottomBar(
    currentTab: AppTab,
    onChallengesClick: () -> Unit,
    onRankingClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMyExercisesClick: () -> Unit,
    isTeacher: Boolean = false,
    isAuthenticated: Boolean = false,
) {
    NavigationBar(containerColor = ColorSurface, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = currentTab == AppTab.CHALLENGES,
            onClick = onChallengesClick,
            icon = { Icon(Icons.Default.Code, contentDescription = null) },
            label = { Text("Retos", fontSize = 11.sp) },
            colors = appNavItemColors(),
        )
        NavigationBarItem(
            selected = currentTab == AppTab.RANKING,
            onClick = onRankingClick,
            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
            label = { Text("Ranking", fontSize = 11.sp) },
            colors = appNavItemColors(),
        )
        if (isTeacher) {
            NavigationBarItem(
                selected = currentTab == AppTab.MY_EXERCISES,
                onClick = onMyExercisesClick,
                icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null) },
                label = { Text("Mis Retos", fontSize = 11.sp) },
                colors = appNavItemColors(),
            )
        }
        if (isAuthenticated) {
            NavigationBarItem(
                selected = currentTab == AppTab.PROFILE,
                onClick = onProfileClick,
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                label = { Text("Perfil", fontSize = 11.sp) },
                colors = appNavItemColors(),
            )
        }
    }
}

@Composable
fun appNavItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor   = ColorPrimary,
    selectedTextColor   = ColorPrimary,
    indicatorColor      = ColorPrimary.copy(alpha = 0.18f),
    unselectedIconColor = ColorMuted,
    unselectedTextColor = ColorMuted,
)
