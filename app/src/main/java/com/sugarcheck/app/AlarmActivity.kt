package com.sugarcheck.app

import android.app.Activity
import android.app.NotificationManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class AlarmActivity : Activity() {
    private var player: MediaPlayer? = null
    private val bg = Color.rgb(11,16,32)
    private val mint = Color.rgb(167,243,208)
    private val white = Color.rgb(245,247,255)
    private val muted = Color.rgb(155,166,190)
    private var alarmId = 999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmId = intent.getIntExtra("id", 999)
        if (Build.VERSION.SDK_INT >= 27) setShowWhenLocked(true)
        if (Build.VERSION.SDK_INT >= 27) setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.statusBarColor = bg
        window.navigationBarColor = bg

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(28), dp(30), dp(28), dp(30))
            setBackgroundColor(bg)
        }
        root.addView(TextView(this).apply {
            text = "✦  SUGARCHECK"; textSize = 13f; setTextColor(mint)
            letterSpacing = .14f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = intent.getStringExtra("title") ?: "Time to check in"
            textSize = 32f; setTextColor(white); typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER; setPadding(0, dp(28), 0, dp(10))
        })
        root.addView(TextView(this).apply {
            text = "♡"; textSize = 68f; setTextColor(mint); gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, dp(12))
        })
        root.addView(TextView(this).apply {
            text = "I'm okay."; textSize = 18f; setTextColor(muted); gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(28))
        })
        root.addView(Button(this).apply {
            text = "OK"; isAllCaps = false; textSize = 17f
            setTextColor(Color.rgb(10,28,27)); background = rounded(mint, 18)
            setPadding(dp(30), dp(12), dp(30), dp(12))
            setOnClickListener {
                stopSound()
                (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(alarmId)
                finish()
            }
        }, LinearLayout.LayoutParams(-1, dp(58)))
        setContentView(root)
        startSound()
    }

    private fun startSound() {
        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            player = MediaPlayer().apply {
                setDataSource(this@AlarmActivity, uri)
                isLooping = true
                prepare()
                start()
            }
        } catch (_: Exception) {}
    }
    private fun stopSound() { try { player?.stop() } catch (_: Exception) {}; player?.release(); player = null }
    override fun onDestroy() { stopSound(); super.onDestroy() }
    override fun onBackPressed() { /* Keep reminder visible until OK. */ }
    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color); cornerRadius = dp(radius).toFloat()
    }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
