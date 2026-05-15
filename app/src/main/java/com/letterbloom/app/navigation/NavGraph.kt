package com.letterbloom.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.letterbloom.app.data.LearningPrefs
import com.letterbloom.app.ui.screens.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object LevelDiagnosis : Screen("level_diagnosis")
    object Home : Screen("home")
    object Category : Screen("category")
    object Flashcard : Screen("flashcard/{category}") {
        fun createRoute(category: String) = "flashcard/$category"
    }
    object Quiz : Screen("quiz/{category}") {
        fun createRoute(category: String) = "quiz/$category"
    }
    object Progress : Screen("progress")
    object Pronunciation : Screen("pronunciation")
}

@Composable
fun LetterBloomNavGraph(navController: NavHostController) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigate = {
                // 온보딩 완료 여부에 따라 바로 홈으로
                if (LearningPrefs.isOnboardingDone(context)) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.LevelDiagnosis.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        composable(Screen.LevelDiagnosis.route) {
            LevelDiagnosisScreen(onComplete = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.LevelDiagnosis.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onCategoryClick = { navController.navigate(Screen.Category.route) },
                onProgressClick = { navController.navigate(Screen.Progress.route) }
            )
        }
        composable(Screen.Category.route) {
            CategoryScreen(
                onCategorySelected = { category ->
                    navController.navigate(Screen.Flashcard.createRoute(category))
                },
                onPronunciation = { navController.navigate(Screen.Pronunciation.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Pronunciation.route) {
            PronunciationScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Flashcard.route) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "AIRPORT"
            FlashcardScreen(
                category = category,
                onComplete = {
                    navController.navigate(Screen.Quiz.createRoute(category)) {
                        popUpTo(Screen.Flashcard.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Quiz.route) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "AIRPORT"
            QuizScreen(
                category = category,
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Progress.route) {
            ProgressScreen(onBack = { navController.popBackStack() })
        }
    }
}
