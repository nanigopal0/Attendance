package com.rk.attendence.database.dbconnection

import android.content.Context
import com.rk.attendence.database.repository.AttendanceRepository
import com.rk.attendence.database.repository.ClassRepository
import com.rk.attendence.database.repository.SemesterRepository

object SingletonDBConnection {
    private lateinit var databaseConnection: DatabaseConnection

    fun provideContext(context: Context) {
        databaseConnection = DatabaseConnection.getDatabase(context)
    }

    val classRepo by lazy {
        ClassRepository(
            databaseConnection.classDao()
        )
    }
    val semesterRepo by lazy {
        SemesterRepository(
            databaseConnection.semesterDao()
        )
    }
    val attendanceRepo by lazy {
        AttendanceRepository(
            databaseConnection.attendanceDao()
        )
    }
}