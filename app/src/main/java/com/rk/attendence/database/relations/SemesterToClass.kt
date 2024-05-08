package com.rk.attendence.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.entity.SemesterEntity

data class SemesterToClass(
    @Embedded
    val semester: SemesterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "semesterId"
    )
    val classes: List<ClassEntity>,
)


