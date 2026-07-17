package com.example.pakforces.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Radar chart for AI Weakness Diagnosis — visualises user accuracy per category.
 * 8 axes (one per Category). Higher value = better accuracy.
 */
@Composable
fun RadarChart(
    data: List<Pair<String, Float>>,  // (label, value 0..1)
    modifier: Modifier = Modifier,
    size: Int = 280,
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val grid = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    val labelColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier.size(size.dp).padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size.dp)) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val r = min(cx, cy) * 0.75f
            val n = data.size.coerceAtLeast(3)

            // Grid rings (4 concentric)
            for (i in 1..4) {
                val rr = r * i / 4f
                val path = Path()
                for (j in 0 until n) {
                    val angle = (2 * PI * j / n) - PI / 2
                    val x = cx + (rr * cos(angle)).toFloat()
                    val y = cy + (rr * sin(angle)).toFloat()
                    if (j == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, grid, style = Stroke(width = 1.5.dp.toPx()))
            }

            // Axis lines + labels
            for (j in 0 until n) {
                val angle = (2 * PI * j / n) - PI / 2
                drawLine(
                    grid,
                    start = Offset(cx, cy),
                    end = Offset((cx + r * cos(angle)).toFloat(), (cy + r * sin(angle)).toFloat()),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            // Data polygon
            val dataPath = Path()
            for (j in data.indices) {
                val value = data[j].second.coerceIn(0f, 1f)
                val angle = (2 * PI * j / n) - PI / 2
                val x = (cx + r * value * cos(angle)).toFloat()
                val y = (cy + r * value * sin(angle)).toFloat()
                if (j == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()
            drawPath(dataPath, secondary.copy(alpha = 0.35f))
            drawPath(dataPath, secondary, style = Stroke(width = 2.5.dp.toPx()))

            // Data points
            for (j in data.indices) {
                val value = data[j].second.coerceIn(0f, 1f)
                val angle = (2 * PI * j / n) - PI / 2
                val x = (cx + r * value * cos(angle)).toFloat()
                val y = (cy + r * value * sin(angle)).toFloat()
                drawCircle(primary, 4.dp.toPx(), Offset(x, y))
            }
        }
    }
    // Labels row (text below chart since canvas doesn't render text nicely)
    Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = data.joinToString("  ·  ") { "${it.first}: ${(it.second * 100).toInt()}%" },
            style = MaterialTheme.typography.bodySmall,
            color = labelColor.copy(alpha = 0.7f),
        )
    }
}

/**
 * Simple line chart for score-over-time visualization.
 */
@Composable
fun LineChart(
    points: List<Float>,  // values 0..100
    modifier: Modifier = Modifier,
    height: Int = 160,
    accentColor: Color = MaterialTheme.colorScheme.secondary,
) {
    val primary = MaterialTheme.colorScheme.primary
    val grid = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    Box(
        modifier = modifier.fillMaxWidth().height(height.dp).padding(8.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height.dp)) {
            val w = size.width
            val h = size.height
            // Grid lines (3 horizontal)
            for (i in 0..3) {
                val y = h * i / 3f
                drawLine(grid, Offset(0f, y), Offset(w, y), strokeWidth = 1.dp.toPx())
            }

            if (points.size < 2) {
                return@Canvas
            }
            val maxV = 100f
            val stepX = w / (points.size - 1).toFloat()
            // Line
            val path = Path()
            points.forEachIndexed { i, v ->
                val x = i * stepX
                val y = h - (v / maxV) * h
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, accentColor, style = Stroke(width = 3.dp.toPx()))

            // Fill under line
            val fillPath = Path().apply {
                addPath(path)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(fillPath, accentColor.copy(alpha = 0.15f))

            // Points
            points.forEachIndexed { i, v ->
                val x = i * stepX
                val y = h - (v / maxV) * h
                drawCircle(primary, 4.dp.toPx(), Offset(x, y))
            }
        }
    }
}

/**
 * Calendar heatmap (GitHub contribution-style).
 * data: list of (dayKey, intensity 0..4)
 */
@Composable
fun CalendarHeatmap(
    data: Map<String, Int>,  // dayKey (yyyy-MM-dd) -> intensity 0..4
    modifier: Modifier = Modifier,
    weeks: Int = 12,
) {
    val low = MaterialTheme.colorScheme.surfaceVariant
    val high = MaterialTheme.colorScheme.secondary
    val colors = listOf(
        low.copy(alpha = 0.4f),
        high.copy(alpha = 0.25f),
        high.copy(alpha = 0.5f),
        high.copy(alpha = 0.75f),
        high,
    )
    val cellSize = 14.dp
    val gap = 3.dp

    Column(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        // Build a weeks×7 grid for the last `weeks` weeks
        val cal = java.util.Calendar.getInstance()
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time)
        // Move to start (weeks ago, Sunday)
        cal.add(java.util.Calendar.WEEK_OF_YEAR, -weeks + 1)
        cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY)

        val days = (0 until (weeks * 7)).map { i ->
            val c = cal.clone() as java.util.Calendar
            c.add(java.util.Calendar.DAY_OF_YEAR, i)
            val key = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(c.time)
            Triple(key, c.get(java.util.Calendar.DAY_OF_WEEK), data[key] ?: 0)
        }

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height((7 * (cellSize.value + gap.value)).dp)
        ) {
            val cellPx = cellSize.toPx()
            val gapPx = gap.toPx()
            val totalW = weeks * (cellPx + gapPx)
            val startOffsetX = (size.width - totalW) / 2f
            days.forEachIndexed { i, (key, _, intensity) ->
                val week = i / 7
                val dow = i % 7
                val x = startOffsetX + week * (cellPx + gapPx)
                val y = dow * (cellPx + gapPx)
                drawRoundRect(
                    color = colors[intensity.coerceIn(0, 4)],
                    topLeft = Offset(x, y),
                    size = Size(cellPx, cellPx),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Last $weeks weeks · daily activity",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

/**
 * Circular progress ring (for subject mastery levels).
 */
@Composable
fun ProgressRing(
    progress: Float,  // 0..1
    modifier: Modifier = Modifier,
    size: Int = 80,
    label: String = "",
    accentColor: Color = MaterialTheme.colorScheme.secondary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    Box(modifier = modifier.size(size.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size.dp)) {
            val stroke = 6.dp.toPx()
            val canvasW = this.size.width
            val canvasH = this.size.height
            val r = min(canvasW, canvasH) / 2f - stroke
            drawCircle(trackColor, r, center = Offset(canvasW / 2f, canvasH / 2f), style = Stroke(stroke))
            drawArc(
                accentColor, -90f, 360f * progress.coerceIn(0f, 1f),
                false, style = Stroke(stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                topLeft = Offset(stroke, stroke),
                size = Size(canvasW - stroke * 2, canvasH - stroke * 2),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            if (label.isNotBlank()) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}
