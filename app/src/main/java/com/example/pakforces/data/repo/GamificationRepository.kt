package com.example.pakforces.data.repo

import android.content.Context
import com.example.pakforces.data.db.PakForcesDatabase
import com.example.pakforces.data.db.UserStatsEntity
import com.example.pakforces.data.db.XpHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Duolingo-style gamification — XP, hearts, streaks, achievements.
 */
class GamificationRepository(context: Context, db: PakForcesDatabase = PakForcesDatabase.get(context)) {

    private val statsDao = db.userStatsDao()
    private val xpDao = db.xpHistoryDao()

    val stats: Flow<UserStatsEntity?> = statsDao.stats()

    private val dayFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun ensureStats() {
        if (statsDao.get() == null) {
            statsDao.upsert(UserStatsEntity())
        }
    }

    suspend fun loseHeart() {
        ensureStats()
        val s = statsDao.get() ?: return
        if (s.currentHearts > 0) statsDao.setHearts(s.currentHearts - 1)
    }

    /** Refill 1 heart every 4 hours (called on app open). */
    suspend fun tickHeartRefill() {
        ensureStats()
        val s = statsDao.get() ?: return
        if (s.currentHearts >= s.maxHearts) {
            statsDao.upsert(s.copy(lastHeartRefillAt = System.currentTimeMillis()))
            return
        }
        val now = System.currentTimeMillis()
        val elapsed = now - s.lastHeartRefillAt
        val refillIntervalMs = 4 * 60 * 60 * 1000L  // 4 hours
        if (elapsed >= refillIntervalMs) {
            val heartsToAdd = (elapsed / refillIntervalMs).toInt().coerceAtMost(s.maxHearts - s.currentHearts)
            statsDao.upsert(s.copy(
                currentHearts = s.currentHearts + heartsToAdd,
                lastHeartRefillAt = if (s.currentHearts + heartsToAdd >= s.maxHearts) now else s.lastHeartRefillAt + heartsToAdd * refillIntervalMs
            ))
        }
    }

    suspend fun refillHearts() {
        ensureStats()
        val s = statsDao.get() ?: return
        statsDao.upsert(s.copy(currentHearts = s.maxHearts, lastHeartRefillAt = System.currentTimeMillis()))
    }

    /** Streak freeze — auto-consumed when streak would break. Earned every 7-day streak. */
    suspend fun addStreakFreeze() {
        ensureStats()
        val s = statsDao.get() ?: return
        if (s.streakFreezes < 2) statsDao.upsert(s.copy(streakFreezes = s.streakFreezes + 1))
    }

    suspend fun consumeStreakFreeze(): Boolean {
        ensureStats()
        val s = statsDao.get() ?: return false
        if (s.streakFreezes > 0) {
            statsDao.upsert(s.copy(streakFreezes = s.streakFreezes - 1))
            return true
        }
        return false
    }

    suspend fun addFocusMinutes(min: Int) {
        ensureStats()
        val s = statsDao.get() ?: return
        statsDao.upsert(s.copy(focusMinutes = s.focusMinutes + min))
    }

    suspend fun incrementVocabularyMastered() {
        ensureStats()
        val s = statsDao.get() ?: return
        statsDao.upsert(s.copy(vocabularyMastered = s.vocabularyMastered + 1))
    }

    /** Award XP for a test/practice session and update streak. */
    suspend fun awardXP(activity: String, baseXp: Int, questionsAnswered: Int, correct: Int) {
        ensureStats()
        val accuracyBonus = if (questionsAnswered > 0) (correct * 100 / questionsAnswered) / 5 else 0
        val totalXp = baseXp + accuracyBonus

        val today = dayFmt.format(Date())
        statsDao.addXpAndAnswers(totalXp, questionsAnswered, correct)
        statsDao.incrementTests()
        statsDao.upsert(statsDao.get()!!.copy())  // ensure row exists

        xpDao.insert(XpHistoryEntity(dayKey = today, xp = totalXp, activity = activity, timestamp = System.currentTimeMillis()))

        // Streak logic with freeze support
        val current = statsDao.get()!!
        when {
            current.lastActiveDay == today -> { /* same day, no change */ }
            isYesterday(current.lastActiveDay, today) -> {
                val newStreak = current.streakDays + 1
                statsDao.setStreak(newStreak, today)
                // Award a freeze every 7-day streak
                if (newStreak % 7 == 0) addStreakFreeze()
            }
            isTwoDaysAgo(current.lastActiveDay, today) -> {
                // Missed one day — try to consume a freeze
                if (consumeStreakFreeze()) {
                    val newStreak = current.streakDays + 1
                    statsDao.setStreak(newStreak, today)
                } else {
                    statsDao.setStreak(1, today)
                }
            }
            else -> {
                statsDao.setStreak(1, today)
            }
        }
    }

    private fun isTwoDaysAgo(prev: String, today: String): Boolean {
        if (prev.isBlank()) return false
        return try {
            val p = dayFmt.parse(prev) ?: return false
            val t = dayFmt.parse(today) ?: return false
            val diff = (t.time - p.time) / (24 * 60 * 60 * 1000)
            diff == 2L
        } catch (_: Exception) { false }
    }

    /** Returns true if achievement was newly unlocked. */
    suspend fun unlockAchievement(achievementId: String): Boolean {
        ensureStats()
        val s = statsDao.get() ?: return false
        val current = if (s.achievements.isBlank()) emptyList() else s.achievements.split(",")
        if (achievementId in current) return false
        statsDao.setAchievements((current + achievementId).joinToString(","))
        return true
    }

    private fun isYesterday(prev: String, today: String): Boolean {
        if (prev.isBlank()) return false
        return try {
            val p = dayFmt.parse(prev) ?: return false
            val t = dayFmt.parse(today) ?: return false
            val diff = (t.time - p.time) / (24 * 60 * 60 * 1000)
            diff == 1L
        } catch (_: Exception) { false }
    }
}

/** Achievement catalog. */
enum class Achievement(val id: String, val title: String, val desc: String, val icon: String) {
    FIRST_TEST("first_test", "Pehla Kadam", "Apna pehla test complete karein", "🎯"),
    STREAK_7("streak_7", "Saat Din Jari", "7-din ka streak banayein", "🔥"),
    STREAK_30("streak_30", "Mahine Ka Champion", "30-din ka streak banayein", "💎"),
    PERFECT_SCORE("perfect", "Perfect!", "Kisi test mein 100% score karein", "🏆"),
    CENTURY_100Q("century", "Sau Ka Score", "Total 100 questions solve karein", "💯"),
    BOOKMARK_10("bookmark_10", "Collector", "10 questions bookmark karein", "📚"),
    DAILY_7("daily_7", "Daily Devotee", "7 daily challenges complete karein", "⭐"),
    FORCE_MASTER("force_master", "All-Rounder", "Teeno forces ke tests complete karein", "🥇"),
    XP_1000("xp_1000", "XP Master", "1000 XP earn karein", "⚡"),
    EXAM_READY("exam_ready", "Exam Ready", "Simulation mode pass karein", "🎖️"),
    VOCAB_50("vocab_50", "Word Smith", "50 flashcards master karein", "📖"),
    POMODORO_10("pomodoro_10", "Focus Master", "10 pomodoro sessions complete karein", "⏱️"),
    MISSIONS_7("missions_7", "Mission Accomplished", "7 daily missions pooray karein", "✅"),
    RANK_CAPTAIN("rank_captain", "Officer Material", "Captain rank tak pahunchein", "🎖️"),
    FREEZE_OWNER("freeze_owner", "Prepared", "1 streak freeze earn karein", "🧊"),
}
