package com.example.pakforces.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pakforces.ui.screens.AchievementsScreen
import com.example.pakforces.ui.screens.AnalyticsScreen
import com.example.pakforces.ui.screens.BookmarksScreen
import com.example.pakforces.ui.screens.CustomTestBuilderScreen
import com.example.pakforces.ui.screens.DailyChallengeScreen
import com.example.pakforces.ui.screens.DailyMissionsScreen
import com.example.pakforces.ui.screens.DayInLifeScreen
import com.example.pakforces.ui.screens.DocumentsChecklistScreen
import com.example.pakforces.ui.screens.ExamCountdownScreen
import com.example.pakforces.ui.screens.FlashcardsScreen
import com.example.pakforces.ui.screens.ForcePickerScreen
import com.example.pakforces.ui.screens.HomeScreen
import com.example.pakforces.ui.screens.IslamicReferenceScreen
import com.example.pakforces.ui.screens.LearningScreen
import com.example.pakforces.ui.screens.MathFormulasScreen
import com.example.pakforces.ui.screens.MedicalStandardsScreen
import com.example.pakforces.ui.screens.MockExamsScreen
import com.example.pakforces.ui.screens.OnboardingScreen
import com.example.pakforces.ui.screens.PakistanTimelineScreen
import com.example.pakforces.ui.screens.PomodoroScreen
import com.example.pakforces.ui.screens.PracticeScreen
import com.example.pakforces.ui.screens.ProgressScreen
import com.example.pakforces.ui.screens.RevisionScreen
import com.example.pakforces.ui.screens.ResultScreen
import com.example.pakforces.ui.screens.SettingsScreen
import com.example.pakforces.ui.screens.SimulationScreen
import com.example.pakforces.ui.screens.SmartRevisionScreen
import com.example.pakforces.ui.screens.SplashScreen
import com.example.pakforces.ui.screens.SubjectPickerScreen
import com.example.pakforces.ui.screens.TestPlannerScreen
import com.example.pakforces.ui.screens.TestScreen
import com.example.pakforces.ui.screens.XpShopScreen
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory

@Composable
fun PakForcesNavGraph(
    navController: NavHostController,
    appViewModel: AppViewModel,
) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(onContinue = {
                navController.navigate(if (appViewModel.state.value.onboarded) Routes.HOME else Routes.ONBOARDING) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(onDone = {
                appViewModel.completeOnboarding()
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                }
            })
        }

        composable(Routes.HOME) {
            HomeScreen(
                onPickForce = { navController.navigate(Routes.FORCE_PICKER) },
                onDailyChallenge = { navController.navigate(Routes.DAILY_CHALLENGE) },
                onProgress = { navController.navigate(Routes.PROGRESS) },
                onBookmarks = { navController.navigate(Routes.BOOKMARKS) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onRevision = { navController.navigate(Routes.revision(appViewModel.state.value.force.key)) },
                onSimulation = { navController.navigate(Routes.simulation(appViewModel.state.value.force.key)) },
                onPractice = { navController.navigate(Routes.practice(appViewModel.state.value.force.key)) },
                onLearning = { navController.navigate(Routes.LEARNING) },
                onAchievements = { navController.navigate(Routes.ACHIEVEMENTS) },
                onContinueForce = { force ->
                    navController.navigate(Routes.subjectPicker(force))
                },
                // v3
                onAnalytics = { navController.navigate(Routes.ANALYTICS) },
                onMockExams = { navController.navigate(Routes.mockExams(appViewModel.state.value.force.key)) },
                onFlashcards = { navController.navigate(Routes.FLASHCARDS) },
                onMathFormulas = { navController.navigate(Routes.MATH_FORMULAS) },
                onIslamicRef = { navController.navigate(Routes.ISLAMIC_REF) },
                onPakistanTimeline = { navController.navigate(Routes.PAKISTAN_TIMELINE) },
                onMedical = { navController.navigate(Routes.MEDICAL) },
                onDocuments = { navController.navigate(Routes.DOCUMENTS) },
                onDayInLife = { navController.navigate(Routes.DAY_IN_LIFE) },
                onPomodoro = { navController.navigate(Routes.POMODORO) },
                onMissions = { navController.navigate(Routes.MISSIONS) },
                onExamCountdown = { navController.navigate(Routes.EXAM_COUNTDOWN) },
                onSmartRevision = { navController.navigate(Routes.SMART_REVISION) },
                onXpShop = { navController.navigate(Routes.XP_SHOP) },
                onCustomBuilder = { navController.navigate(Routes.customBuilder(appViewModel.state.value.force.key)) },
                onTestPlanner = { navController.navigate(Routes.TEST_PLANNER) },
            )
        }

        composable(Routes.FORCE_PICKER) {
            ForcePickerScreen(onForceSelected = { force ->
                appViewModel.setForce(force)
                navController.navigate(Routes.subjectPicker(force))
            })
        }

        composable(
            Routes.SUBJECT_PICKER,
            arguments = listOf(navArgument("force") { type = NavType.StringType })
        ) { entry ->
            val force = entry.arguments?.getString("force") ?: "army"
            SubjectPickerScreen(
                forceKey = force,
                onBack = { navController.popBackStack() },
                onPick = { category, mode ->
                    navController.navigate(Routes.test(force, category.key, mode.name))
                }
            )
        }

        composable(
            Routes.TEST,
            arguments = listOf(
                navArgument("force") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType },
                navArgument("mode") { type = NavType.StringType },
            )
        ) { entry ->
            val force = entry.arguments?.getString("force") ?: "army"
            val category = entry.arguments?.getString("category") ?: "verbal"
            val mode = entry.arguments?.getString("mode") ?: "TIMED"
            TestScreen(
                forceKey = force, categoryKey = category, modeKey = mode,
                onFinish = { correct, total, timeSec ->
                    navController.navigate(Routes.result(force, category, correct, total, timeSec)) {
                        popUpTo(Routes.TEST) { inclusive = true }
                    }
                },
                onExit = { navController.popBackStack() }
            )
        }

        composable(
            Routes.RESULT,
            arguments = listOf(
                navArgument("force") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType },
                navArgument("correct") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("timeSec") { type = NavType.IntType },
            )
        ) { entry ->
            val force = entry.arguments?.getString("force") ?: "army"
            val category = entry.arguments?.getString("category") ?: "verbal"
            val correct = entry.arguments?.getInt("correct") ?: 0
            val total = entry.arguments?.getInt("total") ?: 0
            val timeSec = entry.arguments?.getInt("timeSec") ?: 0
            ResultScreen(
                forceKey = force, categoryKey = category, correct = correct,
                total = total, timeSec = timeSec,
                onHome = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                onRetry = { navController.popBackStack() }
            )
        }

        composable(Routes.PROGRESS) {
            ProgressScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.BOOKMARKS) {
            BookmarksScreen(
                onBack = { navController.popBackStack() },
                onOpenQuestion = { /* could navigate to detail; show inline list */ }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.DAILY_CHALLENGE) {
            DailyChallengeScreen(onBack = { navController.popBackStack() })
        }

        composable(
            Routes.REVISION,
            arguments = listOf(navArgument("force") { type = NavType.StringType })
        ) { entry ->
            val force = entry.arguments?.getString("force") ?: "army"
            RevisionScreen(forceKey = force, onBack = { navController.popBackStack() })
        }

        composable(
            Routes.SIMULATION,
            arguments = listOf(navArgument("force") { type = NavType.StringType })
        ) { entry ->
            val force = entry.arguments?.getString("force") ?: "army"
            SimulationScreen(
                forceKey = force,
                onStart = { section ->
                    // Start a timed test with simulation params
                    navController.navigate(Routes.test(force, section.category.key, "TIMED"))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            Routes.PRACTICE,
            arguments = listOf(navArgument("force") { type = NavType.StringType })
        ) { entry ->
            val force = entry.arguments?.getString("force") ?: "army"
            PracticeScreen(
                forceKey = force,
                onPickCategory = { category ->
                    navController.navigate(Routes.practiceTest(force, category.key))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            Routes.PRACTICE_TEST,
            arguments = listOf(
                navArgument("force") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType },
            )
        ) { entry ->
            val force = entry.arguments?.getString("force") ?: "army"
            val category = entry.arguments?.getString("category") ?: "verbal"
            TestScreen(
                forceKey = force, categoryKey = category, modeKey = "PRACTICE",
                onFinish = { correct, total, timeSec ->
                    navController.navigate(Routes.result(force, category, correct, total, timeSec)) {
                        popUpTo(Routes.PRACTICE_TEST) { inclusive = true }
                    }
                },
                onExit = { navController.popBackStack() }
            )
        }

        composable(Routes.LEARNING) {
            LearningScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ACHIEVEMENTS) {
            AchievementsScreen(onBack = { navController.popBackStack() })
        }

        // ───── v3 routes ─────
        composable(Routes.ANALYTICS) {
            AnalyticsScreen(onBack = { navController.popBackStack() })
        }

        composable(
            Routes.MOCK_EXAMS,
            arguments = listOf(navArgument("force") { type = NavType.StringType })
        ) { entry ->
            val force = entry.arguments?.getString("force") ?: "army"
            MockExamsScreen(
                forceKey = force,
                onStart = { f, cat -> navController.navigate(Routes.test(f, cat, "TIMED")) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.FLASHCARDS) {
            FlashcardsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.MATH_FORMULAS) {
            MathFormulasScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ISLAMIC_REF) {
            IslamicReferenceScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PAKISTAN_TIMELINE) {
            PakistanTimelineScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.MEDICAL) {
            MedicalStandardsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.DOCUMENTS) {
            DocumentsChecklistScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.DAY_IN_LIFE) {
            DayInLifeScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.POMODORO) {
            PomodoroScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.MISSIONS) {
            DailyMissionsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.EXAM_COUNTDOWN) {
            ExamCountdownScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SMART_REVISION) {
            SmartRevisionScreen(
                onBack = { navController.popBackStack() },
                onStart = { force, cat -> navController.navigate(Routes.test(force, cat, "PRACTICE")) },
            )
        }

        composable(Routes.XP_SHOP) {
            XpShopScreen(onBack = { navController.popBackStack() })
        }

        composable(
            Routes.CUSTOM_BUILDER,
            arguments = listOf(navArgument("force") { type = NavType.StringType })
        ) { entry ->
            val force = entry.arguments?.getString("force") ?: "army"
            CustomTestBuilderScreen(
                forceKey = force,
                onStart = { f, cat, mode -> navController.navigate(Routes.test(f, cat, mode)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.TEST_PLANNER) {
            TestPlannerScreen(onBack = { navController.popBackStack() })
        }
    }
}
