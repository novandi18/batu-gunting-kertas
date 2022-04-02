/**
 * @author Novandi Ramadhan
 * @since 2022
 */

package com.rynove.batuguntingkertas

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.rynove.batuguntingkertas.databinding.ActivityHomeBinding

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide Navigation Menu & Status Bar
        window.decorView.apply {
            systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        val service = Intent(this, BackgroundSoundGameService::class.java)
        startService(service)

        binding.btnOffline.setOnClickListener {
            soundFx()
            navigateToOffline()
        }
    }

    private fun navigateToOffline() {
        val intent = Intent(this, OfflineActivity::class.java)
        startActivity(intent)
    }

    private fun soundFx() {
        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.tap)
        mediaPlayer.start()
    }
}