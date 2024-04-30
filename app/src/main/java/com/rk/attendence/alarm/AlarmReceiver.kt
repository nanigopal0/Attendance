package com.rk.attendence.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.notification.LocalDataInNotification
import com.rk.attendence.notification.NotificationService
import com.rk.attendence.sharedpref.LocalData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent?) {

        println("Alarm receiver")
        //Create notification channel
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

        SingletonDBConnection.provideContext(context)
        LocalData.initialize(context)

        createAlarmForTodayClasses(context)
    }


    private fun createAlarmForTodayClasses(context: Context) {
        //Get All today's classes
        val currentDay =
            LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        var job: Job? = null
        job = CoroutineScope(Dispatchers.IO).launch {
            SingletonDBConnection.classRepo.getTodayClasses(
                LocalData.getInt(LocalData.CURRENT_SEMESTER_ID),
                currentDay
            ).combine(
                SingletonDBConnection.attendanceRepo.getAttendanceInaDay(
                    LocalDate.now().toEpochDay()
                )
            )
            { c, a ->
                val list = mutableListOf<LocalDataInNotification>()
                c.forEach { ct ->
                    val attendance = a.find { it.classId == ct.id }
                    if (attendance == null) {
                        list.add(LocalDataInNotification(ct, false))
                    }
                }
                list
            }.collect {
                if (validateAlarm(context)) {
                    val c = Calendar.getInstance()
                    c.apply {
                        timeInMillis =
                            LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)
                                .toEpochMilli()
                        set(Calendar.HOUR_OF_DAY, 1)
                        set(Calendar.MINUTE, 0)
                    }
//                    println(c.time)
                    LocalData.setBoolean(LocalData.BROADCAST_CLASS, false)
                    AlarmSchedulerImplement(context).schedule(c.timeInMillis)
                    it.forEach { classEntity ->
                        if (classEntity.classEntity.classStartTime[currentDay].toString() != "") {
                            val time = classEntity.classEntity.classStartTime[currentDay]
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                set(Calendar.HOUR_OF_DAY, time?.substring(0, 2)?.toInt() ?: 0)
                                set(Calendar.MINUTE, time?.substring(3, 5)?.toInt() ?: 0)
                                set(Calendar.SECOND, 0)
                            }
//                            println("Alarm receiver " + calendar.time)
                            AlarmScheduleForSubject(context).alarmCreate(
                                classId = classEntity.classEntity.id,
                                time = calendar.timeInMillis
                            )
                        }
                    }
                }
                val json = Gson().toJson(it)
                LocalData.setString(LocalData.TODAY_CLASS, json)
                job?.cancel()
            }
        }


    }
}
