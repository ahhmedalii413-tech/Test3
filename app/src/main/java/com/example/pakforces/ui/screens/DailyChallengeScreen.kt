package com.example.pakforces.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pakforces.data.db.QuestionEntity
import com.example.pakforces.data.model.Force
import com.example.pakforces.ui.components.LoadingScreen
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyChallengeScreen(onBack: () -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val repo = appViewModel.repository
    val state by appViewModel.state.collectAsState()
    val force = state.force
    val scope = rememberCoroutineScope()

    val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    var alreadyDone by remember { mutableStateOf(false) }
    var questions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    val answers = remember { mutableStateListOf<Int?>() }
    var currentIndex by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    val streak by repo.streakCount().collectAsState(initial = 0)
    val history by repo.dailyHistory().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        val existing = repo.getDaily(todayKey)
        if (existing != null) {
            alreadyDone = true
            correctCount = existing.correct
            finished = true
        }
        questions = repo.randomQuestions(force, null, 10)
        repeat(10) { answers.add(null) }
        loaded = true
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Daily Challenge", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("10 random questions · build your streak", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.LocalFireDepartment, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("$streak", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(20.dp))

            if (!loaded) {
                LoadingScreen("Generating today's challenge…")
                return@Column
            }

            if (finished) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (correctCount >= 7) "🏆" else "🎯", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.height(16.dp))
                        Text(if (alreadyDone) "Already completed today!" else "Challenge Complete!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(8.dp))
                        Text("Score: $correctCount / 10", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(24.dp))
                        Text("Recent challenges", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            items(history.take(14)) { h ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface).padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(h.dayKey, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                    Text("${h.correct}/${h.total}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                return@Column
            }

            val q = questions.getOrNull(currentIndex) ?: return@Column

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Question ${currentIndex + 1} of 10", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Text(q.question, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    val opts = listOf(q.option0, q.option1, q.option2, q.option3)
                    opts.forEachIndexed { idx, txt ->
                        val selected = answers[currentIndex] == idx
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .clickable { answers[currentIndex] = idx }
                                .padding(14.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(30.dp).clip(CircleShape).background(if (selected) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(('A' + idx).toString(), color = if (selected) Color.White else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(10.dp))
                                Text(txt, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (currentIndex < 9) {
                        currentIndex++
                    } else {
                        correctCount = questions.indices.count { i -> answers.getOrNull(i) == questions[i].correctIndex }
                        finished = true
                        alreadyDone = false
                        scope.launch { repo.saveDaily(todayKey, force, correctCount, 10) }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Text(if (currentIndex < 9) "Next Question" else "Finish Challenge", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
