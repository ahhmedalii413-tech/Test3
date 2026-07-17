package com.example.pakforces.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun stats(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun get(): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stats: UserStatsEntity)

    @Query("UPDATE user_stats SET totalXP = totalXP + :xp, questionsAnswered = questionsAnswered + :q, correctAnswers = correctAnswers + :c WHERE id = 1")
    suspend fun addXpAndAnswers(xp: Int, q: Int, c: Int)

    @Query("UPDATE user_stats SET currentHearts = :h WHERE id = 1")
    suspend fun setHearts(h: Int)

    @Query("UPDATE user_stats SET streakDays = :s, lastActiveDay = :day WHERE id = 1")
    suspend fun setStreak(s: Int, day: String)

    @Query("UPDATE user_stats SET testsCompleted = testsCompleted + 1 WHERE id = 1")
    suspend fun incrementTests()

    @Query("UPDATE user_stats SET achievements = :a WHERE id = 1")
    suspend fun setAchievements(a: String)
}

@Dao
interface XpHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: XpHistoryEntity)

    @Query("SELECT * FROM xp_history WHERE dayKey = :day ORDER BY timestamp DESC")
    fun forDay(day: String): Flow<List<XpHistoryEntity>>

    @Query("SELECT dayKey, CAST(SUM(xp) AS INTEGER) as xp FROM xp_history GROUP BY dayKey ORDER BY dayKey DESC LIMIT 30")
    fun dailyXp(): Flow<List<DayXp>>

    data class DayXp(val dayKey: String, val xp: Int)
}
