package com.rk.attendence.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey
    val id: Long,       //Id is the current date with the class id
    val present: Boolean,
    val absent: Boolean,
    val semesterId: Int,
    val cancel: Boolean,
    val classId: Int,    // Id of the class table
    val date: LocalDate,      // Current date
)
