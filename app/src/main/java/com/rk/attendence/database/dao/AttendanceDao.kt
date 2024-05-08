package com.rk.attendence.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.rk.attendence.database.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface AttendanceDao {
    @Upsert
    suspend fun upsertAttendance(dayEntity: AttendanceEntity)

    @Delete
    suspend fun deleteAttendance(dayEntity: AttendanceEntity)

    @Query("DELETE FROM attendance WHERE classId =:classId")
    suspend fun deleteAttendanceCorrespondingClass(classId: Int)

    @Query("SELECT * FROM attendance")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("DELETE FROM attendance WHERE semesterId=:semId")
    suspend fun deleteAttendanceCorrespondingSemId(semId: Int)

    @Query("SELECT * FROM attendance WHERE date BETWEEN :startDate and :endDate ")
    fun getAttendanceFromRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE date =:date")
    fun getAttendanceInaDay(date: Long): Flow<List<AttendanceEntity>>

}