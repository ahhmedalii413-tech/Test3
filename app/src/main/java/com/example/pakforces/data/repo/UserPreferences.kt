package com.example.pakforces.data.repo

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("pak_forces_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val ONBOARDED = booleanPreferencesKey("onboarded")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val FORCE = stringPreferencesKey("force")
        val DAILY_REMINDER = booleanPreferencesKey("daily_reminder")
        val TEST_LENGTH = stringPreferencesKey("test_length")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val COLOR_BLIND_MODE = booleanPreferencesKey("color_blind_mode")
        val LANGUAGE = stringPreferencesKey("language")  // "en" or "ur_roman"
        val DARK_MODE_SCHEDULE = booleanPreferencesKey("dark_mode_schedule")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    }

    val onboarded: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDED] ?: false }
    suspend fun setOnboarded(v: Boolean) = context.dataStore.edit { it[ONBOARDED] = v }

    val darkTheme: Flow<Boolean?> = context.dataStore.data.map { it[DARK_THEME] }
    suspend fun setDarkTheme(v: Boolean) = context.dataStore.edit { it[DARK_THEME] = v }

    val force: Flow<String> = context.dataStore.data.map { it[FORCE] ?: "army" }
    suspend fun setForce(v: String) = context.dataStore.edit { it[FORCE] = v }

    val dailyReminder: Flow<Boolean> = context.dataStore.data.map { it[DAILY_REMINDER] ?: true }
    suspend fun setDailyReminder(v: Boolean) = context.dataStore.edit { it[DAILY_REMINDER] = v }

    val testLength: Flow<String> = context.dataStore.data.map { it[TEST_LENGTH] ?: "MEDIUM" }
    suspend fun setTestLength(v: String) = context.dataStore.edit { it[TEST_LENGTH] = v }

    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[SOUND_ENABLED] ?: true }
    suspend fun setSoundEnabled(v: Boolean) = context.dataStore.edit { it[SOUND_ENABLED] = v }

    val hapticEnabled: Flow<Boolean> = context.dataStore.data.map { it[HAPTIC_ENABLED] ?: true }
    suspend fun setHapticEnabled(v: Boolean) = context.dataStore.edit { it[HAPTIC_ENABLED] = v }

    val colorBlindMode: Flow<Boolean> = context.dataStore.data.map { it[COLOR_BLIND_MODE] ?: false }
    suspend fun setColorBlindMode(v: Boolean) = context.dataStore.edit { it[COLOR_BLIND_MODE] = v }

    val language: Flow<String> = context.dataStore.data.map { it[LANGUAGE] ?: "en" }
    suspend fun setLanguage(v: String) = context.dataStore.edit { it[LANGUAGE] = v }

    val darkModeSchedule: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE_SCHEDULE] ?: false }
    suspend fun setDarkModeSchedule(v: Boolean) = context.dataStore.edit { it[DARK_MODE_SCHEDULE] = v }

    val reminderHour: Flow<Int> = context.dataStore.data.map { it[REMINDER_HOUR] ?: 18 }
    suspend fun setReminderHour(v: Int) = context.dataStore.edit { it[REMINDER_HOUR] = v }
}

enum class TestLength(val count: Int, val display: String, val timeSec: Int) {
    SHORT(10, "Quick (10 Q)", 600),
    MEDIUM(20, "Standard (20 Q)", 1200),
    LONG(40, "Marathon (40 Q)", 2400);

    companion object {
        fun fromKey(k: String?) = entries.firstOrNull { it.name == k } ?: MEDIUM
    }
}
