/**
 * @author Novandi Ramadhan
 * @since 2022
 */

package com.rynove.batuguntingkertas

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

@Suppress("DEPRECATION")
class BackgroundSoundGameService : Service() {
    private lateinit var player: MediaPlayer

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @Override
    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(applicationContext, R.raw.home)
        player.isLooping = true
        player.setVolume(100F, 100F)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        player.start()
        return START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    @Override
    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
    }

    @Override
    override fun onLowMemory() {
        super.onLowMemory()
    }
}