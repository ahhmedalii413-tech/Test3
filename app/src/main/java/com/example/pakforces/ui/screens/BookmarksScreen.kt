package com.example.pakforces.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pakforces.data.db.QuestionEntity
import com.example.pakforces.ui.components.EmptyState
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory

@Composable
fun BookmarksScreen(onBack: () -> Unit, onOpenQuestion: (String) -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val repo = appViewModel.repository
    val bookmarks by repo.bookmarkedQuestions().collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Bookmarks", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Your saved questions for revision", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(20.dp))

            if (bookmarks.isEmpty()) {
                EmptyState("No bookmarks yet", "Tap the bookmark icon on any question to save it for revision.")
                return@Column
            }

            LazyColumn(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                items(bookmarks) { q -> BookmarkRow(q) }
            }
        }
    }
}

@Composable
private fun BookmarkRow(q: QuestionEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(Icons.Filled.Bookmark, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 2.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(q.question, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, maxLines = 3)
                Spacer(Modifier.height(4.dp))
                Text("Force: ${q.force} · Category: ${q.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(Modifier.height(8.dp))
                Text("Answer: ${listOf(q.option0, q.option1, q.option2, q.option3).getOrNull(q.correctIndex)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
        }
    }
}
