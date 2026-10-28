package com.sugarcheck.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object AlarmScheduler {
    data class Reminder(val hour: Int, val minute: Int, val title: String, val id: Int)

    // Source schedule from the Minneapolis column. The 2 AM and 6 AM alarms mark
    // the endpoints of the requested 2–6 AM window.
    private val sourceTimes = listOf(
        Reminder(8,0,"Morning check-in",101),
        Reminder(12,30,"Midday check-in",102),
        Reminder(18,30,"Evening check-in",103),
        Reminder(23,0,"Important check-in",104),
        Reminder(2,0,"Night window begins",105),
        Reminder(6,0,"Night window ends",106)
    )

    fun scheduleAll(context: Context) {
        val prefs = context.getSharedPreferences("sugarcheck", Context.MODE_PRIVATE)
        val her = prefs.getString("role", "me") == "her"
        val sourceZone = ZoneId.of("America/Chicago")
        val targetZone = if (her) sourceZone else ZoneId.of("Asia/Kolkata")
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = ZonedDateTime.now(targetZone)

        sourceTimes.forEach { reminder ->
            var nextTrigger: ZonedDateTime? = null
            // Look ahead across source-zone dates. Conversion automatically respects
            // Minneapolis daylight-saving transitions and India has no DST.
            for (offset in 0..3) {
                val sourceDate = LocalDate.now(sourceZone).plusDays(offset.toLong())
                val sourceDateTime = LocalDateTime.of(sourceDate, LocalTime.of(reminder.hour, reminder.minute))
                val converted = sourceDateTime.atZone(sourceZone).withZoneSameInstant(targetZone)
                if (converted.isAfter(now) && (nextTrigger == null || converted.isBefore(nextTrigger))) {
                    nextTrigger = converted
                }
            }
            val trigger = nextTrigger ?: return@forEach
            val intent = Intent(context, AlarmReceiver::class.java)
                .putExtra("title", reminder.title)
                .putExtra("id", reminder.id)
            val pi = PendingIntent.getBroadcast(
                context, reminder.id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger.toInstant().toEpochMilli(), pi)
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger.toInstant().toEpochMilli(), pi)
            }
        }
    }
}
