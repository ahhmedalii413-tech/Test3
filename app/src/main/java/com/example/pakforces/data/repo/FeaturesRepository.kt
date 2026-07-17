package com.example.pakforces.data.repo

import android.content.Context
import com.example.pakforces.data.content.FlashcardData
import com.example.pakforces.data.content.MissionTemplates
import com.example.pakforces.data.db.BookmarkFolderEntity
import com.example.pakforces.data.db.ExamDateEntity
import com.example.pakforces.data.db.FlashcardEntity
import com.example.pakforces.data.db.MissionEntity
import com.example.pakforces.data.db.NoteEntity
import com.example.pakforces.data.db.PakForcesDatabase
import com.example.pakforces.data.db.PomodoroSessionEntity
import com.example.pakforces.data.db.QuestionEntity
import com.example.pakforces.data.db.QuestionTimingEntity
import com.example.pakforces.data.db.SrsScheduleEntity
import com.example.pakforces.data.model.Force
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository for the v3 new feature set: notes, folders, missions, flashcards,
 * exam date, pomodoro, SRS, question timing.
 */
class FeaturesRepository(
    context: Context,
    db: PakForcesDatabase = PakForcesDatabase.get(context),
) {
    private val noteDao = db.noteDao()
    private val folderDao = db.bookmarkFolderDao()
    private val missionDao = db.missionDao()
    private val flashcardDao = db.flashcardDao()
    private val examDateDao = db.examDateDao()
    private val pomodoroDao = db.pomodoroDao()
    private val srsDao = db.srsDao()
    private val timingDao = db.questionTimingDao()

    private val dayFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    fun todayKey(): String = dayFmt.format(Date())

    // ───── Notes ─────
    suspend fun saveNote(qid: String, text: String) {
        noteDao.upsert(NoteEntity(qid, text, System.currentTimeMillis()))
    }
    suspend fun deleteNote(qid: String) = noteDao.delete(qid)
    suspend fun getNote(qid: String): String? = noteDao.get(qid)?.note
    fun allNotes(): Flow<List<NoteEntity>> = noteDao.all()

    // ───── Bookmark folders ─────
    fun folders(): Flow<List<BookmarkFolderEntity>> = folderDao.all()
    suspend fun createFolder(name: String, colorHex: String): Long =
        folderDao.insert(BookmarkFolderEntity(name = name, colorHex = colorHex, createdAt = System.currentTimeMillis()))
    suspend fun deleteFolder(id: Long) = folderDao.delete(id)
    suspend fun linkQuestion(qid: String, folderId: Long) =
        folderDao.link(com.example.pakforces.data.db.BookmarkFolderLinkEntity(qid, folderId, System.currentTimeMillis()))
    suspend fun unlinkQuestion(qid: String, folderId: Long) = folderDao.unlink(qid, folderId)
    fun questionsInFolder(folderId: Long): Flow<List<QuestionEntity>> = folderDao.questionsInFolder(folderId)

    // ───── Missions ─────
    suspend fun todayMission(): MissionEntity {
        val day = todayKey()
        return missionDao.forDay(day) ?: run {
            val goals = MissionTemplates.pickForDay(day)
            val arr = JSONArray()
            goals.forEach { g ->
                arr.put(JSONObject().apply {
                    put("id", g.id); put("title", g.title); put("xp", g.xp)
                    put("target", g.target); put("type", g.type); put("progress", 0); put("done", false)
                })
            }
            MissionEntity(dayKey = day, xpEarned = 0, goalsJson = arr.toString(), completedAt = null).also {
                missionDao.upsert(it)
            }
        }
    }
    suspend fun recordMissionProgress(goalType: String, increment: Int) {
        val mission = todayMission()
        val arr = JSONArray(mission.goalsJson)
        var xpDelta = 0
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (obj.getString("type") == goalType && !obj.getBoolean("done")) {
                val newProg = obj.getInt("progress") + increment
                val target = obj.getInt("target")
                if (newProg >= target) {
                    obj.put("progress", target)
                    obj.put("done", true)
                    xpDelta += obj.getInt("xp")
                } else {
                    obj.put("progress", newProg)
                }
            }
        }
        missionDao.upsert(mission.copy(goalsJson = arr.toString(), xpEarned = mission.xpEarned + xpDelta))
    }
    fun missionHistory(): Flow<List<MissionEntity>> = missionDao.history()

    // ───── Flashcards ─────
    suspend fun seedFlashcardsIfNeeded() {
        if (flashcardDao.count() == 0) {
            flashcardDao.upsertAll(FlashcardData.all)
        }
    }
    fun allFlashcards(): Flow<List<FlashcardEntity>> = flashcardDao.all()
    fun flashcardsByCategory(cat: String): Flow<List<FlashcardEntity>> = flashcardDao.byCategory(cat)
    suspend fun dueFlashcards(now: Long): List<FlashcardEntity> = flashcardDao.due(now)

    /** SRS update for a flashcard review. Quality: 0=forgot, 3=hard, 5=easy. */
    suspend fun reviewFlashcard(cardId: String, quality: Int) {
        val all = flashcardDao.due(System.currentTimeMillis())
        val card = all.firstOrNull { it.id == cardId } ?: return
        // SM-2 algorithm. Use Float literals throughout to avoid Double/Float mismatch.
        val q = quality.toFloat()
        val delta = 0.1f - (5f - q) * (0.08f + (5f - q) * 0.02f)
        val newEase = (card.ease + delta).coerceIn(1.3f, 2.6f)
        val newInterval = when (quality) {
            0 -> 1
            3 -> (card.intervalDays * newEase).toInt().coerceAtLeast(1)
            else -> (card.intervalDays * newEase * 1.3f).toInt().coerceAtLeast(2)
        }
        val newDue = System.currentTimeMillis() + newInterval * 86_400_000L
        flashcardDao.upsertAll(listOf(card.copy(
            intervalDays = newInterval, ease = newEase, dueAt = newDue, reviewCount = card.reviewCount + 1
        )))
    }

    // ───── Exam date ─────
    fun examDate(): Flow<ExamDateEntity?> = examDateDao.get()
    suspend fun setExamDate(force: Force, date: Long) {
        examDateDao.upsert(ExamDateEntity(force = force.key, examDate = date, createdAt = System.currentTimeMillis()))
    }
    suspend fun clearExamDate() {
        examDateDao.upsert(ExamDateEntity(force = "", examDate = 0, createdAt = System.currentTimeMillis()))
    }

    // ───── Pomodoro ─────
    fun pomodoroForDay(day: String): Flow<List<PomodoroSessionEntity>> = pomodoroDao.forDay(day)
    fun pomodoroDaily(): Flow<List<com.example.pakforces.data.db.PomodoroDao.DayMinutes>> = pomodoroDao.daily()
    suspend fun logPomodoro(durationMin: Int) {
        pomodoroDao.insert(PomodoroSessionEntity(
            dayKey = todayKey(), durationMin = durationMin,
            startedAt = System.currentTimeMillis() - durationMin * 60_000L,
            completedAt = System.currentTimeMillis(),
        ))
    }

    // ───── SRS (wrong-answer queue) ─────
    suspend fun scheduleSrs(question: QuestionEntity) {
        val now = System.currentTimeMillis()
        srsDao.upsert(SrsScheduleEntity(
            questionId = question.id, force = question.force, category = question.category,
            intervalDays = 1, ease = 2.5f, dueAt = now + 86_400_000L,
            lastReviewedAt = null, reviewCount = 0
        ))
    }
    suspend fun reviewSrs(qid: String, force: String, category: String, quality: Int) {
        val now = System.currentTimeMillis()
        val all = srsDao.due(now, 1000)
        val existing = all.firstOrNull { it.questionId == qid }
        val base = existing ?: SrsScheduleEntity(qid, force, category, 1, 2.5f, now, null, 0)
        // Float literals to avoid Double/Float mismatch
        val q = quality.toFloat()
        val delta = 0.1f - (5f - q) * (0.08f + (5f - q) * 0.02f)
        val newEase = (base.ease + delta).coerceIn(1.3f, 2.6f)
        val newInterval = when (quality) {
            0 -> 1
            3 -> (base.intervalDays * newEase).toInt().coerceAtLeast(1)
            else -> (base.intervalDays * newEase * 1.3f).toInt().coerceAtLeast(2)
        }
        srsDao.upsert(base.copy(
            intervalDays = newInterval, ease = newEase,
            dueAt = now + newInterval * 86_400_000L,
            lastReviewedAt = now, reviewCount = base.reviewCount + 1
        ))
    }
    fun srsDueCount(now: Long): Flow<Int> = srsDao.dueCount(now)
    fun srsTotal(): Flow<Int> = srsDao.total()
    suspend fun srsDueQuestions(limit: Int): List<QuestionEntity> = srsDao.dueQuestions(System.currentTimeMillis(), limit)

    // ───── Question timing analytics ─────
    suspend fun logTiming(qid: String, force: String, category: String, timeSec: Int, correct: Boolean) {
        timingDao.insert(QuestionTimingEntity(
            questionId = qid, force = force, category = category, timeSec = timeSec,
            correct = correct, answeredAt = System.currentTimeMillis()
        ))
    }
    fun timingByCategory(): Flow<List<com.example.pakforces.data.db.QuestionTimingDao.CategoryTiming>> =
        timingDao.averagesByCategory()
    fun recentTiming(limit: Int = 200): Flow<List<QuestionTimingEntity>> = timingDao.recent(limit)
}
