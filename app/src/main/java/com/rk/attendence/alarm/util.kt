package com.rk.attendence.alarm

import android.content.Context
import com.rk.attendence.bottomnavigation.screens.setting.notification.checkNotificationPermission
import com.rk.attendence.sharedpref.LocalData

fun validateAlarm(context: Context): Boolean {  //If alarm will create or not
    return LocalData.getInt(LocalData.CURRENT_SEMESTER_ID) > 0 && LocalData.getInt(LocalData.CLASS_ID) > 0
            && LocalData.getBoolean(LocalData.NOTIFICATION) && checkNotificationPermission(context)
}