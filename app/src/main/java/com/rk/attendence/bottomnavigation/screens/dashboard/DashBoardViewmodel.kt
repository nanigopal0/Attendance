package com.rk.attendence.bottomnavigation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.database.entity.AttendanceEntity
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.repository.AttendanceRepository
import com.rk.attendence.database.repository.ClassRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class DashBoardViewmodel(
    private val attendanceRepository: AttendanceRepository = SingletonDBConnection.attendanceRepo,
    private val classRepository: ClassRepository = SingletonDBConnection.classRepo,
) : ViewModel() {
    private val currentDay =
        LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    private val dateInLong = LocalDate.now().toEpochDay()
    private val nowLocalDate = LocalDate.now()
    private lateinit var currentSemesterEntity: SemesterEntity

    fun initiateCurrentSem(semesterEntity: SemesterEntity) {
        currentSemesterEntity = semesterEntity
    }

    fun getStartTime(classEntity: ClassEntity): String {
        return classEntity.classStartTime[currentDay] ?: ""
    }

    private fun updateAttendanceAndClass(attendance: AttendanceEntity, classEntity: ClassEntity) {
        viewModelScope.launch {
            attendanceRepository.upsertAttendance(
                attendance
            )
            classRepository.upsertClass(
                classEntity
            )
        }
    }

    fun onClickEventFunction(dashboardEvent: DashboardEvent) {
        when (dashboardEvent) {
            is DashboardEvent.Absent -> {
                if (!dashboardEvent.classContent.isTodayAbsent) {
                    val classId = dashboardEvent.classContent.classEntity.id
                    val id = "$dateInLong$classId".toLong()
                    val attendanceEntity = AttendanceEntity(
                        id = id,
                        date = nowLocalDate,
                        semesterId = currentSemesterEntity.id,
                        present = false,
                        absent = true,
                        cancel = false,
                        classId = classId
                    )
                    var present = dashboardEvent.classContent.classEntity.present
                    var cancel = dashboardEvent.classContent.classEntity.cancel
                    if (dashboardEvent.classContent.isTodayPresent) present--
                    if (dashboardEvent.classContent.isTodayCancel) cancel--
                    val absent = dashboardEvent.classContent.classEntity.absent + 1
                    updateAttendanceAndClass(
                        attendanceEntity,
                        dashboardEvent.classContent.classEntity.copy(
                            present = present,
                            absent = absent,
                            cancel = cancel
                        )
                    )
                }
            }

            is DashboardEvent.Present -> {
                if (!dashboardEvent.classContent.isTodayPresent) {
                    val classId = dashboardEvent.classContent.classEntity.id
                    val id = "$dateInLong$classId".toLong()
                    val attendanceEntity = AttendanceEntity(
                        id = id,
                        date = nowLocalDate,
                        semesterId = currentSemesterEntity.id,
                        present = true,
                        absent = false,
                        cancel = false,
                        classId = classId
                    )
                    var absent = dashboardEvent.classContent.classEntity.absent
                    var cancel = dashboardEvent.classContent.classEntity.cancel
                    if (dashboardEvent.classContent.isTodayAbsent) absent--
                    if (dashboardEvent.classContent.isTodayCancel) cancel--
                    val present = dashboardEvent.classContent.classEntity.present + 1
                    updateAttendanceAndClass(
                        attendanceEntity,
                        dashboardEvent.classContent.classEntity.copy(
                            absent = absent,
                            present = present,
                            cancel = cancel
                        )
                    )
                }
            }

            is DashboardEvent.Cancel -> {
                if (!dashboardEvent.classContent.isTodayCancel) {
                    val classId = dashboardEvent.classContent.classEntity.id
                    val id = "$dateInLong$classId".toLong()
                    val attendanceEntity = AttendanceEntity(
                        id = id,
                        date = nowLocalDate,
                        semesterId = currentSemesterEntity.id,
                        present = false,
                        absent = false,
                        cancel = true,
                        classId = classId
                    )
                    var absent = dashboardEvent.classContent.classEntity.absent
                    var present = dashboardEvent.classContent.classEntity.present
                    if (dashboardEvent.classContent.isTodayAbsent) absent--
                    if (dashboardEvent.classContent.isTodayPresent) present--
                    val cancel = dashboardEvent.classContent.classEntity.cancel + 1
                    updateAttendanceAndClass(
                        attendanceEntity,
                        dashboardEvent.classContent.classEntity.copy(
                            absent = absent,
                            present = present,
                            cancel = cancel
                        )
                    )
                }
            }
        }
    }
}

data class ClassContent(
    val classEntity: ClassEntity,
//    val present: Int = 0,
//    val absent: Int = 0,
//    val cancel: Int = 0,
    val isTodayPresent: Boolean = false,
    val isTodayAbsent: Boolean = false,
    val isTodayCancel: Boolean = false,
)

sealed interface DashboardEvent {
    data class Present(val classContent: ClassContent) : DashboardEvent
    data class Absent(val classContent: ClassContent) : DashboardEvent
    data class Cancel(val classContent: ClassContent) : DashboardEvent
}