package com.rk.attendence.database.repository

import com.rk.attendence.database.dao.AttendanceDao
import com.rk.attendence.database.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class AttendanceRepository(private val attendanceDao: AttendanceDao) {

    suspend fun upsertAttendance(attendanceEntity: AttendanceEntity) =
        attendanceDao.upsertAttendance(attendanceEntity)

    suspend fun deleteAttendance(attendanceEntity: AttendanceEntity) =
        attendanceDao.deleteAttendance(attendanceEntity)

    fun getAllAttendance(): Flow<List<AttendanceEntity>> = attendanceDao.getAllAttendance()

    suspend fun deleteAttendanceCorrespondingSemId(semId: Int) =
        attendanceDao.deleteAttendanceCorrespondingSemId(semId)

    fun getAttendanceFromRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<AttendanceEntity>> =
        attendanceDao.getAttendanceFromRange(startDate, endDate)

    suspend fun deleteAttendanceCorrespondingClass(clasId: Int) =
        attendanceDao.deleteAttendanceCorrespondingClass(clasId)

    fun getAttendanceInaDay(date: Long): Flow<List<AttendanceEntity>> =
        attendanceDao.getAttendanceInaDay(date)
}