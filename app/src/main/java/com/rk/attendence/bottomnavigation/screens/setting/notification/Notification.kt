package com.rk.attendence.bottomnavigation.screens.setting.notification

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rk.attendence.alarm.AlarmReceiver
import com.rk.attendence.alarm.AlarmScheduleForSubject
import com.rk.attendence.alarm.AlarmSchedulerImplement
import com.rk.attendence.notification.LocalDataInNotification
import com.rk.attendence.sharedpref.LocalData
import java.time.LocalDate


@Composable
fun Notification(onClick: () -> Unit) {
    val context = LocalContext.current
    val call = remember {
        mutableStateOf(checkNotificationPermission(context))
    }
    if (!call.value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationReq = notificationRequest {
                       if (it) {
                           LocalData.setBoolean(LocalData.NOTIFICATION, true)
                           AlarmScheduleForSubject(context).createNotificationChannel(context)
                           AlarmReceiver().createAlarmForTodayClasses(context)
                           val alarm = AlarmSchedulerImplement(context)
                           alarm.schedule(LocalDate.now())
                           call.value = true
                           println("Permission granted")
                       } else {
                           call.value = false
                           LocalData.setBoolean(LocalData.NOTIFICATION,false)
                           val alarmManager = context.getSystemService(AlarmManager::class.java)
                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) alarmManager.cancelAll()
                           else cancelled(context, alarmManager)
                           println("Permission not granted")
                       }
            }
            LaunchedEffect(key1 = true) {
                notificationReq.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        val requestActivity =
           requestActivityCompose {
                if (it.resultCode == Activity.RESULT_OK && checkNotificationPermission(context)) {
                    LocalData.setBoolean(LocalData.NOTIFICATION, true)
                    AlarmScheduleForSubject(context).createNotificationChannel(context)
                    call.value = true
                    AlarmReceiver().createAlarmForTodayClasses(context)
                    val alarm = AlarmSchedulerImplement(context)
                    alarm.schedule(LocalDate.now())
                } else if (!checkNotificationPermission(context)){
                    LocalData.setBoolean(LocalData.NOTIFICATION, false)
                    LocalData.setBoolean(LocalData.NOTIFICATION,false)
                    val alarmManager = context.getSystemService(AlarmManager::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) alarmManager.cancelAll()
                    else cancelled(context, alarmManager)
                    println("Not granted the permission")
                    call.value = false
                } else call.value = false
            }

        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).addCategory(Intent.CATEGORY_DEFAULT)
                .setData(
                    Uri.parse("package:${context.packageName}")
                )
        Box(modifier = Modifier.fillMaxSize()) {
            Button(
                onClick = {
                    onClick.invoke()
                    requestActivity.launch(intent)
                },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(text = "Enable permission")
            }
        }
    } else {
        val notificationInLocalData = LocalData.getBoolean(LocalData.NOTIFICATION)
        val isNotificationEnable = remember {
            mutableStateOf(notificationInLocalData)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row {
                Text(
                    text = "Notification",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.titleLarge
                )
                Switch(checked = isNotificationEnable.value, onCheckedChange = {
                    isNotificationEnable.value = it
                    if (isNotificationEnable.value) {
                        LocalData.setBoolean(LocalData.NOTIFICATION, true)
                        LocalData.setBoolean(LocalData.BROADCAST_CLASS, false)
                        AlarmScheduleForSubject(context).createNotificationChannel(context)
                        if (LocalData.getInt(LocalData.CLASS_ID) > 0 ) {
                            AlarmReceiver().createAlarmForTodayClasses(context)
                            val alarm = AlarmSchedulerImplement(context)
                            alarm.schedule(LocalDate.now())
                        }
                    } else {
                        LocalData.setBoolean(LocalData.NOTIFICATION,false)
                         val alarmManager = context.getSystemService(AlarmManager::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            alarmManager.cancelAll()
                        } else {
                            cancelled(context, alarmManager)
                        }
                    }
                }, modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
    }
}

@Composable
fun notificationRequest(callback:(Boolean) -> Unit): ManagedActivityResultLauncher<String, Boolean> {
    return rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),callback)
}

@Composable
fun requestActivityCompose(callback: (ActivityResult) -> Unit): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(), callback)
}

fun checkNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.checkSelfPermission(
            context as Activity,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        println("not android 13")
        true
    }
}


fun cancelled(context: Context, alarmManager: AlarmManager) {
    val today = LocalData.getString(LocalData.TODAY_CLASS)
    val type = object : TypeToken<List<LocalDataInNotification>>() {}.type
    val dataList = Gson().fromJson<List<LocalDataInNotification>>(today, type)
    val alarmScheduleForSubject = AlarmScheduleForSubject(context)
    dataList.forEach {
        alarmScheduleForSubject.cancelAlarm(it.classEntity.id)
    }
    alarmManager.cancel(
        PendingIntent.getBroadcast(
            context,
            45,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    )
}
