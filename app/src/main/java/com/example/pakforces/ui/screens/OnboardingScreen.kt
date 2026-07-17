package com.example.pakforces.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pakforces.ui.components.PakForcesCrest
import kotlinx.coroutines.launch

private data class OnboardPage(
    val icon: ImageVector,
    val title: String,
    val body: String,
)

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val pages = listOf(
        OnboardPage(Icons.Filled.Check, "Welcome", "Pak Forces Prep is your elite companion for the initial screening tests of the Pakistan Army, Air Force, and Navy."),
        OnboardPage(Icons.Filled.Check, "Verbal & Non-Verbal Intelligence", "Practice analogies, series, coding-decoding, direction sense, blood relations, and figure patterns just like the real test."),
        OnboardPage(Icons.Filled.Check, "Academic Mastery", "English, Mathematics, Pakistan Studies, Islamic Studies, General Knowledge, and Everyday Science — fully covered with verified answers and explanations."),
        OnboardPage(Icons.Filled.Check, "Daily Challenge & Progress", "Build a streak with the Daily Challenge, bookmark tricky questions, and track your accuracy across all forces in real time."),
    )
    val pager = rememberPagerState(initialPage = 0, pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
                        MaterialTheme.colorScheme.background,
                    )
                )
            ),
    ) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(40.dp))
            if (pager.currentPage == 0) {
                PakForcesCrest(size = 130, label = "PAK FORCES", subtitle = "Prep Elite")
            } else {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(pages[pager.currentPage].icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp))
                }
            }
            Spacer(Modifier.height(28.dp))

            HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = pages[page].title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = pages[page].body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // Dots
            Row(Modifier.padding(vertical = 20.dp), horizontalArrangement = Arrangement.Center) {
                repeat(pages.size) { i ->
                    val active = pager.currentPage == i
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(if (active) 10.dp else 8.dp)
                            .width(if (active) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (active) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f))
                    )
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = onDone) { Text("Skip") }
                Button(
                    onClick = {
                        if (pager.currentPage == pages.size - 1) onDone()
                        else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                ) {
                    Text(if (pager.currentPage == pages.size - 1) "Get Started" else "Next")
                    Spacer(Modifier.width(6.dp))
                    Icon(if (pager.currentPage == pages.size - 1) Icons.Filled.Check else Icons.Filled.ArrowForward, contentDescription = null)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
