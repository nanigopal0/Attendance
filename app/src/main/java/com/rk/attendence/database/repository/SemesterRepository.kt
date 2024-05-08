package com.rk.attendence.database.repository

import com.rk.attendence.bottomnavigation.screens.dashboard.ClassContent
import com.rk.attendence.database.dao.SemesterDao
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.relations.SemesterToClassToAttendance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class SemesterRepository(private val semesterDao: SemesterDao) {

    suspend fun upsertSemester(semesterEntity: SemesterEntity) =
        semesterDao.upsertSemester(semesterEntity)

    suspend fun deleteSemester(semesterEntity: SemesterEntity) =
        semesterDao.deleteSemester(semesterEntity)

    fun getSemester(id: Int): Flow<SemesterEntity?> = semesterDao.getSemester(id)

    fun getAllSemester(): Flow<List<SemesterEntity>> = semesterDao.getAllSemester()

    fun getAllClasses(id: Int): Flow<List<ClassEntity>> {
        return semesterDao.getAllClasses(id).map {
            it?.classes ?: emptyList()
        }
    }

    fun getAllClassesInaDay(semId: Int, day: String): Flow<List<ClassEntity>> {
        return semesterDao.getAllClasses(semId).map { semClass ->
            val list = mutableListOf<ClassEntity>()
            semClass?.classes?.forEach {
                if (it.classStartTime.keys.contains(day))
                    list.add(it)
            }
            list
        }
    }


    fun getAllClassAndAttendance(semId: Int): Flow<List<ClassContent>?> {
        return semesterDao.getAllClassAndAttendance(semId).map { stcta ->
            val list = mutableListOf<ClassContent>()
            stcta?.classToAttendance?.forEach { cta ->
                if (cta != null) {
//                    val present = cta.attendanceList.filter { it?.present == true }.size
//                    val absent = cta.attendanceList.filter { it?.absent == true }.size
                    list.add(
                        ClassContent(
                            classEntity = cta.classEntity,
//                            present = present,
//                            absent = absent
                        )
                    )
                }
            }
            list
        }
    }

    fun getAllClassAttend(semId: Int): Flow<SemesterToClassToAttendance?> {
        return semesterDao.getAllClassAndAttendance(semId)
    }

    fun getAllClassAndAttendance(
        id: Int,
        day: String,
    ): Flow<List<ClassContent>?> {
        return semesterDao.getAllClassAndAttendance(id).map { stcta ->
            val list = mutableListOf<ClassContent>()
            stcta?.classToAttendance?.forEach { cta ->
                if (cta?.classEntity?.classStartTime?.keys?.contains(day) == true) {
                    val todayAttendance = cta.attendanceList.find {
                        it?.id == "${
                            LocalDate.now().toEpochDay()
                        }${cta.classEntity.id}".toLong()
                    }

                    list.add(
                        ClassContent(
                            classEntity = cta.classEntity,
                            isTodayPresent = todayAttendance?.present ?: false,
                            isTodayAbsent = todayAttendance?.absent ?: false,
                            isTodayCancel = todayAttendance?.cancel ?: false
                        )
                    )

                }
            }
            list
        }
    }

    fun getAllDaysInParticularSem(semId: Int): Flow<List<String>?> {
        return semesterDao.getSemester(semId).map {
            it?.days
        }
    }

}

