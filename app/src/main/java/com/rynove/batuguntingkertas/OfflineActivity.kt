/**
 * @author Novandi Ramadhan
 * @since 2022
 */

package com.rynove.batuguntingkertas

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.rynove.batuguntingkertas.databinding.ActivityOfflineBinding

@Suppress("DEPRECATION")
class OfflineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOfflineBinding
    private lateinit var player: MediaPlayer
    private var service: Intent? = null
    private var round: Int = 1 // Inisiasi variabel untuk ronde
    private var botPoint: Int = 0 // Inisiasi variabel untuk poin bot
    private var yourPoint: Int = 0 // Inisiasi variabel untuk poin user
    private var status: String = "" // Inisiasi variabel untuk pesan status
    private var result: String = "" // Inisiasi variabel untuk pesan hasil akhir
    private val maxRound: Int = 10 // Inisiasi variabel untuk maksimal ronde yang dimainkan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfflineBinding.inflate(layoutInflater)
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

        // Mematikan musik latar belakang utama
        service = Intent(this, BackgroundSoundGameService::class.java)
        stopService(service)

        // Memutar musik latar belakang game
        player = MediaPlayer.create(applicationContext, R.raw.ingame)
        player.isLooping = true
        playBackgroundMusic()

        binding.btnBatu.setOnClickListener { handler(1) }
        binding.btnGunting.setOnClickListener { handler(2) }
        binding.btnKertas.setOnClickListener { handler(3) }
        binding.btnPlayagain.setOnClickListener { playAgain() }
        binding.btnExit.setOnClickListener { exitGame() }
    }

    /**
     * Efek suara ketika klik
     */
    private fun soundOnTap() {
        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.tap)
        mediaPlayer.start()
    }

    /**
     * Efek suara ketika User menang
     */
    private fun soundUserWin() {
        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.winner)
        mediaPlayer.start()
    }

    /**
     * Efek suara ketika Bot menang
     */
    private fun soundBotWin() {
        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.lose)
        mediaPlayer.start()
    }

    /**
     * Efek suara ketika Seri
     */
    private fun soundDraw() {
        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.draw)
        mediaPlayer.start()
    }

    /**
     * Memutar musik latar belakang game
     */
    private fun playBackgroundMusic() {
        player.start()
    }

    /**
     * Memberhentikan musik latar belakang game
     */
    private fun stopBackgroundMusic() {
        player.pause() // Pause musik
        player.seekTo(0) // Atur musik dimulai dari awal
    }

    /**
     * Memunculkan pesan konfirmasi ketika user pencet tombol back
     */
    override fun onBackPressed() {
        exitGame()
    }

    /**
     * Memunculkan pesan konfirmasi ingin keluar apa ngga
     */
    private fun exitGame() {
        soundOnTap()
        AlertDialog.Builder(this).apply {
            setMessage("Yakin ingin keluar dari permainan?")
            setPositiveButton(android.R.string.yes) { _, _ ->
                stopBackgroundMusic()
                startService(service)
                finish()
            }

            setNegativeButton(android.R.string.no) { _,_ ->
                Toast.makeText(
                    applicationContext,
                    android.R.string.no, Toast.LENGTH_SHORT
                ).show()
            }
            setCancelable(true)
        }.create().show()
    }

    /**
     * Menghandle point dan ronde ketika user memilih batu, gunting atau kertas
     */
    private fun handler(user: Int) {
        soundOnTap()
        check(user, bot())
        increaseRound()
        if(lastRound()) {
            toggleRound(true)
            togglePlayAgain(false)
            toggleGame(true)
            stopBackgroundMusic()
            soundLastWinner()
            lastWinner()
            toggleResult(true)
        }
        bindingUpdate()
    }

    /**
     * Bot memilih Batu, Gunting atau Kertas secara random
     *
     * @return number
     */
    private fun bot(): Int {
        return (1..3).random()
    }

    /**
     * Mengecek siapa yang menang antara pilihan yang dipilih oleh User dan Bot
     *
     * @param { user } Pilihan Batu, Gunting atau Kertas yang dipilih oleh User
     * @param { bot } Pilihan Batu, Gunting atau Kertas yang dipilih oleh Bot
     */
    private fun check(user: Int, bot: Int) {
         if (bot == 1 && user == 2) increasePoint(0, 10,"bot")
            else if(bot == 1 && user == 3) increasePoint(10, 0,"user")
            else if(bot == 2 && user == 1) increasePoint(10, 0,"user")
            else if(bot == 2 && user == 3) increasePoint(0, 10,"bot")
            else if(bot == 3 && user == 1) increasePoint(0, 10,"bot")
            else if(bot == 3 && user == 2) increasePoint(10, 0,"user")
            else increasePoint(0, 0,"draw")
    }

    /**
     * Menaikkan ronde
     */
    private fun increaseRound() {
        round += 1
    }

    /**
     * Memperbarui point untuk User dan Bot
     *
     * @param { user } Poin yang didapat oleh User ketika memilih Batu, Gunting atau Kertas tersebut
     * @param { bot } Poin yang didapat oleh Bot ketika memilih Batu, Gunting atau Kertas tersebut
     * @param { win } Pemenang dari hasil memilih Batu, Gunting atau Kertas tersebut
     */
    private fun increasePoint(user: Int, bot: Int, win: String) {
        yourPoint += user
        botPoint += bot
        winner(win)
    }

    /**
     * Memperbarui pesan status siapa yang menang ketika User dan Bot memilih Batu, Gunting atau Kertas tersebut
     *
     * @param { win } Pemenang
     */
    private fun winner(win: String) {
        status = when (win) {
            "bot" -> "Musuh menang!" // Jika bot menang
            "user" -> "Kamu menang!" // Jika user menang
            else -> "Seri yah" // Jika user dan bot imbang
        }
    }

    /**
     * Memperbarui ronde, poin Bot, poin User dan pesan status pada antarmuka
     */
    private fun bindingUpdate() {
        binding.txtRonde.text = round.toString()
        binding.poinMusuh.text = botPoint.toString()
        binding.poinKamu.text = yourPoint.toString()
        binding.txtStatus.text = status
        if(result.isNotEmpty()) binding.result.text = result
    }

    /**
     * Mengecek apakah sudah ronde terakhir apa belum
     *
     * @return boolean
     */
    private fun lastRound(): Boolean {
        return round == maxRound + 1
    }

    /**
     * Menampilkan atau menyembunyikan tampilan `Main lagi`
     * Jika `play` adalah true, maka sembunyikan tampilan `Main lagi`
     * Jika `play` adalah false, maka tampilkan tampilan `Main lagi`
     *
     * @param { play } true or false
     */
    private fun togglePlayAgain(play: Boolean) {
        binding.playagainBox.isVisible = !play
    }

    /**
     * Menampilkan atau menyembunyikan tampilan `Countdown`
     * Jika `finish` adalah true, maka sembunyikan tampilan `Countdown`
     * Jika `finish` adalah false, maka tampilkan tampilan `Countdown`
     *
     * @param { finish } true or false
     */
    private fun toggleCountdown(finish: Boolean) {
        binding.countdownBox.isVisible = !finish
    }

    /**
     * Menampilkan atau menyembunyikan tampilan Ronde
     * Jika `play` adalah true, maka sembunyikan tampilan Ronde
     * Jika `play` adalah false, maka tampilkan tampilan Ronde
     *
     * @param { play } true or false
     */
    private fun toggleRound(play: Boolean) {
        binding.rondeBox.isVisible = !play
    }

    /**
     * Menghitung mundur atau `Countdown` dari angka 3 sampai 0
     * Jika hitungan mundur selesai, maka permainan bisa dimulai kembali
     */
    private fun countdown() {
        toggleCountdown(false) // Tampilkan `Countdown`
        object: CountDownTimer(4000, 1000) {
            override fun onTick(p0: Long) { // Ketika hitungan mundur berlangsung
                binding.countdown.text = if((p0 / 1000).toString() == "0") "Mulai" else (p0 / 1000).toString() // Memperbarui angka pada antarmuka
            }

            override fun onFinish() { // Ketika hitungan mundur selesai
                toggleCountdown(true) // Sembunyikan tampilan `Countdown` pada antarmuka
                toggleGame(false) // Sembunyikan tampilan `Main lagi` pada antarmuka
            }
        }.start() // Mulai hitungan mundur
    }

    /**
     * Menampilkan atau menyembunyikan tombol Batu, Gunting dan Kertas
     * Jika `playagain` adalah true, maka sembunyikan tombol Batu, Gunting dan Kertas
     * Jika `playagain` adalah false, maka tampilkan tombol Batu, Gunting dan Kertas
     */
    private fun toggleGame(playagain: Boolean) {
        binding.gameBox.isVisible = !playagain
    }

    /**
     * Menampilkan atau menyembunyikan hasil akhir
     * Jika `show` adalah true, maka sembunyikan hasil akhir
     * Jika `show` adalah false, maka tampilkan hasil akhir
     */
    private fun toggleResult(show: Boolean) {
        binding.resultBox.isVisible = show
    }

    /**
     * Me-reset ronde, poin Bot, poin User dan pesan status
     */
    private fun reset() {
        round = 1
        botPoint = 0
        yourPoint = 0
        status = ""
        result = ""
    }

    /**
     * Memulai permainan kembali
     */
    private fun playAgain() {
        soundOnTap()
        toggleResult(false)
        reset()
        toggleRound(false)
        bindingUpdate()
        togglePlayAgain(true)
        playBackgroundMusic()
        countdown()
    }

    /**
     * Menentukan pemenang akhir
     */
    private fun lastWinner() {
        result = when {
            yourPoint > botPoint -> "Yeay kamu menang!"
            yourPoint < botPoint -> "Yahh kamu kalah :("
            else -> "Imbang yah"
        }
    }

    /**
     * Menentukan suara User menang, Bot menang atau seri
     */
    private fun soundLastWinner() {
        when {
            yourPoint > botPoint -> {
                soundUserWin()
            }
            yourPoint < botPoint -> {
                soundBotWin()
            }
            else -> {
                soundDraw()
            }
        }
    }
}