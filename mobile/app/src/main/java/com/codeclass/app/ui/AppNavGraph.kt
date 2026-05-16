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
import com.codeclass.app.ui.screens.exercises.CreateExerciseScreen
import com.codeclass.app.ui.screens.exercises.MyExercisesScreen
import com.codeclass.app.ui.screens.profile.ProfileScreen
import com.codeclass.app.ui.screens.ranking.RankingScreen
import com.codeclass.app.ui.screens.submissions.SubmissionsScreen


object Routes {
    const val WELCOME         = "welcome"
    const val LOGIN           = "login"
    const val REGISTER        = "register"
    const val CHALLENGES      = "challenges"
    const val CHALLENGE       = "challenges/{exerciseId}"
    const val RANKING         = "ranking"
    const val PROFILE         = "profile"
    const val SUBMISSIONS     = "submissions/{exerciseId}"
    const val MY_EXERCISES    = "my-exercises"
    const val CREATE_EXERCISE = "my-exercises/create"
    const val EDIT_EXERCISE   = "my-exercises/edit/{exerciseId}"

    fun challenge(id: Int)    = "challenges/$id"
    fun submissions(id: Int)  = "submissions/$id"
    fun editExercise(id: Int) = "my-exercises/edit/$id"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isAuthenticated: Boolean,
    isTeacher: Boolean = false,
) {
    val startDestination = if (isAuthenticated) Routes.CHALLENGES else Routes.WELCOME

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onLoginClick    = { navController.navigate(Routes.LOGIN) },
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onExploreClick  = { navController.navigate(Routes.CHALLENGES) { popUpTo(0) } },
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.CHALLENGES) { popUpTo(0) } },
                onGoToRegister = { navController.navigate(Routes.REGISTER) },
                onBack         = { navController.popBackStack() },
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Routes.CHALLENGES) { popUpTo(0) } },
                onGoToLogin       = { navController.navigate(Routes.LOGIN) },
                onBack            = { navController.popBackStack() },
            )
        }

        composable(Routes.CHALLENGES) {
            ChallengesScreen(
                onChallengeClick   = { id -> navController.navigate(Routes.challenge(id)) },
                onSubmissionsClick = { id -> navController.navigate(Routes.submissions(id)) },
                onRankingClick     = { navController.navigate(Routes.RANKING) },
                onProfileClick     = { navController.navigate(Routes.PROFILE) },
                onMyExercisesClick = { navController.navigate(Routes.MY_EXERCISES) },
                onGoToLogin        = { navController.navigate(Routes.LOGIN) },
                onGoToRegister     = { navController.navigate(Routes.REGISTER) },
                isTeacher          = isTeacher,
            )
        }

        composable(Routes.SUBMISSIONS) { backStack ->
            val exerciseId = backStack.arguments?.getString("exerciseId")?.toIntOrNull() ?: return@composable
            SubmissionsScreen(
                exerciseId = exerciseId,
                onBack     = { navController.popBackStack() },
            )
        }

        composable(Routes.CHALLENGE) { backStack ->
            val exerciseId = backStack.arguments?.getString("exerciseId")?.toIntOrNull() ?: return@composable
            ChallengeScreen(
                exerciseId = exerciseId,
                onBack     = { navController.popBackStack() },
            )
        }

        composable(Routes.RANKING) {
            RankingScreen(
                onChallengesClick  = { navController.navigate(Routes.CHALLENGES) { launchSingleTop = true } },
                onProfileClick     = { navController.navigate(Routes.PROFILE) { launchSingleTop = true } },
                onMyExercisesClick = { navController.navigate(Routes.MY_EXERCISES) { launchSingleTop = true } },
                onGoToLogin        = { navController.navigate(Routes.LOGIN) },
                isTeacher          = isTeacher,
                isAuthenticated    = isAuthenticated,
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogout           = { navController.navigate(Routes.LOGIN) { popUpTo(0) } },
                onChallengesClick  = { navController.navigate(Routes.CHALLENGES) { launchSingleTop = true } },
                onRankingClick     = { navController.navigate(Routes.RANKING) { launchSingleTop = true } },
                onMyExercisesClick = { navController.navigate(Routes.MY_EXERCISES) { launchSingleTop = true } },
                isTeacher          = isTeacher,
            )
        }

        composable(Routes.MY_EXERCISES) {
            if (!isTeacher) {
                navController.navigate(Routes.CHALLENGES) { popUpTo(0) }
                return@composable
            }
            MyExercisesScreen(
                onChallengesClick  = { navController.navigate(Routes.CHALLENGES) { launchSingleTop = true } },
                onRankingClick     = { navController.navigate(Routes.RANKING) { launchSingleTop = true } },
                onProfileClick     = { navController.navigate(Routes.PROFILE) { launchSingleTop = true } },
                onCreateClick      = { navController.navigate(Routes.CREATE_EXERCISE) },
                onEditClick        = { id -> navController.navigate(Routes.editExercise(id)) },
                onChallengeClick   = { id -> navController.navigate(Routes.challenge(id)) },
                isTeacher          = isTeacher,
            )
        }

        composable(Routes.CREATE_EXERCISE) {
            CreateExerciseScreen(
                exerciseId = null,
                onBack     = { navController.popBackStack() },
                onSuccess  = {
                    navController.navigate(Routes.MY_EXERCISES) {
                        popUpTo(Routes.MY_EXERCISES) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.EDIT_EXERCISE) { backStack ->
            val exerciseId = backStack.arguments?.getString("exerciseId")?.toIntOrNull() ?: return@composable
            CreateExerciseScreen(
                exerciseId = exerciseId,
                onBack     = { navController.popBackStack() },
                onSuccess  = { navController.popBackStack() },
            )
        }
    }
}
