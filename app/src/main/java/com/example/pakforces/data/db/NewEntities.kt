package com.example.pakforces.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Per-question user note (revision aid). */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val questionId: String,
    val note: String,
    val updatedAt: Long
)

/** Bookmark folder for organizing saved questions. */
@Entity(tableName = "bookmark_folders")
data class BookmarkFolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String,
    val createdAt: Long
)

@Entity(tableName = "bookmark_folder_links", primaryKeys = ["questionId", "folderId"])
data class BookmarkFolderLinkEntity(
    val questionId: String,
    val folderId: Long,
    val addedAt: Long
)

/** Daily mission: 3-5 micro goals for the day. */
@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey val dayKey: String,  // yyyy-MM-dd
    val xpEarned: Int,
    val goalsJson: String,  // JSON list of MissionGoal
    val completedAt: Long?
)

/** Flashcard progress (SRS). */
@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey val id: String,   // e.g. "synonym-benign"
    val category: String,
    val front: String,
    val back: String,
    val intervalDays: Int = 1,
    val ease: Float = 2.5f,
    val dueAt: Long = 0L,        // epoch millis
    val reviewCount: Int = 0
)

/** Exam date set by user. Drives countdown + study plan. */
@Entity(tableName = "exam_date")
data class ExamDateEntity(
    @PrimaryKey val id: Int = 1,
    val force: String,
    val examDate: Long,   // epoch millis
    val createdAt: Long
)

/** Pomodoro focus session log. */
@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayKey: String,
    val durationMin: Int,
    val startedAt: Long,
    val completedAt: Long?
)

/** SRS schedule for wrong answers — each wrong question re-surfaces on a schedule. */
@Entity(tableName = "srs_schedule", primaryKeys = ["questionId"])
data class SrsScheduleEntity(
    val questionId: String,
    val force: String,
    val category: String,
    val intervalDays: Int = 1,
    val ease: Float = 2.5f,
    val dueAt: Long,
    val lastReviewedAt: Long?,
    val reviewCount: Int = 0
)

/** Per-question timing log (for time-per-question analytics). */
@Entity(tableName = "question_timing", primaryKeys = ["questionId", "answeredAt"])
data class QuestionTimingEntity(
    val questionId: String,
    val category: String,
    val force: String,
    val timeSec: Int,
    val correct: Boolean,
    val answeredAt: Long
)
