package com.example.pakforces.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalXP: Int = 0,
    val currentHearts: Int = 5,
    val maxHearts: Int = 5,
    val streakDays: Int = 0,
    val lastActiveDay: String = "",  // yyyy-MM-dd
    val testsCompleted: Int = 0,
    val questionsAnswered: Int = 0,
    val correctAnswers: Int = 0,
    val achievements: String = "",  // comma-separated achievement IDs
    val streakFreezes: Int = 0,        // available freezes
    val lastHeartRefillAt: Long = 0,   // for timed refill
    val focusMinutes: Int = 0,
    val vocabularyMastered: Int = 0,
)

@Entity(tableName = "xp_history")
data class XpHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayKey: String,   // yyyy-MM-dd
    val xp: Int,
    val activity: String,
    val timestamp: Long
)
