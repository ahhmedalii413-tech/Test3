package com.example.pakforces.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pakforces.data.model.Force
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(onBack: () -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val repo = appViewModel.repository
    val state by appViewModel.state.collectAsState()
    val force = state.force

    val attempts by repo.attemptsForForce(force).collectAsState(initial = 0)
    val avgScore by repo.avgScoreForForce(force).collectAsState(initial = 0f)
    val bestScore by repo.bestScoreForForce(force).collectAsState(initial = 0)
    val wrongCount by repo.totalWrongCount().collectAsState(initial = 0)
    val results by repo.allResults().collectAsState(initial = emptyList())
    val daily by repo.dailyHistory().collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Progress", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Pakistan ${force.display.removePrefix("Pakistan ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(20.dp))

            // Big stat cards
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BigStat("Attempts", "$attempts", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                BigStat("Avg Score", "${avgScore?.toInt() ?: 0}%", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BigStat("Best Score", "${bestScore ?: 0}%", ScoreExcellent, Modifier.weight(1f))
                BigStat("Wrong Bank", "$wrongCount", ScorePoor, Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            Text("Recent tests", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(results.take(40)) { r ->
                    val cat = runCatching { com.example.pakforces.data.model.Category.fromKey(r.category) }.getOrDefault(com.example.pakforces.data.model.Category.VERBAL)
                    val date = SimpleDateFormat("dd MMM · HH:mm", Locale.getDefault()).format(Date(r.completedAt))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(scoreColor(r.scorePercent).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("${r.scorePercent}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = scoreColor(r.scorePercent))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cat.display, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                Text("$date · ${r.correct}/${r.totalQuestions} correct · ${r.mode}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BigStat(label: String, value: String, accent: Color, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier.height(108.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.linearGradient(listOf(accent.copy(alpha = 0.10f), Color.Transparent))
            ).padding(16.dp),
        ) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

private val ScoreExcellent = com.example.pakforces.ui.theme.ScoreExcellent
private val ScorePoor = com.example.pakforces.ui.theme.ScorePoor
private val ScoreGood = com.example.pakforces.ui.theme.ScoreGood
private val ScoreAverage = com.example.pakforces.ui.theme.ScoreAverage

private fun scoreColor(p: Int): Color = when {
    p >= 85 -> ScoreExcellent
    p >= 65 -> ScoreGood
    p >= 40 -> ScoreAverage
    else -> ScorePoor
}
