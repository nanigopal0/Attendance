package com.rk.attendence.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.notification.NotificationService
import com.rk.attendence.sharedpref.LocalData

class AlarmBroadcastForSubject : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val classId = intent.getIntExtra("classId", 0)
        LocalData.initialize(context)
        SingletonDBConnection.provideContext(context)
        NotificationService(context).showNotification(classId)
        println("alarm broadcast")
    }
}