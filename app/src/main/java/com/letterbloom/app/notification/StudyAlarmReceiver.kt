package com.letterbloom.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar
import java.util.TimeZone

class StudyAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ALARM_TYPE = "alarm_type"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val kst = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        val dayOfWeek = kst.get(Calendar.DAY_OF_WEEK)
        // 월(2)~금(6)만 발송
        if (dayOfWeek !in Calendar.MONDAY..Calendar.FRIDAY) return

        val type = intent.getStringExtra(EXTRA_ALARM_TYPE) ?: "morning"
        NotificationHelper.showStudyNotification(context, type)
    }
}
