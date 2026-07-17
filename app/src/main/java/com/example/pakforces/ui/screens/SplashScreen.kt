package com.example.pakforces.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pakforces.ui.components.PakForcesCrest
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onContinue: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1800) // cinematic pause
        onContinue()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.background,
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            // Bouncy crest entry
            val scale = remember { Animatable(0.4f) }
            val alpha = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                alpha.animateTo(1f, tween(500))
                scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }

            Box(modifier = Modifier.graphicsLayer {
                this.scaleX = scale.value
                this.scaleY = scale.value
                this.alpha = alpha.value
            }) {
                PakForcesCrest(
                    size = 180,
                    label = "PAK FORCES PREP",
                    subtitle = "Army · Air Force · Navy",
                )
            }
            Spacer(Modifier.height(48.dp))
            Text(
                text = "Elite Initial Test Preparation",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { this.alpha = alpha.value },
            )
        }
    }
}
