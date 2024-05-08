package com.rk.attendence.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey
    val id: Int,
    val semesterId: Int,        // Semester table Id
    val className: String,      // Class/Subject name
    val classStartTime: Map<String, String>, //First is the day and the next is the time
    val present: Int,
    val absent: Int,
    val cancel: Int,
)
