package com.rk.attendence.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity("semester")
data class SemesterEntity(
    @PrimaryKey
    val id: Int,
    val studentName: String,
    val semesterName: String,
    val days: List<String>,
    val dateCreation: LocalDate,
)
