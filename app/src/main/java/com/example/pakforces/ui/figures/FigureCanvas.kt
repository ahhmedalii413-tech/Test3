package com.example.pakforces.ui.figures

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.example.pakforces.data.model.Figure
import com.example.pakforces.data.model.Shape
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Renders a single Figure (collection of shapes) on a square canvas.
 * Used both for the question stem (sequence of figures) and for the options.
 */
@Composable
fun FigureCanvas(
    figure: Figure,
    modifier: Modifier = Modifier,
    size: Int = 96,
    primary: Color = MaterialTheme.colorScheme.primary,
    secondary: Color = MaterialTheme.colorScheme.secondary,
    accent: Color = MaterialTheme.colorScheme.tertiary,
    background: Color = MaterialTheme.colorScheme.surface,
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.size(size.dp)) {
            val cw = this.size.width
            val ch = this.size.height
            val canvasSize = min(cw, ch)
            val unit = canvasSize * 0.32f  // base unit for shapes
            val cx = cw / 2f
            val cy = ch / 2f
            for (shape in figure.shapes) {
                val color = when (shape.color) {
                    "secondary" -> secondary
                    "accent" -> accent
                    else -> primary
                }
                val sx = cx + shape.x * canvasSize * 0.3f
                val sy = cy + shape.y * canvasSize * 0.3f
                val sSize = unit * shape.size
                drawShape(shape, sx, sy, sSize, color)
            }
        }
    }
}

private fun DrawScope.drawShape(shape: Shape, cx: Float, cy: Float, size: Float, color: Color) {
    val style = when (shape.fill) {
        "hollow" -> Stroke(width = size * 0.12f)
        "dotted" -> Stroke(width = size * 0.10f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(size * 0.05f, size * 0.08f), 0f))
        "striped" -> Fill  // we'll handle stripes via path effect
        else -> Fill
    }

    rotate(shape.rotation, pivot = Offset(cx, cy)) {
        when (shape.type) {
            "circle" -> drawCircle(color, size * 0.5f, Offset(cx, cy), style = style)
            "dot" -> {
                val n = shape.count.coerceAtLeast(1)
                val spacing = size * 0.6f
                val startX = cx - (n - 1) * spacing / 2f
                for (i in 0 until n) drawCircle(color, size * 0.15f, Offset(startX + i * spacing, cy))
            }
            "dotGrid" -> {
                val n = shape.count.coerceAtLeast(1)
                val cols = kotlin.math.ceil(kotlin.math.sqrt(n.toFloat())).toInt()
                val rows = kotlin.math.ceil(n.toFloat() / cols)
                val spacing = size * 0.5f
                val startX = cx - (cols - 1) * spacing / 2f
                val startY = cy - (rows - 1) * spacing / 2f
                for (i in 0 until n) {
                    val r = i / cols
                    val c = i % cols
                    drawCircle(color, size * 0.12f, Offset(startX + c * spacing, startY + r * spacing))
                }
            }
            "square" -> drawRect(color, Offset(cx - size/2, cy - size/2), Size(size, size), style = style)
            "triangle" -> drawPolygon(color, regularPolygon(cx, cy, size * 0.55f, 3, -PI/2f), style)
            "pentagon" -> drawPolygon(color, regularPolygon(cx, cy, size * 0.55f, 5, -PI/2f), style)
            "hexagon" -> drawPolygon(color, regularPolygon(cx, cy, size * 0.55f, 6, 0.0), style)
            "star" -> drawPolygon(color, starPolygon(cx, cy, size * 0.55f, size * 0.25f, 5, -PI/2f), style)
            "diamond" -> drawPolygon(color, regularPolygon(cx, cy, size * 0.55f, 4, -PI/2f), style)
            "arrow" -> drawArrow(color, cx, cy, size, style)
            "halfCircleTop" -> drawHalfCircle(color, cx, cy, size, "top", style)
            "halfCircleBottom" -> drawHalfCircle(color, cx, cy, size, "bottom", style)
            "halfCircleLeft" -> drawHalfCircle(color, cx, cy, size, "left", style)
            "halfCircleRight" -> drawHalfCircle(color, cx, cy, size, "right", style)
            "quarterCircle" -> drawQuarterCircle(color, cx, cy, size, style)
            "parallelogram" -> drawParallelogram(color, cx, cy, size, style)
            "trapezoid" -> drawTrapezoid(color, cx, cy, size, style)
            "cross" -> drawCross(color, cx, cy, size, style)
            "line" -> drawLine(color, Offset(cx - size/2, cy), Offset(cx + size/2, cy), strokeWidth = size * 0.10f, cap = StrokeCap.Round)
        }
    }
}

private fun regularPolygon(cx: Float, cy: Float, r: Float, sides: Int, startAngle: Double): List<Offset> {
    val pts = ArrayList<Offset>(sides)
    for (i in 0 until sides) {
        val angle = startAngle + 2 * PI * i / sides
        pts += Offset((cx + r * cos(angle)).toFloat(), (cy + r * sin(angle)).toFloat())
    }
    return pts
}

private fun starPolygon(cx: Float, cy: Float, rOuter: Float, rInner: Float, points: Int, startAngle: Double): List<Offset> {
    val pts = ArrayList<Offset>(points * 2)
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) rOuter else rInner
        val angle = startAngle + PI * i / points
        pts += Offset((cx + r * cos(angle)).toFloat(), (cy + r * sin(angle)).toFloat())
    }
    return pts
}

private fun DrawScope.drawPolygon(color: Color, points: List<Offset>, style: androidx.compose.ui.graphics.drawscope.DrawStyle) {
    val path = Path().apply {
        moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
        close()
    }
    drawPath(path, color, style = style)
}

private fun DrawScope.drawArrow(color: Color, cx: Float, cy: Float, size: Float, style: androidx.compose.ui.graphics.drawscope.DrawStyle) {
    val path = Path().apply {
        moveTo(cx - size/2, cy)
        lineTo(cx + size/4, cy)
        lineTo(cx + size/4, cy - size/3)
        lineTo(cx + size/2, cy)
        lineTo(cx + size/4, cy + size/3)
        lineTo(cx + size/4, cy)
        close()
    }
    drawPath(path, color, style = style)
}

private fun DrawScope.drawHalfCircle(color: Color, cx: Float, cy: Float, size: Float, dir: String, style: androidx.compose.ui.graphics.drawscope.DrawStyle) {
    val rect = androidx.compose.ui.geometry.Rect(cx - size/2, cy - size/2, cx + size/2, cy + size/2)
    val (startAngle, sweep) = when (dir) {
        "top" -> 180f to 180f
        "bottom" -> 0f to 180f
        "left" -> 270f to 180f
        "right" -> 90f to 180f
        else -> 0f to 180f
    }
    drawArc(color, startAngle, sweep, false, style, topLeft = Offset(rect.left, rect.top), size = Size(rect.width, rect.height))
}

private fun DrawScope.drawQuarterCircle(color: Color, cx: Float, cy: Float, size: Float, style: androidx.compose.ui.graphics.drawscope.DrawStyle) {
    drawArc(color, 0f, 90f, false, style, topLeft = Offset(cx - size/2, cy - size/2), size = Size(size, size))
}

private fun DrawScope.drawParallelogram(color: Color, cx: Float, cy: Float, size: Float, style: androidx.compose.ui.graphics.drawscope.DrawStyle) {
    val path = Path().apply {
        moveTo(cx - size*0.4f, cy + size/2)
        lineTo(cx + size*0.2f, cy + size/2)
        lineTo(cx + size*0.4f, cy - size/2)
        lineTo(cx - size*0.2f, cy - size/2)
        close()
    }
    drawPath(path, color, style = style)
}

private fun DrawScope.drawTrapezoid(color: Color, cx: Float, cy: Float, size: Float, style: androidx.compose.ui.graphics.drawscope.DrawStyle) {
    val path = Path().apply {
        moveTo(cx - size/2, cy + size/2)
        lineTo(cx + size/2, cy + size/2)
        lineTo(cx + size*0.25f, cy - size/2)
        lineTo(cx - size*0.25f, cy - size/2)
        close()
    }
    drawPath(path, color, style = style)
}

private fun DrawScope.drawCross(color: Color, cx: Float, cy: Float, size: Float, style: androidx.compose.ui.graphics.drawscope.DrawStyle) {
    val w = size * 0.25f
    val path = Path().apply {
        moveTo(cx - w, cy - size/2)
        lineTo(cx + w, cy - size/2)
        lineTo(cx + w, cy - w)
        lineTo(cx + size/2, cy - w)
        lineTo(cx + size/2, cy + w)
        lineTo(cx + w, cy + w)
        lineTo(cx + w, cy + size/2)
        lineTo(cx - w, cy + size/2)
        lineTo(cx - w, cy + w)
        lineTo(cx - size/2, cy + w)
        lineTo(cx - size/2, cy - w)
        lineTo(cx - w, cy - w)
        close()
    }
    drawPath(path, color, style = style)
}
