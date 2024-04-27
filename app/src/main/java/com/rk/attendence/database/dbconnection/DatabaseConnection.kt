package com.rk.attendence.database.dbconnection

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rk.attendence.database.dao.AttendanceDao
import com.rk.attendence.database.dao.ClassDao
import com.rk.attendence.database.dao.SemesterDao
import com.rk.attendence.database.entity.AttendanceEntity
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.util.Converters

@Database(
    entities = [ClassEntity::class, SemesterEntity::class, AttendanceEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DatabaseConnection : RoomDatabase() {
    abstract fun semesterDao(): SemesterDao
    abstract fun classDao(): ClassDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var Instance: DatabaseConnection? = null

        fun getDatabase(context: Context): DatabaseConnection {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    DatabaseConnection::class.java,
                    "database_connection"
                )
            }.build().also { Instance = it }
        }
    }
}