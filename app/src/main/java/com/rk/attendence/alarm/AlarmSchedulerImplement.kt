package com.rk.attendence.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rk.attendence.sharedpref.LocalData

class AlarmSchedulerImplement(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("MissingPermission")
    fun schedule(time: Long) {

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

            val intent = Intent(context, AlarmReceiver::class.java)
//            val calendar: Calendar = Calendar.getInstance().apply {
//                timeInMillis = localDateTime.plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli()
//                set(Calendar.HOUR_OF_DAY, 0)
//                set(Calendar.MINUTE, 0)
//            }
//            println(calendar.timeInMillis)
//            println(Date(calendar.timeInMillis))
            LocalData.setBoolean(LocalData.BROADCAST_CLASS, true)

//            alarmManager.setInexactRepeating(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                AlarmManager.INTERVAL_DAY,
//                PendingIntent.getBroadcast(
//                    context,
//                    45,
//                    intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                )
//            )
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                time,
                PendingIntent.getBroadcast(
                    context,
                    45,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }
}