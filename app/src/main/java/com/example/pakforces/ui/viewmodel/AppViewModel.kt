package com.example.pakforces.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pakforces.data.model.Force
import com.example.pakforces.data.repo.Achievement
import com.example.pakforces.data.repo.FeaturesRepository
import com.example.pakforces.data.repo.GamificationRepository
import com.example.pakforces.data.repo.QuestionRepository
import com.example.pakforces.data.repo.TestLength
import com.example.pakforces.data.repo.UserPreferences
import com.example.pakforces.ui.notifications.DailyReminderWorker
import com.example.pakforces.ui.utilities.FeedbackManager
import com.example.pakforces.ui.utilities.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AppState(
    val onboarded: Boolean = false,
    val force: Force = Force.ARMY,
    val darkTheme: Boolean? = null,
    val testLength: TestLength = TestLength.MEDIUM,
    val isLoading: Boolean = true,
    val totalQuestions: Int = 0,
    val dailyReminder: Boolean = true,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val colorBlindMode: Boolean = false,
    val language: String = "en",
    val darkModeSchedule: Boolean = false,
    val reminderHour: Int = 18,
    // Gamification
    val xp: Int = 0,
    val hearts: Int = 5,
    val maxHearts: Int = 5,
    val streak: Int = 0,
    val streakFreezes: Int = 0,
    val questionsAnswered: Int = 0,
    val testsCompleted: Int = 0,
    val rank: com.example.pakforces.data.model.OfficerRank = com.example.pakforces.data.model.Ranks.current(0, Force.ARMY),
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = QuestionRepository(application)
    private val prefs = UserPreferences(application)
    private val gameRepo = GamificationRepository(application)
    private val featuresRepo = FeaturesRepository(application)

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    private val _forceTheme = MutableStateFlow(Force.ARMY)
    val forceTheme: StateFlow<Force> = _forceTheme

    val gameStats = gameRepo.stats
    val features get() = featuresRepo

    val tts = TtsManager(application)
    val feedback = FeedbackManager(application).also {
        viewModelScope.launch {
            prefs.soundEnabled.collect { v -> it.soundEnabled = v }
        }
        viewModelScope.launch {
            prefs.hapticEnabled.collect { v -> it.hapticEnabled = v }
        }
    }

    init {
        viewModelScope.launch {
            val total = repo.seedIfNeeded()
            featuresRepo.seedFlashcardsIfNeeded()
            gameRepo.tickHeartRefill()
            DailyReminderWorker.schedule(application)

            // Combine 11 prefs + gameStats into a single AppState. Since combine() has typed
            // overloads only up to 5 sources, we nest: (5 prefs) → PrefBundle1,
            // (other 6 prefs) split into (3 + 3) → PrefBundle2, then combine(B1, B2, gameStats).
            val prefsFlow = kotlinx.coroutines.flow.combine(
                prefs.onboarded, prefs.force, prefs.darkTheme, prefs.testLength, prefs.dailyReminder
            ) { ob, f, dt, tl, dailyRem ->
                PrefBundle(ob, f, dt, tl, dailyRem)
            }
            val prefs2a = kotlinx.coroutines.flow.combine(
                prefs.soundEnabled, prefs.hapticEnabled, prefs.colorBlindMode
            ) { s, h, cb -> Triple(s, h, cb) }
            val prefs2b = kotlinx.coroutines.flow.combine(
                prefs.language, prefs.darkModeSchedule, prefs.reminderHour
            ) { lang, ds, rh -> Triple(lang, ds, rh) }
            val prefsFlow2 = kotlinx.coroutines.flow.combine(prefs2a, prefs2b) { a, b ->
                PrefBundle2(a.first, a.second, a.third, b.first, b.second, b.third)
            }
            kotlinx.coroutines.flow.combine(prefsFlow, prefsFlow2, gameStats) { p1, p2, stats ->
                val force = Force.fromKey(p1.f)
                AppState(
                    onboarded = p1.ob, force = force,
                    darkTheme = if (p2.ds) null else p1.dt,
                    testLength = TestLength.fromKey(p1.tl),
                    isLoading = false, totalQuestions = total,
                    dailyReminder = p1.dailyRem,
                    soundEnabled = p2.s, hapticEnabled = p2.h,
                    colorBlindMode = p2.cb, language = p2.lang,
                    darkModeSchedule = p2.ds, reminderHour = p2.rh,
                    xp = stats?.totalXP ?: 0,
                    hearts = stats?.currentHearts ?: 5,
                    maxHearts = stats?.maxHearts ?: 5,
                    streak = stats?.streakDays ?: 0,
                    streakFreezes = stats?.streakFreezes ?: 0,
                    questionsAnswered = stats?.questionsAnswered ?: 0,
                    testsCompleted = stats?.testsCompleted ?: 0,
                    rank = com.example.pakforces.data.model.Ranks.current(stats?.totalXP ?: 0, force),
                )
            }.collect { st ->
                _state.value = st
                _forceTheme.value = st.force
            }
        }
    }

    private data class PrefBundle(
        val ob: Boolean, val f: String, val dt: Boolean?,
        val tl: String, val dailyRem: Boolean,
    )
    private data class PrefBundle2(
        val s: Boolean, val h: Boolean, val cb: Boolean,
        val lang: String, val ds: Boolean, val rh: Int,
    )

    // ── Onboarding / force ──
    fun completeOnboarding() = viewModelScope.launch { prefs.setOnboarded(true) }
    fun setForce(force: Force) = viewModelScope.launch { prefs.setForce(force.key) }

    // ── Theme / prefs ──
    fun setDarkTheme(v: Boolean?) = viewModelScope.launch {
        if (v == null) prefs.setDarkTheme(true) else prefs.setDarkTheme(!v)
    }
    fun setTestLength(tl: TestLength) = viewModelScope.launch { prefs.setTestLength(tl.name) }
    fun setSoundEnabled(v: Boolean) = viewModelScope.launch { prefs.setSoundEnabled(v) }
    fun setHapticEnabled(v: Boolean) = viewModelScope.launch { prefs.setHapticEnabled(v) }
    fun setColorBlindMode(v: Boolean) = viewModelScope.launch { prefs.setColorBlindMode(v) }
    fun setLanguage(v: String) = viewModelScope.launch { prefs.setLanguage(v) }
    fun setDarkModeSchedule(v: Boolean) = viewModelScope.launch { prefs.setDarkModeSchedule(v) }
    fun setReminderHour(h: Int) = viewModelScope.launch {
        prefs.setReminderHour(h)
        DailyReminderWorker.schedule(getApplication(), h)
    }

    // ── Bookmarks ──
    fun toggleBookmark(questionId: String) = viewModelScope.launch {
        repository.toggleBookmark(questionId)
    }

    // ── Test result + gamification ──
    fun saveTestResult(
        force: Force, category: com.example.pakforces.data.model.Category,
        mode: com.example.pakforces.data.model.TestMode,
        total: Int, correct: Int, wrong: Int, skipped: Int,
        scorePercent: Int, timeTakenSec: Int,
    ) = viewModelScope.launch {
        repository.saveTestResult(force, category, mode, total, correct, wrong, skipped, scorePercent, timeTakenSec)
        val baseXp = if (mode == com.example.pakforces.data.model.TestMode.TIMED) 30 else 20
        gameRepo.awardXP("${category.display} ${mode.display}", baseXp, total, correct)
        if (scorePercent == 100) gameRepo.unlockAchievement(Achievement.PERFECT_SCORE.id)
        gameRepo.unlockAchievement(Achievement.FIRST_TEST.id)
        val st = state.value
        if (st.questionsAnswered >= 100) gameRepo.unlockAchievement(Achievement.CENTURY_100Q.id)
        if (st.xp >= 1000) gameRepo.unlockAchievement(Achievement.XP_1000.id)
        if (st.streak >= 7) gameRepo.unlockAchievement(Achievement.STREAK_7.id)
        if (st.streak >= 30) gameRepo.unlockAchievement(Achievement.STREAK_30.id)
        if (st.streakFreezes > 0) gameRepo.unlockAchievement(Achievement.FREEZE_OWNER.id)
        if (st.xp >= 3000) gameRepo.unlockAchievement(Achievement.RANK_CAPTAIN.id)
        // Mission progress
        features.recordMissionProgress("test_count", 1)
        features.recordMissionProgress("questions", total)
        features.recordMissionProgress("correct", correct)
        features.recordMissionProgress("category_${category.key}", total)
        if (mode == com.example.pakforces.data.model.TestMode.TIMED) features.recordMissionProgress("simulation", 1)
    }

    fun setDailyReminder(enabled: Boolean) = viewModelScope.launch {
        prefs.setDailyReminder(enabled)
        if (enabled) DailyReminderWorker.schedule(getApplication()) else DailyReminderWorker.cancel(getApplication())
    }

    // ── Gamification shortcuts ──
    fun loseHeart() = viewModelScope.launch { gameRepo.loseHeart() }
    fun refillHearts() = viewModelScope.launch { gameRepo.refillHearts() }

    override fun onCleared() {
        tts.shutdown()
        feedback.release()
        super.onCleared()
    }

    val repository get() = repo
    val gamification get() = gameRepo
}

class AppViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) return AppViewModel(app) as T
        throw IllegalArgumentException("Unknown ViewModel")
    }
    companion object {
        var Factory: AppViewModelFactory? = null
    }
}
