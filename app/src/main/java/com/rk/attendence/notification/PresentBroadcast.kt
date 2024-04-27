package com.rk.attendence.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.database.entity.AttendanceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class PresentBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        println("Present Button working...")
        val intentData = intent.getStringExtra("subject")
        val subject = Gson().fromJson(intentData, LocalDataInNotification::class.java)
        val currentDate = LocalDate.now()
        val attendanceEntity = AttendanceEntity(
            id = "${currentDate.toEpochDay()}${subject.classEntity.id}".toLong(),
            present = true, absent = false, semesterId = subject.classEntity.semesterId,
            cancel = false, classId = subject.classEntity.id, date = currentDate
        )
        val classEntity = subject.classEntity.copy(present = subject.classEntity.present + 1)
        CoroutineScope(Dispatchers.IO).launch {
            SingletonDBConnection.classRepo.upsertClass(classEntity)
            SingletonDBConnection.attendanceRepo.upsertAttendance(attendanceEntity)
        }
        //Cancel the notification
        NotificationService(context).hideNotification()
    }
}