package com.codeclass.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.codeclass.app.ui.screens.auth.LoginScreen
import com.codeclass.app.ui.screens.auth.RegisterScreen
import com.codeclass.app.ui.screens.auth.WelcomeScreen
import com.codeclass.app.ui.screens.challenges.ChallengeScreen
import com.codeclass.app.ui.screens.challenges.ChallengesScreen
import com.codeclass.app.ui.screens.profile.ProfileScreen
import com.codeclass.app.ui.screens.ranking.RankingScreen


object Routes {
    const val WELCOME    = "welcome"
    const val LOGIN      = "login"
    const val REGISTER   = "register"
    const val CHALLENGES = "challenges"
    const val CHALLENGE  = "challenges/{exerciseId}"
    const val RANKING    = "ranking"
    const val PROFILE    = "profile"

    fun challenge(id: Int) = "challenges/$id"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isAuthenticated: Boolean,
) {
    // Si el usuario ya tiene token (isAuthenticated = true), va directo a Challenges. Si no, empieza en Welcome.
    val startDestination = if (isAuthenticated) Routes.CHALLENGES else Routes.WELCOME

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onExploreClick = { navController.navigate(Routes.CHALLENGES) { popUpTo(0) } },
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.CHALLENGES) { popUpTo(0) } },
                onGoToRegister = { navController.navigate(Routes.REGISTER) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Routes.CHALLENGES) { popUpTo(0) } },
                onGoToLogin = { navController.popBackStack() },
            )
        }

        composable(Routes.CHALLENGES) {
            ChallengesScreen(
                onChallengeClick = { id -> navController.navigate(Routes.challenge(id)) },
                onRankingClick = { navController.navigate(Routes.RANKING) },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
            )
        }

        composable(Routes.CHALLENGE) { backStack ->
            val exerciseId = backStack.arguments?.getString("exerciseId")!!.toInt()
            ChallengeScreen(
                exerciseId = exerciseId,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.RANKING) {
            RankingScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogout = { navController.navigate(Routes.LOGIN) { popUpTo(0) } },
                onBack = { navController.popBackStack() },
            )
        }
    }
}
