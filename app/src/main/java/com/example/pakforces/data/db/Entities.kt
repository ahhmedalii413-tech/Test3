package com.example.pakforces.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A persisted question. We embed category and sub-category as plain strings
 * so we can filter by them; the force is also stored as a string key.
 */
@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val force: String,        // Force.key
    val category: String,     // Category.key
    val subCategory: String,
    val question: String,
    val option0: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val correctIndex: Int,
    val explanation: String,
    /** For non-verbal: each option may be a JSON-encoded Figure. UI detects this by
     *  checking if the option string starts with `{` and contains `"shapes"`. */
    val isFigure: Boolean = false,
    /** Pipe-separated JSON-encoded Figure objects shown in the question stem (sequence). */
    val stemFigures: String = ""
)

@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val force: String,
    val category: String,
    val mode: String,          // TIMED / PRACTICE
    val totalQuestions: Int,
    val correct: Int,
    val wrong: Int,
    val skipped: Int,
    val scorePercent: Int,
    val timeTakenSec: Int,
    val completedAt: Long      // epoch millis
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val questionId: String,
    val addedAt: Long
)

/** Tracks a wrong answer for the Revision mode + weak-area analytics. */
@Entity(tableName = "wrong_answers", primaryKeys = ["questionId"])
data class WrongAnswerEntity(
    val questionId: String,
    val force: String,
    val category: String,
    val timesWrong: Int,
    val lastWrongAt: Long
)

/** Daily challenge progress: one row per day (epoch day). */
@Entity(tableName = "daily_challenge")
data class DailyChallengeEntity(
    @PrimaryKey val dayKey: String,    // yyyy-MM-dd
    val force: String,
    val correct: Int,
    val total: Int,
    val completedAt: Long
)
