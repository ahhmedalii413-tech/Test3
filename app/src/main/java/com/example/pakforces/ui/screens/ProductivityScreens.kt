package com.example.pakforces.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pakforces.data.content.DailyQuotes
import com.example.pakforces.ui.charts.ProgressRing
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PomodoroScreen(onBack: () -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val features = appViewModel.features
    val scope = rememberCoroutineScope()

    var durationMin by remember { mutableIntStateOf(25) }
    var remaining by remember { mutableIntStateOf(25 * 60) }
    var running by remember { mutableStateOf(false) }
    var sessionCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(running) {
        while (running && remaining > 0) {
            delay(1000)
            remaining--
        }
        if (remaining == 0 && running) {
            running = false
            sessionCount++
            scope.launch {
                features.logPomodoro(durationMin)
                appViewModel.gamification.addFocusMinutes(durationMin)
                appViewModel.gamification.unlockAchievement(com.example.pakforces.data.repo.Achievement.POMODORO_10.id)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pomodoro Focus Mode", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("25-min focus + 5-min break", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(40.dp))

            // Timer ring
            Box(contentAlignment = Alignment.Center) {
                ProgressRing(
                    progress = 1f - (remaining.toFloat() / (durationMin * 60f)),
                    size = 280,
                    accentColor = MaterialTheme.colorScheme.secondary,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val m = remaining / 60
                    val s = remaining % 60
                    Text("%02d:%02d".format(m, s), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("${sessionCount} sessions done", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }

            Spacer(Modifier.height(40.dp))

            // Duration options
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 25, 45).forEach { d ->
                    OutlinedButton(
                        onClick = { if (!running) { durationMin = d; remaining = d * 60 } },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = if (durationMin == d) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                 else ButtonDefaults.outlinedButtonColors(),
                    ) {
                        Text("${d}m")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { running = !running },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                ) {
                    Icon(if (running) Icons.Filled.Pause else Icons.Filled.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (running) "Pause" else "Start", fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = { running = false; remaining = durationMin * 60 },
                    modifier = Modifier.weight(1f).height(56.dp),
                ) {
                    Icon(Icons.Filled.Refresh, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Reset")
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("💡 Tip: Phone ko doosre kamre mein rakhein. Focus mode mein notifications band kar dein.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun DailyMissionsScreen(onBack: () -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val features = appViewModel.features
    val scope = rememberCoroutineScope()
    var mission by remember { mutableStateOf<com.example.pakforces.data.db.MissionEntity?>(null) }

    LaunchedEffect(Unit) {
        mission = features.todayMission()
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Today's Missions", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("4 micro goals · bonus XP", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
                Text("🔥", style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(Modifier.height(20.dp))

            val m = mission
            if (m == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                return@Column
            }

            // Parse goals
            val arr = org.json.JSONArray(m.goalsJson)
            val totalDone = (0 until arr.length()).count { arr.getJSONObject(it).getBoolean("done") }
            val totalXp = (0 until arr.length()).filter { arr.getJSONObject(it).getBoolean("done") }.sumOf { arr.getJSONObject(it).getInt("xp") }

            // Header card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Daily Mission Progress", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("$totalDone/${arr.length()} goals complete", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("+$totalXp XP earned today", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                }
            }
            Spacer(Modifier.height(20.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(0 until arr.length()) { i ->
                    val goal = arr.getJSONObject(i)
                    val done = goal.getBoolean("done")
                    val progress = goal.getInt("progress")
                    val target = goal.getInt("target")
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (done) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                             else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (done) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (done) Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                else Text("○", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(goal.getString("title"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                                Text("$progress / $target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f))
                            }
                            Text("+${goal.getInt("xp")}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExamCountdownScreen(onBack: () -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val features = appViewModel.features
    val state by appViewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val examDate by features.examDate().collectAsState(initial = null)
    var showDatePicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Exam Countdown", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Apne test ki date set karein", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(40.dp))

            val e = examDate
            if (e == null || e.examDate == 0L) {
                // No date set
                Text("📅", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(12.dp))
                Text("Apne test ki date set karein", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(20.dp))
                Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("Date set karein")
                }
            } else {
                val now = System.currentTimeMillis()
                val daysLeft = ((e.examDate - now) / (86_400_000L)).toInt()
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$daysLeft", style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp), fontWeight = FontWeight.Bold, color = Color.White)
                            Text(if (daysLeft == 1L) "day left" else "days left", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.85f))
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                val dateStr = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(e.examDate))
                Text("Test date: $dateStr", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                Text("Force: ${com.example.pakforces.data.model.Force.fromKey(e.force).display}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                Spacer(Modifier.height(20.dp))
                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Text("Date change karein")
                }
                Spacer(Modifier.height(8.dp))
                if (daysLeft in 1..90) {
                    Text("💡 Aapke paas $daysLeft din hain. Roz 1-2 ghante practice karein.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f), textAlign = TextAlign.Center)
                } else if (daysLeft <= 0) {
                    Text("🎯 Test din aa gaya! Best of luck!", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                } else {
                    Text("💡 Abhi se start karein — daily practice se hi kamyabi milti hai.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f), textAlign = TextAlign.Center)
                }
            }

            Spacer(Modifier.height(40.dp))
            // Daily quote
            val quote = remember { DailyQuotes.all.random() }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Daily Quote", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(quote, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
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

@Composable
fun SmartRevisionScreen(onBack: () -> Unit, onStart: (force: String, category: String) -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val features = appViewModel.features
    val state by appViewModel.state.collectAsState()
    val srsDueCount by features.srsDueCount(System.currentTimeMillis()).collectAsState(initial = 0)
    val srsTotal by features.srsTotal().collectAsState(initial = 0)
    var dueQuestions by remember { mutableStateOf<List<com.example.pakforces.data.db.QuestionEntity>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        dueQuestions = features.srsDueQuestions(20)
        loaded = true
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Smart Revision (SRS)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Spaced repetition · wrong answers ki revision", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("DUE NOW", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("$srsDueCount questions", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Total in SRS: $srsTotal", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                }
            }
            Spacer(Modifier.height(20.dp))

            Text("How it works", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))
            Text("• Galat jawab automatic SRS queue mein add ho jate hain\n• Har sawal 1 din baad dobara aata hai\n• Sahi jawab par interval barhta hai (3 → 7 → 14 → 28 din)\n• Galat jawab par interval reset ho jata hai\n• Yeh technique retention 200%+ barhati hai",
                 style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))

            Spacer(Modifier.height(30.dp))

            if (srsDueCount == 0) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✓", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.height(8.dp))
                        Text("Aaj ke liye koi revision nahi!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.height(4.dp))
                        Text("Tests solve karein aur wrong answers yahan aayenge.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), textAlign = TextAlign.Center)
                    }
                }
            } else {
                Button(
                    onClick = {
                        // Start a practice test using a special "srs" category marker
                        onStart(state.force.key, "verbal")  // placeholder — actual SRS flow would need its own route
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                ) {
                    Text("Start Revision ($srsDueCount Q)", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
