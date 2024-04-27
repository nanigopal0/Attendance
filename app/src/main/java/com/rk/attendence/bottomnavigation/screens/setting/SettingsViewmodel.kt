package com.rk.attendence.bottomnavigation.screens.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.relations.SemesterToClassToAttendance
import com.rk.attendence.database.repository.AttendanceRepository
import com.rk.attendence.database.repository.ClassRepository
import com.rk.attendence.database.repository.SemesterRepository
import com.rk.attendence.sharedpref.LocalData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class SettingsViewmodel(
    private val semesterRepository: SemesterRepository = SingletonDBConnection.semesterRepo,
    private val classRepository: ClassRepository = SingletonDBConnection.classRepo,
    private val attendanceRepository: AttendanceRepository = SingletonDBConnection.attendanceRepo
) : ViewModel() {
    private var _state = MutableStateFlow(SettingsContent())
    val state: StateFlow<SettingsContent> = _state

    fun initialiseVar(semesterToClassToAttendance: SemesterToClassToAttendance?) {
        if (semesterToClassToAttendance != null) {
            val classEntities =
                semesterToClassToAttendance.classToAttendance.map { it?.classEntity }
            var attend = 0
            var total = 0
            var cancel = 0
            if (classEntities.isNotEmpty()) {
                classEntities.forEach {
                    attend += it?.present ?: 0
                    total += it?.present?.plus(it.absent) ?: 0
                    cancel += it?.cancel ?: 0
                }
            }
            val totalAttendance = try {
                attend.times(100).div(total)
            } catch (e: Exception) {
                0
            }
            LocalData.setInt(
                LocalData.CURRENT_SEMESTER_ID,
                semesterToClassToAttendance.semesterEntity.id
            )
            _state.update {
                it.copy(
                    currentSemester = semesterToClassToAttendance.semesterEntity,
                    totalAttendance = totalAttendance,
                    totalAbsent = total - attend,
                    totalPresent = attend,
                    totalCancel = cancel
                )
            }
        }
    }

    private suspend fun deleteSem() {
        semesterRepository.deleteSemester(_state.value.currentSemester)
    }

    private suspend fun deleteClassIncludingSem() {
        classRepository.deleteClassCorrespondingSemId(_state.value.currentSemester.id)
    }

    private suspend fun deleteAttendanceIncludingSem() {
        attendanceRepository.deleteAttendanceCorrespondingSemId(_state.value.currentSemester.id)
    }

    fun onClickSettingEvent(settingEvent: SettingEvent) {
        when (settingEvent) {
            SettingEvent.HideUpdateSemesterDialog -> _state.update { it.copy(showUpdateSemDialog = false) }
            SettingEvent.ShowUpdateSemesterDialog -> _state.update { it.copy(showUpdateSemDialog = true) }
            is SettingEvent.UpdateSem -> {
                updateSem(state.value.currentSemester.copy(semesterName = settingEvent.semName))
            }

            SettingEvent.HideDeleteSemDialog -> _state.update { it.copy(showDeleteSemDialog = false) }
            SettingEvent.ShowDeleteSemDialog -> _state.update { it.copy(showDeleteSemDialog = true) }
            SettingEvent.DeleteSemester -> {
                viewModelScope.launch {
                    deleteSem()
                    deleteAttendanceIncludingSem()
                    deleteClassIncludingSem()
                }
            }
        }
    }

    private fun updateSem(semesterEntity: SemesterEntity) {
        viewModelScope.launch {
            semesterRepository.upsertSemester(semesterEntity)
        }
    }

}


data class SettingsContent(
    val currentSemester: SemesterEntity = SemesterEntity(0, "", "", emptyList(), LocalDate.now()),
    val totalAttendance: Int = 0,
    val totalPresent: Int = 0,
    val totalAbsent: Int = 0,
    val totalCancel: Int = 0,
    val showUpdateSemDialog: Boolean = false,
    val showDeleteSemDialog: Boolean = false
)

sealed interface SettingEvent {
    data object ShowUpdateSemesterDialog : SettingEvent
    data object HideUpdateSemesterDialog : SettingEvent
    data class UpdateSem(val semName: String) : SettingEvent
    data object ShowDeleteSemDialog : SettingEvent
    data object HideDeleteSemDialog : SettingEvent
    data object DeleteSemester : SettingEvent
}