package com.example.pakforces.data.model

import com.squareup.moshi.JsonClass

/**
 * A single shape to draw on a non-verbal figure canvas.
 * Designed to be JSON-serializable so question authors can describe
 * diagrams without needing image assets.
 *
 * Supported types:
 *  - circle, square, triangle, pentagon, hexagon, star, arrow
 *  - halfCircleTop, halfCircleBottom, halfCircleLeft, halfCircleRight
 *  - quarterCircle, diamond, parallelogram, trapezoid, cross
 *  - dot, line, dotGrid
 *
 * Properties:
 *  - fill: "solid" | "hollow" | "striped" | "dotted"
 *  - rotation: 0..360 degrees
 *  - x, y: normalized center position (-1..1, default 0,0 = center)
 *  - size: normalized size 0.2..1.5 (default 1.0)
 *  - count: for dotGrid — number of dots (default 1)
 *  - color: optional tint ("primary" | "secondary" | "accent"), default = primary
 */
@JsonClass(generateAdapter = true)
data class Shape(
    val type: String,
    val fill: String = "solid",
    val rotation: Float = 0f,
    val x: Float = 0f,
    val y: Float = 0f,
    val size: Float = 1f,
    val count: Int = 1,
    val color: String = "primary"
)

/**
 * A figure is a collection of shapes drawn on one canvas cell.
 */
@JsonClass(generateAdapter = true)
data class Figure(
    val shapes: List<Shape> = emptyList()
)

/**
 * Extended question format that supports figures (for non-verbal) AND
 * still supports plain text options (for verbal/academic).
 *
 * For text questions: use `q`, `o` (list of strings), `a` (int index)
 * For non-verbal questions: use `q` (the prompt), `figures` (the sequence shown),
 *   `o` can be either strings OR list of figure objects — see OptionParser.
 *
 * To keep backward compatibility, when `figures` is absent the question is
 * treated as a plain text question.
 */
@JsonClass(generateAdapter = true)
data class QuestionJsonV2(
    val id: String,
    val c: String,
    val s: String,
    val q: String,
    val o: List<String> = emptyList(),
    val fo: List<Figure> = emptyList(),   // figure options (for non-verbal)
    val figs: List<Figure> = emptyList(), // figure sequence shown in the question stem
    val a: Int,
    val e: String
)
