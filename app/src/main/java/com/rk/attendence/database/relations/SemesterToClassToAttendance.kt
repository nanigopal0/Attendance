package com.rk.attendence.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.entity.SemesterEntity

data class SemesterToClassToAttendance(
    @Embedded
    val semesterEntity: SemesterEntity,
    @Relation(
        entity = ClassEntity::class,
        parentColumn = "id",
        entityColumn = "semesterId"
    )
    val classToAttendance: List<ClassToAttendance?>,
)
