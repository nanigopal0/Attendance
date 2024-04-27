package com.rk.attendence.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.relations.ClassToAttendance
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassDao {
    @Upsert
    suspend fun upsertClass(classEntity: ClassEntity)

    @Delete
    suspend fun deleteClass(classEntity: ClassEntity)

    @Query("DELETE FROM classes WHERE semesterId =:semId")
    suspend fun deleteClassCorrespondingSemId(semId: Int)

    @Query("SELECT * FROM classes")
    fun getAllClasses(): Flow<List<ClassEntity>>

    @Query("SELECT * FROM classes WHERE className = :className")
    fun getClass(className: String): Flow<ClassEntity?>

    @Query("SELECT * FROM classes WHERE semesterId = :semId")
    fun getTodayClasses(semId: Int): Flow<List<ClassEntity>>

    @Transaction
    @Query("SELECT * FROM classes WHERE className =:className")
    fun getAttendanceInaClass(className: String): Flow<ClassToAttendance?>

    @Transaction
    @Query("SELECT * FROM classes WHERE semesterId =:semId")
    fun getAllAttendance(semId: Int): Flow<List<ClassToAttendance>>
}