package com.letterbloom.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.letterbloom.app.MainActivity
import com.letterbloom.app.R

object NotificationHelper {
    const val CHANNEL_ID = "study_reminder"
    private const val NOTIF_MORNING = 1001
    private const val NOTIF_NIGHT   = 1002

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID, "학습 알림", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "영어 학습 알림 (오전 10시 / 오후 11시)" }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    fun showStudyNotification(context: Context, type: String) {
        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, body, notifId) = if (type == "morning") Triple(
            "🌿 Good morning! 오늘 영어 한 마디",
            "오전 10시 — 오늘 단어 5개만 익혀봐요 ✈️",
            NOTIF_MORNING
        ) else Triple(
            "🌙 오늘 하루 마무리 영어",
            "자기 전 2분! 오늘 배운 표현을 복습해요 📖",
            NOTIF_NIGHT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(notifId, notification)
    }
}
