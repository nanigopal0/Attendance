package com.rk.attendence.notification

import com.rk.attendence.database.entity.ClassEntity

data class LocalDataInNotification(
    val classEntity: ClassEntity,
    val isNotified: Boolean
)
