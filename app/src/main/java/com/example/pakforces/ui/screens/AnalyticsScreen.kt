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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pakforces.data.model.Category
import com.example.pakforces.ui.charts.CalendarHeatmap
import com.example.pakforces.ui.charts.LineChart
import com.example.pakforces.ui.charts.RadarChart
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory

@Composable
fun AnalyticsScreen(onBack: () -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val repo = appViewModel.repository
    val features = appViewModel.features
    val state by appViewModel.state.collectAsState()
    val force = state.force

    val results by repo.allResults().collectAsState(initial = emptyList())
    val timingByCat by features.timingByCategory().collectAsState(initial = emptyList())
    val recentTiming by features.recentTiming(200).collectAsState(initial = emptyList())

    // Calculate heatmap intensity (questions answered per day)
    val heatmapData = remember(recentTiming, results) {
        val map = mutableMapOf<String, Int>()
        for (t in recentTiming) {
            val day = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date(t.answeredAt))
            map[day] = (map[day] ?: 0) + 1
        }
        for (r in results) {
            val day = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date(r.completedAt))
            map[day] = (map[day] ?: 0) + r.totalQuestions
        }
        // Convert counts to intensity 0..4
        map.mapValues { (_, count) ->
            when {
                count >= 50 -> 4
                count >= 25 -> 3
                count >= 10 -> 2
                count >= 1 -> 1
                else -> 0
            }
        }
    }

    // Radar data — accuracy per category
    val radarData = remember(timingByCat) {
        Category.entries.map { cat ->
            val entry = timingByCat.firstOrNull { it.category == cat.key }
            val acc = if (entry != null && entry.cnt > 0) entry.correctCount.toFloat() / entry.cnt else 0f
            cat.display to acc
        }.filter { it.second > 0f || timingByCat.any { it.category == it.first } }
    }

    // Line chart — last 14 test scores
    val scoreHistory = remember(results) {
        results.take(14).reversed().map { it.scorePercent.toFloat() }
    }

    // Weakness diagnosis
    val weakest = remember(timingByCat) {
        timingByCat.filter { it.cnt > 0 }
            .minByOrNull { it.correctCount.toFloat() / it.cnt }
    }
    val strongest = remember(timingByCat) {
        timingByCat.filter { it.cnt > 0 }
            .maxByOrNull { it.correctCount.toFloat() / it.cnt }
    }

    // Time per question
    val avgTimePerCat = remember(timingByCat) {
        timingByCat.associate { it.category to it.avgTime }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Analytics & Insights", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("AI Weakness Diagnosis", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(20.dp))

            // Radar — accuracy per category
            AnalyticsCard("Accuracy by Category") {
                if (radarData.size >= 3) {
                    RadarChart(data = radarData, modifier = Modifier.fillMaxWidth())
                } else {
                    Text("Kuch aur tests solve karein taake diagnosis show ho sake.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(12.dp))

            // Weakness diagnosis
            if (weakest != null) {
                AnalyticsCard("Weakest Area") {
                    val cat = runCatching { Category.fromKey(weakest.category) }.getOrDefault(Category.VERBAL)
                    val acc = if (weakest.cnt > 0) (weakest.correctCount * 100 / weakest.cnt) else 0
                    Text("🎯 ${cat.display}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Sahi jawab: $acc% — is par zyada practice karein.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(Modifier.height(12.dp))
            }
            if (strongest != null && strongest.category != weakest?.category) {
                AnalyticsCard("Strongest Area") {
                    val cat = runCatching { Category.fromKey(strongest.category) }.getOrDefault(Category.VERBAL)
                    val acc = if (strongest.cnt > 0) (strongest.correctCount * 100 / strongest.cnt) else 0
                    Text("⭐ ${cat.display}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Sahi jawab: $acc% — shabash, aise hi continue karein.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(Modifier.height(12.dp))
            }

            // Score history line chart
            AnalyticsCard("Score History (last ${scoreHistory.size} tests)") {
                if (scoreHistory.size >= 2) {
                    LineChart(points = scoreHistory, modifier = Modifier.fillMaxWidth())
                } else {
                    Text("Kam az kam 2 tests complete karein taake graph show ho.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(12.dp))

            // Time per question
            AnalyticsCard("Average Time Per Question") {
                if (avgTimePerCat.isNotEmpty()) {
                    avgTimePerCat.entries.forEach { (cat, sec) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(Category.fromKey(cat).display, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("$sec sec", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    Text("Test solve karne ke baad yahan time analytics show hoga.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(12.dp))

            // Calendar heatmap
            AnalyticsCard("Daily Activity Heatmap") {
                CalendarHeatmap(data = heatmapData, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun AnalyticsCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}
