package com.letterbloom.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar
import java.util.TimeZone

object AlarmScheduler {

    private const val REQUEST_MORNING = 100  // 오전 10시
    private const val REQUEST_NIGHT   = 101  // 오후 11시

    fun scheduleDailyAlarms(context: Context) {
        scheduleAlarm(context, hour = 10, minute = 0, requestCode = REQUEST_MORNING, type = "morning")
        scheduleAlarm(context, hour = 23, minute = 0, requestCode = REQUEST_NIGHT,   type = "night")
    }

    private fun scheduleAlarm(context: Context, hour: Int, minute: Int, requestCode: Int, type: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, StudyAlarmReceiver::class.java).apply {
            putExtra(StudyAlarmReceiver.EXTRA_ALARM_TYPE, type)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        listOf(REQUEST_MORNING to "morning", REQUEST_NIGHT to "night").forEach { (code, type) ->
            val pi = PendingIntent.getBroadcast(
                context, code,
                Intent(context, StudyAlarmReceiver::class.java).apply {
                    putExtra(StudyAlarmReceiver.EXTRA_ALARM_TYPE, type)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pi)
        }
    }
}
