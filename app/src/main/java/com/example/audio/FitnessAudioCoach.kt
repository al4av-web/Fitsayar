package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import java.util.Locale

class FitnessAudioCoach(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isVoiceCoachEnabled: Boolean = true

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            // Audio init fallback
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val turkishLocale = Locale("tr", "TR")
            val result = tts?.setLanguage(turkishLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            tts?.setSpeechRate(1.1f)
            tts?.setPitch(1.05f)
            isTtsReady = true
        }
    }

    fun onRepCounted(repNumber: Int, goal: Int) {
        triggerHaptic(60)

        if (!isSoundEnabled) return

        playBeep(ToneGenerator.TONE_PROP_BEEP)

        if (isVoiceCoachEnabled && isTtsReady) {
            val speechText = when {
                repNumber == goal -> "Tebrikler! $repNumber tekrar tamamlandı!"
                repNumber == (goal / 2) && goal > 4 -> "$repNumber! Harika, yarısı bitti!"
                repNumber % 10 == 0 -> "$repNumber! Mükemmel tempo!"
                repNumber % 5 == 0 -> "$repNumber! Devam et!"
                else -> "$repNumber"
            }
            speak(speechText)
        }
    }

    fun speakMotivation(message: String) {
        if (!isSoundEnabled || !isVoiceCoachEnabled || !isTtsReady) return
        speak(message)
    }

    private fun speak(text: String) {
        try {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "RepAudio_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            // Ignore speak errors
        }
    }

    private fun playBeep(toneType: Int) {
        try {
            toneGenerator?.startTone(toneType, 120)
        } catch (e: Exception) {
            // Ignore tone errors
        }
    }

    private fun triggerHaptic(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // Ignore vibration errors
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
            toneGenerator?.release()
        } catch (e: Exception) {
            // Cleanup
        }
    }
}
