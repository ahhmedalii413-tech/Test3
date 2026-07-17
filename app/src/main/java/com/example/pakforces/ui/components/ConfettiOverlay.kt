package com.example.pakforces.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.pakforces.ui.theme.BrandGold
import com.example.pakforces.ui.theme.ScoreExcellent
import com.example.pakforces.ui.theme.ScoreGood
import com.example.pakforces.ui.theme.ScorePoor
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Lightweight confetti burst — used on test completion / level-up.
 * Pure Compose Canvas, no extra dependencies.
 */
@Composable
fun ConfettiOverlay(
    active: Boolean,
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {},
) {
    if (!active) return

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(2500, easing = LinearEasing))
        delay(100)
        onDone()
    }

    val colors = listOf(BrandGold, ScoreExcellent, ScoreGood, MaterialTheme.colorScheme.tertiary, Color(0xFFE53935))
    val particles = remember {
        List(40) { i ->
            val angle = (i * 9).toDouble()
            ConfettiParticle(
                angle = angle,
                speed = 0.4f + Random.nextFloat() * 0.6f,
                color = colors[i % colors.size],
                size = 8f + Random.nextFloat() * 10f,
                rotationSpeed = 4f + Random.nextFloat() * 8f,
            )
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 3f
            val p = progress.value
            for (particle in particles) {
                val rad = Math.toRadians(particle.angle)
                val dist = size.minDimension * particle.speed * p
                val x = cx + (cos(rad) * dist).toFloat()
                val y = cy + (sin(rad) * dist).toFloat() + (p * p * 200f)  // gravity drop
                drawCircle(
                    color = particle.color.copy(alpha = (1f - p).coerceIn(0f, 1f)),
                    radius = particle.size / 2f,
                    center = Offset(x, y),
                )
            }
        }
    }
}

private data class ConfettiParticle(
    val angle: Double,
    val speed: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float,
)

/**
 * Bouncy score reveal — for Result screen big %.
 */
@Composable
fun BouncyScoreReveal(
    targetScore: Int,
    modifier: Modifier = Modifier,
    color: Color = ScoreExcellent,
) {
    val displayScore = remember { Animatable(0f) }

    LaunchedEffect(targetScore) {
        delay(200)
        displayScore.animateTo(
            targetValue = targetScore.toFloat(),
            animationSpec = tween(1200, easing = { androidx.compose.animation.core.FastOutSlowInEasing.transform(it) }),
        )
    }

    androidx.compose.material3.Text(
        text = "${displayScore.value.toInt()}%",
        style = MaterialTheme.typography.displayMedium,
        color = color,
        modifier = modifier.graphicsLayer {
            scaleX = 1f + (1f - (targetScore.toFloat() - displayScore.value) / targetScore.coerceAtLeast(1)) * 0.0f
            scaleY = 1f
        },
    )
}
