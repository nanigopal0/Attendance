package com.rk.attendence.bottomnavigation.screens.semester

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.attendence.bottomnavigation.screens.classes.Days
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.repository.AttendanceRepository
import com.rk.attendence.database.repository.ClassRepository
import com.rk.attendence.database.repository.SemesterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class EditSemesterViewmodel(
    private val semRepo: SemesterRepository = SingletonDBConnection.semesterRepo,
    private val classRepo: ClassRepository = SingletonDBConnection.classRepo,
    private val attendanceRepository: AttendanceRepository = SingletonDBConnection.attendanceRepo,
) : ViewModel() {
    private var _state = MutableStateFlow(EditSemesterContent())
    val state: StateFlow<EditSemesterContent> = _state
    private var classEntities = listOf<ClassEntity>()

    fun initialiseVar(semesterEntity: SemesterEntity, classEntities: List<ClassEntity>) {
        this.classEntities = classEntities
        val map = mutableMapOf<String, Boolean>()
        semesterEntity.days.forEach {
            map[it] = true
        }
        state.value.daysList.minus(semesterEntity.days.toSet()).forEach { map[it] = false }
        _state.update {
            it.copy(
                semesterEntity = semesterEntity,
                previousCheckedDates = map,
                currentCheckedDates = map,
                semName = semesterEntity.semesterName
            )
        }

    }


    fun onClickEvent(event: EditSemesterEvent) {
        when (event) {
            EditSemesterEvent.Edit -> {
                val semesterEntity = SemesterEntity(
                    id = state.value.semesterEntity.id,
                    semesterName = state.value.semName,
                    days = state.value.daysList.filter { state.value.currentCheckedDates[it] == true },
                    dateCreation = state.value.semesterEntity.dateCreation,
                    studentName = state.value.semesterEntity.studentName
                )
                val currentDays =
                    state.value.daysList.filter { state.value.currentCheckedDates[it] == true }
                var remainingDays = state.value.semesterEntity.days.minus(currentDays.toSet())
                if (remainingDays.isEmpty()) remainingDays =
                    currentDays.minus(state.value.semesterEntity.days.toSet())
                println(remainingDays)
                //the class will be deleted which contain the day in remainingDays list corresponding attendance will be deleted
                val deletingClassEntities = mutableListOf<ClassEntity>()
                classEntities.forEach {
                    remainingDays.forEach { day ->
                        if (it.classStartTime.keys.contains(day)) deletingClassEntities.add(
                            it
                        )
                    }
                }
                viewModelScope.launch {
                    semRepo.upsertSemester(semesterEntity)
                    deletingClassEntities.forEach {
                        classRepo.deleteClass(it)
                        attendanceRepository.deleteAttendanceCorrespondingClass(it.id)
                    }
                }
            }

            is EditSemesterEvent.OnCheckedDatesChanged -> {
                val map = state.value.currentCheckedDates.toMutableMap()
                map[event.date] = event.isChecked

                _state.update {
                    it.copy(
                        currentCheckedDates = map,
                        atLeastOneChecked = map.values.contains(true) && state.value.previousCheckedDates != map
                    )
                }
            }

            is EditSemesterEvent.SetSemName -> _state.update {
                it.copy(
                    semName = event.name.trim(),
                    atLeastOneChecked = state.value.semesterEntity.semesterName != event.name.trim()
                )
            }
        }
    }
}

data class EditSemesterContent(
    val semesterEntity: SemesterEntity = SemesterEntity(0, "", "", emptyList(), LocalDate.now()),
    val semName: String = "",
    val atLeastOneChecked: Boolean = false,
    val previousCheckedDates: Map<String, Boolean> = emptyMap(),
    val currentCheckedDates: Map<String, Boolean> = emptyMap(),
    val daysList: List<String> = listOf(
        Days.MONDAY,
        Days.TUESDAY,
        Days.WEDNESDAY,
        Days.THURSDAY,
        Days.FRIDAY,
        Days.SATURDAY,
        Days.SUNDAY
    ),
)

sealed interface EditSemesterEvent {
    data object Edit : EditSemesterEvent
    data class OnCheckedDatesChanged(val date: String, val isChecked: Boolean) : EditSemesterEvent
    data class SetSemName(val name: String) : EditSemesterEvent
}