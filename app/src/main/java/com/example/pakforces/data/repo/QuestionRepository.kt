package com.example.pakforces.data.repo

import android.content.Context
import com.example.pakforces.data.db.BookmarkEntity
import com.example.pakforces.data.db.DailyChallengeEntity
import com.example.pakforces.data.db.PakForcesDatabase
import com.example.pakforces.data.db.QuestionEntity
import com.example.pakforces.data.db.TestResultEntity
import com.example.pakforces.data.db.WrongAnswerEntity
import com.example.pakforces.data.model.Category
import com.example.pakforces.data.model.Force
import com.example.pakforces.data.model.QuestionBankFile
import com.example.pakforces.data.model.QuestionJson
import com.example.pakforces.data.model.TestMode
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for everything question/test related.
 * Constructor-injected — no DI framework needed.
 */
class QuestionRepository(
    private val context: Context,
    private val db: PakForcesDatabase = PakForcesDatabase.get(context),
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(List::class.java)

    private val bankFiles = listOf(
        QuestionBankFile(Force.ARMY, "questions/army_verbal.json"),
        QuestionBankFile(Force.ARMY, "questions/army_nonverbal.json"),
        QuestionBankFile(Force.ARMY, "questions/army_english.json"),
        QuestionBankFile(Force.ARMY, "questions/army_math.json"),
        QuestionBankFile(Force.ARMY, "questions/army_pakistan.json"),
        QuestionBankFile(Force.ARMY, "questions/army_islamic.json"),
        QuestionBankFile(Force.ARMY, "questions/army_gk.json"),
        QuestionBankFile(Force.ARMY, "questions/army_science.json"),
        QuestionBankFile(Force.AIR_FORCE, "questions/paf_verbal.json"),
        QuestionBankFile(Force.AIR_FORCE, "questions/paf_english.json"),
        QuestionBankFile(Force.AIR_FORCE, "questions/paf_science.json"),
        QuestionBankFile(Force.AIR_FORCE, "questions/paf_combined.json"),
        QuestionBankFile(Force.NAVY, "questions/navy_verbal.json"),
        QuestionBankFile(Force.NAVY, "questions/navy_combined.json"),
    )

    /** Seed the database from JSON assets on first install. Returns # of questions loaded. */
    suspend fun seedIfNeeded(): Int {
        val existing = db.questionDao().count()
        if (existing > 0) return existing
        val all = mutableListOf<QuestionEntity>()
        for (file in bankFiles) {
            val json = try { context.assets.open(file.assetPath).bufferedReader().use { it.readText() } }
            catch (_: Exception) { continue }
            val parsed = try {
                @Suppress("UNCHECKED_CAST")
                adapter.fromJson(json) as? List<Map<String, Any?>> ?: emptyList()
            } catch (_: Exception) { emptyList() }

            for (row in parsed) {
                val id = row["id"] as? String ?: continue
                val c = (row["c"] as? String) ?: "verbal"
                val s = (row["s"] as? String) ?: ""
                val q = (row["q"] as? String) ?: continue
                @Suppress("UNCHECKED_CAST")
                val o = (row["o"] as? List<String>) ?: emptyList()
                val a = (row["a"] as? Double)?.toInt() ?: (row["a"] as? Int) ?: continue
                val e = (row["e"] as? String) ?: ""

                // V2 support — non-verbal questions can carry figure options / figures in stem
                @Suppress("UNCHECKED_CAST")
                val foRaw = row["fo"] as? List<Map<String, Any?>>
                @Suppress("UNCHECKED_CAST")
                val figsRaw = row["figs"] as? List<Map<String, Any?>>

                // Serialize figure data as compact JSON string and store in option fields.
                // For non-verbal: we store figure JSON in option0..option3 (string form).
                val opt0: String
                val opt1: String
                val opt2: String
                val opt3: String
                val isFig: Boolean
                if (o.size == 4) {
                    opt0 = o[0]; opt1 = o[1]; opt2 = o[2]; opt3 = o[3]; isFig = false
                } else if (foRaw != null && foRaw.size == 4) {
                    opt0 = encodeFigure(foRaw[0])
                    opt1 = encodeFigure(foRaw[1])
                    opt2 = encodeFigure(foRaw[2])
                    opt3 = encodeFigure(foRaw[3])
                    isFig = true
                } else {
                    opt0 = ""; opt1 = ""; opt2 = ""; opt3 = ""; isFig = false
                }

                val figsEncoded = if (figsRaw != null && figsRaw.isNotEmpty()) {
                    figsRaw.joinToString("|") { encodeFigure(it) }
                } else ""

                all += QuestionEntity(
                    id = id, force = file.force.key, category = c, subCategory = s,
                    question = q,
                    option0 = opt0, option1 = opt1, option2 = opt2, option3 = opt3,
                    correctIndex = a, explanation = e,
                    isFigure = isFig,
                    stemFigures = figsEncoded,
                )
            }
        }
        if (all.isNotEmpty()) db.questionDao().insertAll(all)
        return all.size
    }

    private fun encodeFigure(map: Map<String, Any?>): String {
        // Re-serialize a figure (map of shapes) back to a JSON string
        return try {
            val shapes = (map["shapes"] as? List<Map<String, Any?>>)?.map { s ->
                val parts = mutableListOf<String>()
                s["type"]?.let { parts += "\"type\":\"$it\"" }
                s["fill"]?.let { parts += "\"fill\":\"$it\"" }
                (s["rotation"] as? Number)?.let { parts += "\"rotation\":${it.toFloat()}" }
                (s["x"] as? Number)?.let { parts += "\"x\":${it.toFloat()}" }
                (s["y"] as? Number)?.let { parts += "\"y\":${it.toFloat()}" }
                (s["size"] as? Number)?.let { parts += "\"size\":${it.toFloat()}" }
                (s["count"] as? Number)?.let { parts += "\"count\":${it.toInt()}" }
                s["color"]?.let { parts += "\"color\":\"$it\"" }
                "{" + parts.joinToString(",") + "}"
            } ?: emptyList()
            "{\"shapes\":[" + shapes.joinToString(",") + "]}"
        } catch (_: Exception) { "" }
    }

    // ───────── Figure decoding (for non-verbal UI rendering) ─────────
    private val figAdapter = moshi.adapter(com.example.pakforces.data.model.Figure::class.java)

    fun decodeFigure(json: String): com.example.pakforces.data.model.Figure {
        if (json.isBlank()) return com.example.pakforces.data.model.Figure()
        return try { figAdapter.fromJson(json) ?: com.example.pakforces.data.model.Figure() }
        catch (_: Exception) { com.example.pakforces.data.model.Figure() }
    }

    fun decodeStemFigures(stem: String): List<com.example.pakforces.data.model.Figure> {
        if (stem.isBlank()) return emptyList()
        return stem.split("|").map { decodeFigure(it) }
    }

    // ───────── Questions ─────────
    suspend fun countForForce(force: Force) = db.questionDao().countForForce(force.key)
    suspend fun countForCategory(force: Force, category: Category) =
        db.questionDao().countForCategory(force.key, category.key)

    suspend fun randomQuestions(force: Force, category: Category?, limit: Int): List<QuestionEntity> =
        if (category == null) db.questionDao().randomFor(force.key, "", limit)
        else db.questionDao().randomByCategory(force.key, category.key, limit)

    fun categoriesForForce(force: Force): Flow<List<String>> =
        db.questionDao().categoriesForForce(force.key)

    // ───────── Test results ─────────
    suspend fun saveTestResult(
        force: Force, category: Category, mode: TestMode,
        total: Int, correct: Int, wrong: Int, skipped: Int,
        scorePercent: Int, timeTakenSec: Int
    ) = db.testResultDao().insert(
        TestResultEntity(
            force = force.key, category = category.key, mode = mode.name,
            totalQuestions = total, correct = correct, wrong = wrong, skipped = skipped,
            scorePercent = scorePercent, timeTakenSec = timeTakenSec,
            completedAt = System.currentTimeMillis()
        )
    )

    fun allResults(): Flow<List<TestResultEntity>> = db.testResultDao().allResults()
    fun attemptsForForce(force: Force): Flow<Int> = db.testResultDao().attemptsForForce(force.key)
    fun avgScoreForForce(force: Force): Flow<Float?> = db.testResultDao().avgScoreForForce(force.key)
    fun bestScoreForForce(force: Force): Flow<Int?> = db.testResultDao().bestScoreForForce(force.key)

    // ───────── Bookmarks ─────────
    suspend fun toggleBookmark(questionId: String) {
        if (db.bookmarkDao().isBookmarked(questionId))
            db.bookmarkDao().remove(questionId)
        else
            db.bookmarkDao().add(BookmarkEntity(questionId, System.currentTimeMillis()))
    }

    suspend fun isBookmarked(questionId: String) = db.bookmarkDao().isBookmarked(questionId)
    fun bookmarkedQuestions(): Flow<List<QuestionEntity>> = db.bookmarkDao().bookmarkedQuestions()

    // ───────── Wrong answers (for Revision) ─────────
    suspend fun recordWrong(questionId: String, force: Force, category: Category) {
        val existing = db.wrongAnswerDao().wrongQuestions("", 1000).firstOrNull { it.id == questionId }
        if (existing == null) {
            db.wrongAnswerDao().upsert(
                WrongAnswerEntity(questionId, force.key, category.key, 1, System.currentTimeMillis())
            )
        } else {
            db.wrongAnswerDao().increment(questionId, System.currentTimeMillis())
        }
    }

    suspend fun clearWrong(questionId: String) = db.wrongAnswerDao().clearForQuestion(questionId)
    fun totalWrongCount(): Flow<Int> = db.wrongAnswerDao().totalWrongCount()
    suspend fun wrongQuestions(force: Force?, limit: Int): List<QuestionEntity> =
        db.wrongAnswerDao().wrongQuestions(force?.key ?: "", limit)

    // ───────── Daily challenge ─────────
    suspend fun getDaily(dayKey: String) = db.dailyChallengeDao().forDay(dayKey)
    suspend fun saveDaily(dayKey: String, force: Force, correct: Int, total: Int) =
        db.dailyChallengeDao().upsert(
            DailyChallengeEntity(dayKey, force.key, correct, total, System.currentTimeMillis())
        )
    fun dailyHistory(): Flow<List<DailyChallengeEntity>> = db.dailyChallengeDao().history()
    fun streakCount(): Flow<Int> = db.dailyChallengeDao().streakCount()
}
