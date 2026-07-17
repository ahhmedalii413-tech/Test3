package com.example.pakforces.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity)

    @Query("DELETE FROM notes WHERE questionId = :qid")
    suspend fun delete(qid: String)

    @Query("SELECT * FROM notes WHERE questionId = :qid")
    suspend fun get(qid: String): NoteEntity?

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun all(): Flow<List<NoteEntity>>
}

@Dao
interface BookmarkFolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: BookmarkFolderEntity): Long

    @Query("SELECT * FROM bookmark_folders ORDER BY createdAt DESC")
    fun all(): Flow<List<BookmarkFolderEntity>>

    @Query("DELETE FROM bookmark_folders WHERE id = :id")
    suspend fun delete(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun link(link: BookmarkFolderLinkEntity)

    @Query("DELETE FROM bookmark_folder_links WHERE questionId = :qid AND folderId = :fid")
    suspend fun unlink(qid: String, fid: Long)

    @Query("""
        SELECT q.* FROM questions q
        INNER JOIN bookmark_folder_links l ON q.id = l.questionId
        WHERE l.folderId = :folderId
        ORDER BY l.addedAt DESC
    """)
    fun questionsInFolder(folderId: Long): Flow<List<QuestionEntity>>
}

@Dao
interface MissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(mission: MissionEntity)

    @Query("SELECT * FROM missions WHERE dayKey = :day")
    suspend fun forDay(day: String): MissionEntity?

    @Query("SELECT * FROM missions ORDER BY dayKey DESC LIMIT 30")
    fun history(): Flow<List<MissionEntity>>
}

@Dao
interface FlashcardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(cards: List<FlashcardEntity>)

    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun count(): Int

    @Query("SELECT * FROM flashcards WHERE dueAt <= :now ORDER BY dueAt ASC")
    suspend fun due(now: Long): List<FlashcardEntity>

    @Query("SELECT * FROM flashcards WHERE category = :cat ORDER BY dueAt ASC")
    fun byCategory(cat: String): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards ORDER BY id")
    fun all(): Flow<List<FlashcardEntity>>
}

@Dao
interface ExamDateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(e: ExamDateEntity)

    @Query("SELECT * FROM exam_date WHERE id = 1")
    fun get(): Flow<ExamDateEntity?>
}

@Dao
interface PomodoroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: PomodoroSessionEntity): Long

    @Query("SELECT * FROM pomodoro_sessions WHERE dayKey = :day")
    fun forDay(day: String): Flow<List<PomodoroSessionEntity>>

    @Query("SELECT dayKey, CAST(SUM(durationMin) AS INTEGER) as minutes FROM pomodoro_sessions GROUP BY dayKey ORDER BY dayKey DESC LIMIT 30")
    fun daily(): Flow<List<DayMinutes>>

    data class DayMinutes(val dayKey: String, val minutes: Int)
}

@Dao
interface SrsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: SrsScheduleEntity)

    @Query("SELECT * FROM srs_schedule WHERE dueAt <= :now ORDER BY dueAt ASC LIMIT :limit")
    suspend fun due(now: Long, limit: Int): List<SrsScheduleEntity>

    @Query("""
        SELECT q.* FROM questions q
        INNER JOIN srs_schedule s ON q.id = s.questionId
        WHERE s.dueAt <= :now
        ORDER BY s.dueAt ASC
        LIMIT :limit
    """)
    suspend fun dueQuestions(now: Long, limit: Int): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM srs_schedule WHERE dueAt <= :now")
    fun dueCount(now: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM srs_schedule")
    fun total(): Flow<Int>
}

@Dao
interface QuestionTimingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: QuestionTimingEntity)

    @Query("SELECT category, CAST(AVG(timeSec) AS INTEGER) as avgTime, COUNT(*) as cnt, CAST(SUM(CASE WHEN correct THEN 1 ELSE 0 END) AS INTEGER) as correctCount FROM question_timing GROUP BY category")
    fun averagesByCategory(): Flow<List<CategoryTiming>>

    @Query("SELECT * FROM question_timing ORDER BY answeredAt DESC LIMIT :limit")
    fun recent(limit: Int): Flow<List<QuestionTimingEntity>>

    data class CategoryTiming(
        val category: String,
        val avgTime: Int,
        val cnt: Int,
        val correctCount: Int
    )
}
