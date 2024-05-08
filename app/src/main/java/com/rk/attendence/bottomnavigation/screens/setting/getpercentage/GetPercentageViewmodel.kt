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
            val rangeAttendList = sortedAttendList?.filter { it?.date!! in startDate..endDate }
            val present = rangeAttendList?.filter { it?.present!! }?.size ?: 0
            val absent = rangeAttendList?.filter { it?.absent!! }?.size ?: 0
            val cancel = rangeAttendList?.filter { it?.cancel!! }?.size ?: 0
            val c = classToAttendance?.classEntity?.copy(
                present = present,
                absent = absent,
                cancel = cancel
            )
            println(classToAttendance?.classEntity?.className)
            println(rangeAttendList)
            if (c != null) {
                list.add(c)
            }
        }
        _state.update { it.copy(allClasses = list) }
    }

    fun getTotalAttendance() {
        var present = 0
        var absent = 0
        var cancel = 0
        state.value.allClasses.forEach {
//            println(it.className)
//            println("present${it.present}absent${it.absent}cancel${it.cancel}")
            present += it.present
            absent += it.absent
            cancel += it.cancel
        }

        val attend =
            try {
                println("present$present")
                println("absent$absent")
                println("cancel$cancel")
                present.times(100).div(present + absent)
            } catch (e: Exception) {
                0
            }
        _state.update { it.copy(totalAttendance = attend, cancelledClass = cancel) }

    }

}

data class GetPercentageContent(
    val totalAttendance: Int = 0,
    val cancelledClass: Int = 0,
    val allClasses: List<ClassEntity> = emptyList(),
)