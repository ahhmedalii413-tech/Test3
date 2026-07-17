package com.example.pakforces.ui.utilities

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.Locale

/**
 * Text-to-speech for Voice Mode. Reads questions aloud.
 * Lazy-init — only created when first used.
 */
class TtsManager(private val context: Context) {
    private var tts: TextToSpeech? = null
    private var ready = false

    fun init() {
        if (tts != null) return
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                ready = true
            }
        }
    }

    fun speak(text: String) {
        if (!ready) {
            Toast.makeText(context, "Voice mode tayyar ho raha hai…", Toast.LENGTH_SHORT).show()
            init()
            return
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "pakforces_q_${System.currentTimeMillis()}")
    }

    fun stop() { tts?.stop() }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
    }
}
