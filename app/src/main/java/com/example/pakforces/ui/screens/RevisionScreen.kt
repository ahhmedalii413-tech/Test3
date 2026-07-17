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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.pakforces.data.db.QuestionEntity
import com.example.pakforces.data.model.Category
import com.example.pakforces.data.model.Force
import com.example.pakforces.ui.components.EmptyState
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory

@Composable
fun RevisionScreen(forceKey: String, onBack: () -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val repo = appViewModel.repository
    val force = Force.fromKey(forceKey)
    var questions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    val revealed = remember { mutableStateListOf<Boolean>() }

    LaunchedEffect(forceKey) {
        questions = repo.wrongQuestions(force, 50)
        repeat(questions.size) { revealed.add(false) }
        loaded = true
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Revision · Pakistan ${force.display.removePrefix("Pakistan ")}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Questions you previously got wrong", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
                Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
            }
            Spacer(Modifier.height(20.dp))

            if (!loaded) return@Column
            if (questions.isEmpty()) {
                EmptyState("No wrong answers yet", "Take a few tests and any questions you miss will appear here for revision.")
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(questions) { idx, q ->
                    val isRevealed = revealed.getOrNull(idx) ?: false
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(q.question, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(12.dp))
                            val opts = listOf(q.option0, q.option1, q.option2, q.option3)
                            opts.forEachIndexed { i, txt ->
                                val isCorrect = i == q.correctIndex
                                val color = when {
                                    !isRevealed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    isCorrect -> Color(0xFF2E7D32)
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                }
                                val txtColor = when {
                                    !isRevealed -> MaterialTheme.colorScheme.onSurface
                                    isCorrect -> Color.White
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(color)
                                        .clickable { if (!isRevealed) revealed[idx] = true }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(if (isRevealed && isCorrect) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(('A' + i).toString(), color = txtColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Text(txt, color = txtColor, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                    if (isRevealed && isCorrect) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            if (isRevealed) {
                                Spacer(Modifier.height(8.dp))
                                Text("Answer: ${opts[q.correctIndex]}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                Text(q.explanation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            } else {
                                Spacer(Modifier.height(8.dp))
                                Text("Tap an option to reveal the answer.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}
