package com.example.pakforces.data.model

import com.squareup.moshi.JsonClass

/** A single question as it appears in the JSON asset files. */
@JsonClass(generateAdapter = true)
data class QuestionJson(
    val id: String,
    val c: String,   // category key
    val s: String,   // sub-category / topic
    val q: String,   // question text
    val o: List<String>,  // 4 options
    val a: Int,           // index of correct option (0..3)
    val e: String         // explanation
)

/** Metadata for a question bank file in assets. */
data class QuestionBankFile(
    val force: Force,
    val assetPath: String
)
