package com.rk.attendence.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rk.attendence.notification.NotificationService

class AlarmScheduleForSubject(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("MissingPermission")
    fun alarmCreate(time: Long, classId: Int) {
        val intent =
            Intent(context, AlarmBroadcastForSubject::class.java).putExtra("classId", classId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            classId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
//        println("alarm set")
    }

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            NotificationService.CHANNEL_ID,
            "Reminder",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Attendance reminder"
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun cancelAlarm(requestCode: Int) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                requestCode,
                Intent(context, AlarmBroadcastForSubject::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }
}