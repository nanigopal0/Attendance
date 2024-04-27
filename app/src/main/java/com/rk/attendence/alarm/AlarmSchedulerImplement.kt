package com.rk.attendence.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rk.attendence.sharedpref.LocalData
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date

class AlarmSchedulerImplement(private val context: Context) : AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("MissingPermission")
    override fun schedule(localDate: LocalDate) {

        val isAlarmScheduled = try {
            LocalData.getBoolean(LocalData.BROADCAST_CLASS)
        } catch (e: Exception) {
            println(e)
            false
        }

        if (isAlarmScheduled) {
            println(alarmManager.nextAlarmClock)
            println("Alarm already scheduled")
        } else {
            val localDateTime = localDate.atStartOfDay()
            val intent = Intent(context, AlarmReceiver::class.java)
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = localDateTime.plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli()
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
            }
            println(calendar.timeInMillis)
            println(Date(calendar.timeInMillis))
            LocalData.setBoolean(LocalData.BROADCAST_CLASS, true)

            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                PendingIntent.getBroadcast(
                    context,
                    45,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            LocalDateTime.now().plusSeconds(10).atZone(ZoneId.systemDefault())
//                .toEpochSecond() * 1000L,
//            PendingIntent.getBroadcast(
//                context,
//                45,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        )
        }
    }
}