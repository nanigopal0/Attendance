package com.rk.attendence.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rk.attendence.database.entity.AttendanceEntity
import com.rk.attendence.database.entity.ClassEntity

data class ClassToAttendance(
    @Embedded
    val classEntity: ClassEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "classId"
    )
    val attendanceList: List<AttendanceEntity?>,
)
