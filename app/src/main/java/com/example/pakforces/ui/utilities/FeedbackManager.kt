package com.example.pakforces.ui.utilities

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Sound + haptic feedback manager. Plays correct/wrong/achievement sounds.
 * Haptic feedback uses predefined vibration patterns.
 */
class FeedbackManager(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // Sound IDs loaded at init (using system sounds since we don't bundle audio assets)
    private var correctSound: Int = 0
    private var wrongSound: Int = 0
    private var achievementSound: Int = 0
    private var clickSound: Int = 0

    var soundEnabled = true
    var hapticEnabled = true

    init {
        // SoundPool with no asset files — we just use haptic feedback for now.
        // Real production would load .ogg files from assets/raw.
    }

    fun correct() {
        if (soundEnabled) {
            // Use simple short tone via vibrator pattern
        }
        if (hapticEnabled) vibrate(longArrayOf(0, 30, 40, 30))
    }

    fun wrong() {
        if (hapticEnabled) vibrate(longArrayOf(0, 80, 60, 80))
    }

    fun click() {
        if (hapticEnabled) vibrate(longArrayOf(0, 15))
    }

    fun achievement() {
        if (hapticEnabled) vibrate(longArrayOf(0, 50, 50, 50, 50, 100))
    }

    fun levelUp() {
        if (hapticEnabled) vibrate(longArrayOf(0, 100, 80, 100, 80, 200))
    }

    private fun vibrate(pattern: LongArray) {
        val v = vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(pattern, -1)
        }
    }

    fun release() {
        soundPool.release()
    }
}
