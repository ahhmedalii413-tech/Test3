package com.example.pakforces.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun XpShopScreen(onBack: () -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val gameRepo = appViewModel.gamification
    val stats by appViewModel.gameStats.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf<String?>(null) }

    val items = listOf(
        XpShopItem("streak_freeze", "Streak Freeze", "Miss a day, streak bach jayegi", 500, "🧊"),
        XpShopItem("heart_refill", "Hearts Refill", "Saare hearts instantly restore", 300, "❤️"),
        XpShopItem("heart_add", "+1 Heart Slot", "Max hearts 5 → 6", 2000, "💖"),
        XpShopItem("custom_theme", "Premium Theme", "Exclusive gold theme unlock", 1500, "🎨"),
        XpShopItem("skip_power", "Skip Power-up", "Skip 1 hard question in any test", 250, "⏭️"),
        XpShopItem("hint_power", "Hint Power-up", "50/50 hint in any question", 200, "💡"),
    )

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("XP Shop", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Apne XP ko power-ups mein tabdeel karein", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
                Text("⚡ ${stats?.totalXP ?: 0}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(20.dp))

            if (message != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                ) {
                    Text(message!!, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items) { item ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(item.icon, style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                Text(item.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                            Button(
                                onClick = {
                                    val currentXp = stats?.totalXP ?: 0
                                    if (currentXp >= item.cost) {
                                        scope.launch {
                                            // Deduct XP by awarding negative
                                            gameRepo.awardXP("Bought ${item.title}", -item.cost, 0, 0)
                                            if (item.id == "streak_freeze") gameRepo.addStreakFreeze()
                                            if (item.id == "heart_refill") gameRepo.refillHearts()
                                            message = "✓ ${item.title} khareed liya! -${item.cost} XP"
                                        }
                                    } else {
                                        message = "❌ XP kafi nahi. ${item.cost - currentXp} XP aur chahiye."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary,
                                ),
                            ) {
                                Text("${item.cost}")
                                Spacer(Modifier.width(2.dp))
                                Text(" ⚡")
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class XpShopItem(val id: String, val title: String, val desc: String, val cost: Int, val icon: String)

@Composable
fun CustomTestBuilderScreen(
    forceKey: String,
    onStart: (force: String, category: String, mode: String) -> Unit,
    onBack: () -> Unit,
) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val state by appViewModel.state.collectAsState()
    var selectedCategory by remember { mutableStateOf<com.example.pakforces.data.model.Category?>(null) }
    var selectedMode by remember { mutableStateOf("TIMED") }
    var selectedLength by remember { mutableStateOf(com.example.pakforces.data.repo.TestLength.MEDIUM) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Custom Test Builder", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Apni marzi ka test banayein", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(20.dp))

            Text("1. Category chunein", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(10.dp))
            // Category chips
            androidx.compose.foundation.lazy.LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(280.dp),
            ) {
                items(com.example.pakforces.data.model.Category.entries.toList()) { cat ->
                    val selected = selectedCategory == cat
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { selectedCategory = cat },
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selected, onCheckedChange = { selectedCategory = cat })
                            Spacer(Modifier.width(8.dp))
                            Text(cat.display, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("2. Mode chunein", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("TIMED" to "Timed", "PRACTICE" to "Practice").forEach { (key, label) ->
                    OutlinedButton(
                        onClick = { selectedMode = key },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = if (selectedMode == key) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                 else ButtonDefaults.outlinedButtonColors(),
                    ) { Text(label) }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("3. Length chunein", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                com.example.pakforces.data.repo.TestLength.entries.forEach { tl ->
                    OutlinedButton(
                        onClick = { selectedLength = tl },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = if (selectedLength == tl) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                 else ButtonDefaults.outlinedButtonColors(),
                    ) { Text(tl.display, style = MaterialTheme.typography.labelSmall) }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    val cat = selectedCategory ?: return@Button
                    onStart(forceKey, cat.key, selectedMode)
                },
                enabled = selectedCategory != null,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
            ) {
                Text("Test shuru karein", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun TestPlannerScreen(onBack: () -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val features = appViewModel.features
    val state by appViewModel.state.collectAsState()
    val examDate by features.examDate().collectAsState(initial = null)
    var showDatePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Predictive Test Planner", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Day-by-day study plan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(20.dp))

            val e = examDate
            if (e == null || e.examDate == 0L) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📅", style = MaterialTheme.typography.displaySmall)
                        Spacer(Modifier.height(8.dp))
                        Text("Pehle apni test date set karein", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                            Text("Date set karein")
                        }
                    }
                }
            } else {
                val daysLeft = ((e.examDate - System.currentTimeMillis()) / 86_400_000L).toInt()
                if (daysLeft <= 0) {
                    Text("Test din aa gaya — best of luck! 🎯", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                } else {
                    val plan = generatePlan(daysLeft)
                    Text("Aapke $daysLeft din ka plan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(12.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(plan) { phase ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(phase.icon, style = MaterialTheme.typography.headlineSmall)
                                        Spacer(Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(phase.title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                            Text(phase.range, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(phase.detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val initCal = java.util.Calendar.getInstance()
        val dpd = android.app.DatePickerDialog(
            androidx.compose.ui.platform.LocalContext.current,
            { _, y, m, d ->
                val cal = java.util.Calendar.getInstance()
                cal.set(y, m, d, 9, 0, 0)
                scope.launch { features.setExamDate(state.force, cal.timeInMillis) }
                showDatePicker = false
            },
            initCal.get(java.util.Calendar.YEAR),
            initCal.get(java.util.Calendar.MONTH),
            initCal.get(java.util.Calendar.DAY_OF_MONTH),
        )
        dpd.datePicker.minDate = System.currentTimeMillis()
        LaunchedEffect(Unit) { dpd.show() }
    }
}

private data class PlanPhase(val range: String, val title: String, val detail: String, val icon: String)

private fun generatePlan(daysLeft: Int): List<PlanPhase> {
    val foundationDays = (daysLeft * 0.35).toInt().coerceAtLeast(1)
    val practiceDays = (daysLeft * 0.30).toInt().coerceAtLeast(1)
    val mockDays = (daysLeft * 0.20).toInt().coerceAtLeast(1)
    val revisionDays = (daysLeft - foundationDays - practiceDays - mockDays).coerceAtLeast(1)
    return listOf(
        PlanPhase("Day 1-$foundationDays", "Foundation", "Roz 1 chapter parhein. Verbal + Non-Verbal ke tricks seekhein. Learning Section se sab lessons parhein. Daily 30 questions.", "📚"),
        PlanPhase("Day ${foundationDays+1}-${foundationDays+practiceDays}", "Practice Phase", "Roz 50 questions practice mode mein. Saari categories cover karein. Wrong answers bookmark karein. Flashcards 10/day.", "💪"),
        PlanPhase("Day ${foundationDays+practiceDays+1}-${foundationDays+practiceDays+mockDays}", "Mock Tests Phase", "Roz 1 mock exam (timed mode). Simulation mode se real exam pressure feel karein. Score 70%+ target karein.", "🎯"),
        PlanPhase("Last $revisionDays days", "Final Revision", "Sirf SRS revision + bookmarks. Naya kuch na seekhein. Daily 1 light test. Rest well — test din par fresh raho!", "✨"),
    )
}
