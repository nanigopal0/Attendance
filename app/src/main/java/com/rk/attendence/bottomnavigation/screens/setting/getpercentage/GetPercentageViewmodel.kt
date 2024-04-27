package com.rk.attendence.bottomnavigation.screens.setting.getpercentage

import androidx.lifecycle.ViewModel
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.relations.SemesterToClassToAttendance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class GetPercentageViewmodel : ViewModel() {
    private var _state = MutableStateFlow(GetPercentageContent())
    val state: StateFlow<GetPercentageContent> = _state
    private var semesterToClassToAttendance: SemesterToClassToAttendance =
        SemesterToClassToAttendance(
            SemesterEntity(0, "", "", emptyList(), LocalDate.now()), emptyList()
        )

    fun initialiseVar(semToClassToAttend: SemesterToClassToAttendance?) {
        if (semToClassToAttend != null) {
            semesterToClassToAttendance = semToClassToAttend
        }
    }

    fun getPercentage(startDate: LocalDate, endDate: LocalDate) {
        val list = mutableListOf<ClassEntity>()
        semesterToClassToAttendance.classToAttendance.forEach { classToAttendance ->
            val sortedAttendList = classToAttendance?.attendanceList?.sortedBy { it?.id }
            val f = sortedAttendList?.filter { it?.date!! >= startDate }
            val rangeAttendList = f?.filter { it?.date!! <= endDate }
            val present = rangeAttendList?.filter { it?.present!! }?.size ?: 0
            val absent = rangeAttendList?.filter { it?.absent!! }?.size ?: 0
            val cancel = rangeAttendList?.filter { it?.cancel!! }?.size ?: 0
            val c = classToAttendance?.classEntity?.copy(
                present = present,
                absent = absent,
                cancel = cancel
            )
            if (c != null) {
                list.add(c)
            }
        }
        _state.update { it.copy(allClasses = list) }
    }

}

data class GetPercentageContent(
    val allClasses: List<ClassEntity> = emptyList()
)