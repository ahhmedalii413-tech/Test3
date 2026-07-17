package com.example.pakforces.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM questions WHERE force = :force")
    suspend fun countForForce(force: String): Int

    @Query("SELECT COUNT(*) FROM questions WHERE force = :force AND category = :category")
    suspend fun countForCategory(force: String, category: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("""
        SELECT * FROM questions
        WHERE force = :force
        AND (:category = '' OR category = :category)
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun randomFor(force: String, category: String, limit: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE force = :force AND category = :category ORDER BY RANDOM() LIMIT :limit")
    suspend fun randomByCategory(force: String, category: String, limit: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun byId(id: String): QuestionEntity?

    @Query("SELECT * FROM questions WHERE force = :force AND category = :category LIMIT :limit OFFSET :offset")
    suspend fun page(force: String, category: String, limit: Int, offset: Int): List<QuestionEntity>

    /** Categories that actually have questions for this force. */
    @Query("SELECT DISTINCT category FROM questions WHERE force = :force")
    fun categoriesForForce(force: String): Flow<List<String>>

    @Query("SELECT DISTINCT subCategory FROM questions WHERE force = :force AND category = :category")
    fun subCategories(force: String, category: String): Flow<List<String>>
}

@Dao
interface TestResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: TestResultEntity): Long

    @Query("SELECT * FROM test_results ORDER BY completedAt DESC")
    fun allResults(): Flow<List<TestResultEntity>>

    @Query("SELECT * FROM test_results WHERE force = :force ORDER BY completedAt DESC LIMIT 1")
    suspend fun lastForForce(force: String): TestResultEntity?

    @Query("SELECT COUNT(*) FROM test_results WHERE force = :force")
    fun attemptsForForce(force: String): Flow<Int>

    @Query("SELECT AVG(scorePercent) FROM test_results WHERE force = :force")
    fun avgScoreForForce(force: String): Flow<Float?>

    @Query("SELECT MAX(scorePercent) FROM test_results WHERE force = :force")
    fun bestScoreForForce(force: String): Flow<Int?>
}

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE questionId = :questionId")
    suspend fun remove(questionId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE questionId = :questionId)")
    suspend fun isBookmarked(questionId: String): Boolean

    @Query("""
        SELECT q.* FROM questions q
        INNER JOIN bookmarks b ON q.id = b.questionId
        ORDER BY b.addedAt DESC
    """)
    fun bookmarkedQuestions(): Flow<List<QuestionEntity>>
}

@Dao
interface WrongAnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WrongAnswerEntity)

    @Query("""
        UPDATE wrong_answers
        SET timesWrong = timesWrong + 1, lastWrongAt = :now
        WHERE questionId = :questionId
    """)
    suspend fun increment(questionId: String, now: Long)

    @Query("SELECT * FROM wrong_answers WHERE force = :force ORDER BY lastWrongAt DESC")
    fun wrongForForce(force: String): Flow<List<WrongAnswerEntity>>

    @Query("SELECT * FROM wrong_answers ORDER BY lastWrongAt DESC")
    fun allWrong(): Flow<List<WrongAnswerEntity>>

    @Query("""
        SELECT q.* FROM questions q
        INNER JOIN wrong_answers w ON q.id = w.questionId
        WHERE (:force = '' OR q.force = :force)
        ORDER BY w.lastWrongAt DESC
        LIMIT :limit
    """)
    suspend fun wrongQuestions(force: String, limit: Int): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM wrong_answers")
    fun totalWrongCount(): Flow<Int>

    @Query("DELETE FROM wrong_answers WHERE questionId = :questionId")
    suspend fun clearForQuestion(questionId: String)
}

@Dao
interface DailyChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailyChallengeEntity)

    @Query("SELECT * FROM daily_challenge WHERE dayKey = :dayKey")
    suspend fun forDay(dayKey: String): DailyChallengeEntity?

    @Query("SELECT * FROM daily_challenge ORDER BY dayKey DESC")
    fun history(): Flow<List<DailyChallengeEntity>>

    @Query("SELECT COUNT(*) FROM daily_challenge")
    fun streakCount(): Flow<Int>
}
