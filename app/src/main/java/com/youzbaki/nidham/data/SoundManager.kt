package com.youzbaki.nidham.service

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import com.youzbaki.nidham.R

object SoundManager {

    private var checkSound: MediaPlayer? = null
    private var deleteSound: MediaPlayer? = null
    private var buttonSound: MediaPlayer? = null

    fun init(context: Context) {
        checkSound = MediaPlayer.create(context, R.raw.check).apply { setVolume(0.2f, 0.2f) }
        deleteSound = MediaPlayer.create(context, R.raw.delete).apply { setVolume(0.3f, 0.3f) }
        buttonSound = MediaPlayer.create(context, R.raw.click).apply { setVolume(0.2f, 0.2f) }
    }

    fun playCheck(context: Context) = play(context, checkSound, true)
    fun playDelete(context: Context) = play(context, deleteSound, true)
    fun playButton(context: Context) = play(context, buttonSound, false)

    private fun play(context: Context, player: MediaPlayer?, vibrate: Boolean) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                player?.also {
                    it.seekTo(0)
                    it.start()
                }
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                if (vibrate) {
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(40)
                    }
                }
            }
            // RINGER_MODE_SILENT: do nothing
        }
    }

    fun release() {
        checkSound?.release(); checkSound = null
        deleteSound?.release(); deleteSound = null
        buttonSound?.release(); buttonSound = null
    }
}