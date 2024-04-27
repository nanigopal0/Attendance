package com.rk.attendence.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rk.attendence.MainActivity
import com.rk.attendence.R
import com.rk.attendence.sharedpref.LocalData

class NotificationService(
    private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationId = 121
    fun showNotification(classId: Int) {
        println("Enter in notification")
        val dataLocal = LocalData.getString(LocalData.TODAY_CLASS)
        val type = object : TypeToken<List<LocalDataInNotification>>() {}.type
        var data: LocalDataInNotification? = null
        try {
            val dataList = Gson().fromJson<List<LocalDataInNotification>>(dataLocal, type)
            dataList.forEach {
                if (it.classEntity.id == classId) {
                    data = it
                }
            }
        }catch (e: Exception) {
            data = Gson().fromJson(dataLocal,LocalDataInNotification::class.java)
        }
        println(data)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Attendance reminder")
            .setContentText("Give the attendance of ${data?.classEntity?.className} ")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Present",
                PendingIntent.getBroadcast(
                    context,
                    5,
                    Intent(context, PresentBroadcast::class.java).apply {
                        putExtra(
                            "subject",
                            Gson().toJson(data)
                        )
                    },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Absent",
                PendingIntent.getBroadcast(
                    context,
                    8,
                    Intent(context, AbsentBroadcast::class.java).putExtra(
                        "subject",
                        Gson().toJson(data)
                    ),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Cancel",
                PendingIntent.getBroadcast(
                    context,
                    10,
                    Intent(context, CancelBroadcast::class.java).putExtra(
                        "subject",
                        Gson().toJson(data)
                    ),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()

        notificationManager.notify(notificationId, notification)
    }
    fun hideNotification(){
        notificationManager.cancel(notificationId)
    }

    companion object {
        const val CHANNEL_ID = "channel_id"
    }
}