package com.rk.attendence.database.repository

import com.rk.attendence.database.dao.ClassDao
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.relations.ClassToAttendance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClassRepository(private val classDao: ClassDao) {
    suspend fun upsertClass(classEntity: ClassEntity) = classDao.upsertClass(classEntity)

    suspend fun deleteClass(classEntity: ClassEntity) = classDao.deleteClass(classEntity)

    suspend fun deleteClassCorrespondingSemId(semId: Int) = classDao.deleteClassCorrespondingSemId(semId)

    fun getClass(className: String): Flow<ClassEntity?> = classDao.getClass(className)

    fun getAllClasses(): Flow<List<ClassEntity>> = classDao.getAllClasses()

    fun getAllAttendance(semId: Int): Flow<List<ClassToAttendance>> =
        classDao.getAllAttendance(semId)

    fun getTodayClasses(semId: Int, day: String): Flow<List<ClassEntity>> {
        return classDao.getTodayClasses(semId).map { ce ->
            val list = mutableListOf<ClassEntity>()
            ce.forEach {
                if (it.classStartTime.keys.contains(day))
                    list.add(it)
            }
            list
        }
    }

    fun getAttendanceInaClass(className: String): Flow<Map<String, Int>?> {
        return classDao.getAttendanceInaClass(className).map { cta ->
            val present = cta?.attendanceList?.map { it?.present }?.size ?: 0
            val absent = cta?.attendanceList?.map { it?.absent }?.size ?: 0
            mapOf(
                "present" to present,
                "absent" to absent
            )

        }
    }
}