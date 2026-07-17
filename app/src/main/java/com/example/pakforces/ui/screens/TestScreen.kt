package com.example.pakforces.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pakforces.data.db.QuestionEntity
import com.example.pakforces.data.model.Category
import com.example.pakforces.data.model.Force
import com.example.pakforces.data.model.TestMode
import com.example.pakforces.ui.figures.FigureCanvas
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TestScreen(
    forceKey: String,
    categoryKey: String,
    modeKey: String,
    onFinish: (correct: Int, total: Int, timeSec: Int) -> Unit,
    onExit: () -> Unit,
) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val repo = appViewModel.repository
    val features = appViewModel.features
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val force = Force.fromKey(forceKey)
    val category = Category.fromKey(categoryKey)
    val mode = runCatching { TestMode.valueOf(modeKey) }.getOrDefault(TestMode.TIMED)
    val state by appViewModel.state.collectAsState()

    val questionCount = state.testLength.count

    var questions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    val answers = remember { mutableStateListOf<Int?>(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null) }
    val revealed = remember { mutableStateListOf<Boolean>() }
    var bookmarked by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf(if (mode == TestMode.TIMED) state.testLength.timeSec else 0) }
    var elapsedSec by remember { mutableIntStateOf(0) }
    var loaded by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    // Load questions
    LaunchedEffect(forceKey, categoryKey, modeKey) {
        questions = repo.randomQuestions(force, category, questionCount)
        repeat(questions.size) {
            if (answers.size <= it) answers.add(null)
            if (revealed.size <= it) revealed.add(false)
        }
        loaded = true
    }

    // Timer
    LaunchedEffect(loaded, mode) {
        if (!loaded) return@LaunchedEffect
        while (true) {
            delay(1000)
            elapsedSec += 1
            if (mode == TestMode.TIMED) {
                timeLeft -= 1
                if (timeLeft <= 0) {
                    // Auto-submit
                    val correct = computeCorrect(questions, answers)
                    val wrong = questions.size - correct
                    val score = if (questions.size > 0) (correct * 100) / questions.size else 0
                    appViewModel.saveTestResult(force, category, mode, questions.size, correct, wrong, 0, score, elapsedSec)
                    onFinish(correct, questions.size, elapsedSec)
                    break
                }
            }
        }
    }

    if (!loaded) {
        com.example.pakforces.ui.components.LoadingScreen("Building your test…")
        return
    }

    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No questions available for this category.", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val q = questions[currentIndex]
    val selected = answers.getOrNull(currentIndex)

    // Refresh bookmark state
    LaunchedEffect(currentIndex) {
        bookmarked = repo.isBookmarked(q.id)
    }

    fun selectOption(idx: Int) {
        if (mode == TestMode.PRACTICE && revealed[currentIndex]) return
        while (answers.size <= currentIndex) answers.add(null)
        answers[currentIndex] = idx
        if (mode == TestMode.PRACTICE) {
            while (revealed.size <= currentIndex) revealed.add(false)
            revealed[currentIndex] = true
            if (idx != q.correctIndex) {
                scope.launch {
                    repo.recordWrong(q.id, force, category)
                    features.scheduleSrs(q)
                }
                appViewModel.loseHeart()
                appViewModel.feedback.wrong()
            } else {
                scope.launch { repo.clearWrong(q.id) }
                appViewModel.feedback.correct()
            }
        } else {
            // TIMED — sound/haptic but no reveal
            if (idx == q.correctIndex) appViewModel.feedback.correct() else appViewModel.feedback.wrong()
        }
        // Log timing for analytics
        val timeSpent = if (currentIndex == 0) 15 else 20  // simplified
        scope.launch {
            features.logTiming(q.id, force.key, category.key, timeSpent, idx == q.correctIndex)
        }
    }

    fun next() {
        if (currentIndex < questions.size - 1) currentIndex++
    }
    fun prev() { if (currentIndex > 0) currentIndex-- }

    fun submit() {
        val correct = computeCorrect(questions, answers)
        val wrong = questions.size - correct
        val score = if (questions.size > 0) (correct * 100) / questions.size else 0
        appViewModel.saveTestResult(force, category, mode, questions.size, correct, wrong, 0, score, elapsedSec)
        onFinish(correct, questions.size, elapsedSec)
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { showExitDialog = true }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Exit", tint = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("${currentIndex + 1} / ${questions.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text(category.display, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f))
                }
                if (mode == TestMode.TIMED) {
                    val danger = timeLeft <= 60
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (danger) MaterialTheme.colorScheme.error.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Timer, contentDescription = null, tint = if (danger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(formatTime(timeLeft), style = MaterialTheme.typography.labelLarge, color = if (danger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
                IconButton(onClick = {
                    bookmarked = !bookmarked
                    appViewModel.toggleBookmark(q.id)
                }) {
                    Icon(if (bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder, contentDescription = "Bookmark", tint = if (bookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            }

            // Progress
            LinearProgressIndicator(
                progress = { (currentIndex + 1f) / questions.size },
                modifier = Modifier.fillMaxWidth().height(6.dp).padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            // Question content
            AnimatedContent(
                targetState = currentIndex,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                label = "q",
                modifier = Modifier.weight(1f),
            ) { page ->
                val cq = questions[page]
                Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                    Text(
                        text = cq.question,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    )

                    // If this question has stem figures (sequence shown), render them
                    if (cq.stemFigures.isNotBlank()) {
                        val stemFigs = repo.decodeStemFigures(cq.stemFigures)
                        if (stemFigs.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                stemFigs.forEach { fig ->
                                    FigureCanvas(figure = fig, size = 80)
                                }
                            }
                        }
                    }

                    val opts = listOf(cq.option0, cq.option1, cq.option2, cq.option3)
                    val sel = answers.getOrNull(page)
                    val isRevealed = revealed.getOrNull(page) ?: false
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        opts.forEachIndexed { idx, text ->
                            val stateColor = when {
                                !isRevealed && sel == idx -> MaterialTheme.colorScheme.primary
                                isRevealed && idx == cq.correctIndex -> Color(0xFF2E7D32)
                                isRevealed && sel == idx && idx != cq.correctIndex -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.surface
                            }
                            val textColor = when {
                                !isRevealed && sel == idx -> Color.White
                                isRevealed && idx == cq.correctIndex -> Color.White
                                isRevealed && sel == idx && idx != cq.correctIndex -> Color.White
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                            // Detect if this option is a figure (JSON with "shapes")
                            val isFigureOpt = cq.isFigure || (text.startsWith("{") && text.contains("\"shapes\""))
                            OptionRow(
                                index = idx,
                                text = if (isFigureOpt) "" else text,
                                figure = if (isFigureOpt) repo.decodeFigure(text) else null,
                                color = stateColor,
                                textColor = textColor,
                                isRevealed = isRevealed,
                                isCorrect = idx == cq.correctIndex,
                                isSelected = sel == idx,
                                onClick = {
                                    if (page == currentIndex) selectOption(idx)
                                },
                            )
                        }
                    }
                    if (mode == TestMode.PRACTICE && isRevealed) {
                        Spacer(Modifier.height(16.dp))
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Wajah (Explanation)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(cq.explanation, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            // Bottom nav
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = { prev() },
                    enabled = currentIndex > 0,
                    modifier = Modifier.height(52.dp),
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Prev")
                }
                if (currentIndex == questions.size - 1) {
                    Button(
                        onClick = { submit() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                        modifier = Modifier.height(52.dp),
                    ) {
                        Icon(Icons.Filled.Flag, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Submit", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Button(
                        onClick = { next() },
                        modifier = Modifier.height(52.dp),
                    ) {
                        Text("Next", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    if (showExitDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showExitDialog = false },
            confirmButton = {
                Button(
                    onClick = { onExit() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) { Text("Exit") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExitDialog = false }) { Text("Keep going") }
            },
            title = { Text("Exit test?") },
            text = { Text("Your progress on this test will be lost.") },
        )
    }
}

@Composable
private fun OptionRow(
    index: Int,
    text: String,
    figure: com.example.pakforces.data.model.Figure?,
    color: Color,
    textColor: Color,
    isRevealed: Boolean,
    isCorrect: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val label = ('A' + index).toString()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (color == MaterialTheme.colorScheme.surface) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = textColor, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        if (figure != null) {
            // Render figure option
            FigureCanvas(
                figure = figure,
                size = 72,
                background = Color.Transparent,
            )
            Spacer(Modifier.width(8.dp))
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.weight(1f),
            )
        }
        if (isRevealed && isCorrect) Icon(Icons.Filled.Check, contentDescription = null, tint = textColor)
        if (isRevealed && isSelected && !isCorrect) Icon(Icons.Filled.Close, contentDescription = null, tint = textColor)
    }
}

private fun computeCorrect(questions: List<QuestionEntity>, answers: List<Int?>): Int {
    var c = 0
    questions.forEachIndexed { i, q -> if (answers.getOrNull(i) == q.correctIndex) c++ }
    return c
}

private fun formatTime(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%02d:%02d".format(m, s)
}
