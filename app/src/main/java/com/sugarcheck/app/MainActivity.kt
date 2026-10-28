package com.sugarcheck.app

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {
    private val bg = Color.rgb(11,16,32)
    private val panel = Color.rgb(21,29,50)
    private val mint = Color.rgb(167,243,208)
    private val white = Color.rgb(245,247,255)
    private val muted = Color.rgb(155,166,190)
    private val prefs by lazy { getSharedPreferences("sugarcheck", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        requestPermissionsIfNeeded()
        buildUi()
        AlarmScheduler.scheduleAll(this)
    }

    private fun requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 10)
        }
        if (Build.VERSION.SDK_INT >= 31) {
            val am = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!am.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName")))
            }
        }
    }

    private fun buildUi() {
        val scroll = ScrollView(this).apply { setBackgroundColor(bg) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(28), dp(22), dp(28))
        }
        scroll.addView(root)
        setContentView(scroll)

        root.addView(label("✦  SUGARCHECK", mint, 13f, true))
        root.addView(label("A little care,\nright on time.", white, 34f, true).apply {
            setPadding(0, dp(22), 0, dp(8))
        })
        root.addView(label("Choose the role for this phone.", muted, 14f, false))
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(panel, 22)
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }
        card.addView(label("THIS PHONE BELONGS TO", muted, 11f, true))
        val options = listOf("My phone · India", "Her phone · Minneapolis")
        val spinner = Spinner(this)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinner.setSelection(if (prefs.getString("role", "me") == "her") 1 else 0)
        card.addView(spinner)
        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val role = if (position == 1) "her" else "me"
                prefs.edit().putString("role", role).apply()
                AlarmScheduler.scheduleAll(this@MainActivity)
                refreshSchedule(scheduleText)
            }
        }
        root.addView(card, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(24) })

        root.addView(label("REMINDER TIMES", muted, 11f, true).apply { setPadding(0, dp(26), 0, dp(12)) })
        scheduleText = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(scheduleText)
        refreshSchedule(scheduleText)

        val note = label("Alerts stay on screen until OK is pressed.", muted, 12f, false)
        note.setPadding(0, dp(20), 0, 0)
        root.addView(note)
    }

    private lateinit var scheduleText: LinearLayout

    private fun refreshSchedule(container: LinearLayout) {
        container.removeAllViews()
        val her = prefs.getString("role", "me") == "her"
        val rows = if (her) listOf(
            "8:00 AM" to "Morning check-in",
            "12:30 PM" to "Midday check-in",
            "6:30 PM" to "Evening check-in",
            "11:00 PM" to "Important check-in",
            "2:00 AM" to "Night window begins",
            "6:00 AM" to "Night window ends"
        ) else listOf(
            "6:30 PM" to "Morning check-in · Minneapolis 8:00 AM",
            "11:00 PM" to "Midday check-in · Minneapolis 12:30 PM",
            "5:00 AM" to "Evening check-in · Minneapolis 6:30 PM",
            "9:30 AM" to "Important check-in · Minneapolis 11:00 PM",
            "12:30 PM" to "Night window begins · Minneapolis 2:00 AM",
            "4:30 PM" to "Night window ends · Minneapolis 6:00 AM"
        )
        rows.forEachIndexed { i, item ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = rounded(panel, 16)
                setPadding(dp(16), dp(14), dp(16), dp(14))
            }
            val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            col.addView(label(item.first, white, 17f, true))
            col.addView(label(item.second, muted, 12f, false).apply { setPadding(0, dp(4), 0, 0) })
            row.addView(col, LinearLayout.LayoutParams(0, -2, 1f))
            row.addView(label(if (i == 3) "IMPORTANT" else "DAILY", mint, 9f, true))
            container.addView(row, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(8) })
        }
    }

    private fun label(text: String, color: Int, size: Float, bold: Boolean) = TextView(this).apply {
        this.text = text; setTextColor(color); textSize = size
        if (bold) typeface = Typeface.create("sans-serif", Typeface.BOLD)
    }
    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color); cornerRadius = dp(radius).toFloat()
    }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
