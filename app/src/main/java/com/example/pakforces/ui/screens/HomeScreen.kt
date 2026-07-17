package com.example.pakforces.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pakforces.data.model.Force
import com.example.pakforces.data.model.Ranks
import com.example.pakforces.ui.components.FeatureTile
import com.example.pakforces.ui.components.GamificationBar
import com.example.pakforces.ui.components.PakForcesCrest
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory

@Composable
fun HomeScreen(
    onPickForce: () -> Unit,
    onDailyChallenge: () -> Unit,
    onProgress: () -> Unit,
    onBookmarks: () -> Unit,
    onSettings: () -> Unit,
    onRevision: () -> Unit,
    onSimulation: () -> Unit,
    onPractice: () -> Unit,
    onLearning: () -> Unit,
    onAchievements: () -> Unit,
    onContinueForce: (Force) -> Unit,
    // v3
    onAnalytics: () -> Unit = {},
    onMockExams: () -> Unit = {},
    onFlashcards: () -> Unit = {},
    onMathFormulas: () -> Unit = {},
    onIslamicRef: () -> Unit = {},
    onPakistanTimeline: () -> Unit = {},
    onMedical: () -> Unit = {},
    onDocuments: () -> Unit = {},
    onDayInLife: () -> Unit = {},
    onPomodoro: () -> Unit = {},
    onMissions: () -> Unit = {},
    onExamCountdown: () -> Unit = {},
    onSmartRevision: () -> Unit = {},
    onXpShop: () -> Unit = {},
    onCustomBuilder: () -> Unit = {},
    onTestPlanner: () -> Unit = {},
) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val state by appViewModel.state.collectAsState()
    val gameStats by appViewModel.gameStats.collectAsState(initial = null)

    val forceName = when (state.force) {
        Force.ARMY -> "Army"
        Force.AIR_FORCE -> "Air Force"
        Force.NAVY -> "Navy"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background,
                    )
                )
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(40.dp))

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                PakForcesCrest(size = 60, label = "", subtitle = "")
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pak Forces Prep", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Elite Initial Test Preparation", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }

            Spacer(Modifier.height(8.dp))

            // Gamification bar
            GamificationBar(
                xp = gameStats?.totalXP ?: 0,
                hearts = gameStats?.currentHearts ?: 5,
                maxHearts = gameStats?.maxHearts ?: 5,
                streak = gameStats?.streakDays ?: 0,
                streakFreezes = gameStats?.streakFreezes ?: 0,
                rank = state.rank,
                rankProgress = Ranks.progress(gameStats?.totalXP ?: 0, state.force),
            )

            Spacer(Modifier.height(12.dp))

            // Hero card — current force
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            )
                        ),
                        shape = MaterialTheme.shapes.large,
                    )
                    .padding(20.dp),
            ) {
                Column {
                    Text("CURRENTLY PREPARING FOR", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("Pakistan $forceName", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("${state.totalQuestions}+ verified questions available", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                }
            }

            Spacer(Modifier.height(20.dp))

            // MAIN MODES
            SectionHeader("Test Modes")
            FeatureTile(
                title = "Mock Exams (Full-Length)",
                subtitle = "Real exam ka exact replica — all sections, real time",
                icon = Icons.Filled.School,
                accentColor = MaterialTheme.colorScheme.tertiary,
                trailingLabel = "REAL",
                onClick = onMockExams,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Simulation Mode",
                subtitle = "Section-wise real exam pattern",
                icon = Icons.Filled.Flag,
                accentColor = MaterialTheme.colorScheme.tertiary,
                onClick = onSimulation,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Practice Mode",
                subtitle = "Category-wise practice · all questions · explanations",
                icon = Icons.Filled.FitnessCenter,
                accentColor = MaterialTheme.colorScheme.secondary,
                onClick = onPractice,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Custom Test Builder",
                subtitle = "Apni marzi ka test banayein — category + mode + length",
                icon = Icons.Filled.Build,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onCustomBuilder,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Smart Revision (SRS)",
                subtitle = "Spaced repetition · wrong answers ki revision",
                icon = Icons.Filled.AutoAwesome,
                accentColor = MaterialTheme.colorScheme.secondary,
                onClick = onSmartRevision,
            )

            Spacer(Modifier.height(20.dp))

            // ANALYTICS & PROGRESS
            SectionHeader("Analytics & Progress")
            FeatureTile(
                title = "Analytics & Weakness Diagnosis",
                subtitle = "Radar chart, time-per-Q, heatmap, score history",
                icon = Icons.Filled.Insights,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onAnalytics,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Progress & Stats",
                subtitle = "Attempts, avg score, best score",
                icon = Icons.Filled.TrendingUp,
                onClick = onProgress,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Exam Countdown + Test Planner",
                subtitle = "Apni test date set karein · day-by-day study plan",
                icon = Icons.Filled.Event,
                accentColor = MaterialTheme.colorScheme.secondary,
                onClick = onExamCountdown,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Predictive Test Planner",
                subtitle = "Apne test ki date ke hisaab se plan",
                icon = Icons.Filled.CalendarMonth,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onTestPlanner,
            )

            Spacer(Modifier.height(20.dp))

            // LEARNING
            SectionHeader("Learning & Reference")
            FeatureTile(
                title = "Learning Section",
                subtitle = "Tricks, tips aur examples har category ke liye",
                icon = Icons.Filled.Lightbulb,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onLearning,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Vocabulary Flashcards",
                subtitle = "Spaced repetition · synonyms, antonyms, one-words",
                icon = Icons.Filled.Style,
                accentColor = MaterialTheme.colorScheme.secondary,
                onClick = onFlashcards,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Math Formulas Sheet",
                subtitle = "Arithmetic, algebra, geometry quick reference",
                icon = Icons.Filled.Calculate,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onMathFormulas,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Islamic Studies Reference",
                subtitle = "Pillars, Quran, Prophets, Ghazwaat quick facts",
                icon = Icons.Filled.MenuBook,
                accentColor = MaterialTheme.colorScheme.secondary,
                onClick = onIslamicRef,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Pakistan Studies Timeline",
                subtitle = "1857 → present · important events",
                icon = Icons.Filled.History,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onPakistanTimeline,
            )

            Spacer(Modifier.height(20.dp))

            // GAMIFICATION
            SectionHeader("Daily Engagement")
            FeatureTile(
                title = "Daily Challenge",
                subtitle = "10 random questions · build your streak",
                icon = Icons.Filled.LocalFireDepartment,
                accentColor = MaterialTheme.colorScheme.tertiary,
                onClick = onDailyChallenge,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Today's Missions",
                subtitle = "4 micro goals · bonus XP",
                icon = Icons.Filled.Assignment,
                accentColor = MaterialTheme.colorScheme.secondary,
                onClick = onMissions,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Pomodoro Focus Mode",
                subtitle = "25-min focus sessions · bina distraction padho",
                icon = Icons.Filled.Timer,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onPomodoro,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "XP Shop",
                subtitle = "Apne XP ko power-ups mein tabdeel karein",
                icon = Icons.Filled.ShoppingCart,
                accentColor = MaterialTheme.colorScheme.secondary,
                onClick = onXpShop,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Achievements",
                subtitle = "Apne badges aur trophies dekhein",
                icon = Icons.Filled.EmojiEvents,
                accentColor = MaterialTheme.colorScheme.tertiary,
                onClick = onAchievements,
            )

            Spacer(Modifier.height(20.dp))

            // EXAM ESSENTIALS
            SectionHeader("Exam Essentials")
            FeatureTile(
                title = "Medical & Physical Standards",
                subtitle = "Height, weight, vision, running times per force",
                icon = Icons.Filled.MonitorHeart,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onMedical,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Document Checklist",
                subtitle = "AS&RC / PAF / Navy ke liye documents",
                icon = Icons.Filled.Checklist,
                accentColor = MaterialTheme.colorScheme.secondary,
                onClick = onDocuments,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Day in the Life",
                subtitle = "PMA, PAF Academy, PNS Bahadur — cadet life",
                icon = Icons.Filled.Sports,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onDayInLife,
            )

            Spacer(Modifier.height(20.dp))

            // MORE
            SectionHeader("More")
            FeatureTile(
                title = "Switch Force",
                subtitle = "Army · Air Force · Navy",
                icon = Icons.Filled.Quiz,
                onClick = onPickForce,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Bookmarks",
                subtitle = "Apne saved questions dekhein",
                icon = Icons.Filled.Bookmark,
                accentColor = MaterialTheme.colorScheme.secondary,
                onClick = onBookmarks,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Revision · Wrong Answers",
                subtitle = "Re-attempt questions you missed",
                icon = Icons.Filled.Warning,
                accentColor = MaterialTheme.colorScheme.tertiary,
                onClick = onRevision,
            )
            Spacer(Modifier.height(10.dp))
            FeatureTile(
                title = "Settings",
                subtitle = "Theme, language, sound, reminders",
                icon = Icons.Filled.Settings,
                onClick = onSettings,
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 8.dp))
}
