package com.rk.attendence.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.relations.SemesterToClass
import com.rk.attendence.database.relations.SemesterToClassToAttendance
import kotlinx.coroutines.flow.Flow


@Dao
interface SemesterDao {
    @Upsert
    suspend fun upsertSemester(semesterEntity: SemesterEntity)

    @Delete
    suspend fun deleteSemester(semesterEntity: SemesterEntity)

    @Query("SELECT * FROM semester WHERE id = :id")
    fun getSemester(id: Int): Flow<SemesterEntity?>

    @Query("SELECT * FROM semester")
    fun getAllSemester(): Flow<List<SemesterEntity>>

    @Transaction
    @Query("SELECT * FROM semester WHERE id = :id")
    fun getAllClasses(id: Int): Flow<SemesterToClass?>

    @Transaction
    @Query("SElECT * FROM semester WHERE id =:id")
    fun getAllClassAndAttendance(id: Int): Flow<SemesterToClassToAttendance?>
//
//    @Transaction
//    @Query("SELECT * FROM semester WHERE id =:semId")
//    fun getAllDaysInParticularSem(semId: Int): Flow<SemesterToDay?>
//
//    @Transaction
//    @Query("SELECT * FROM semester")
//    fun getAllDays(): Flow<SemesterToDay>
//
//    @Transaction
//    @Query("SElECT * FROM semester WHERE id =:id")
//    fun getAllClassesCorrespondingDays(id: Int): Flow<SemToDayToClass?>
}