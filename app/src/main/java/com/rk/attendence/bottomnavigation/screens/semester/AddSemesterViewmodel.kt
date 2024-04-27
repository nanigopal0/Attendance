package com.rk.attendence.bottomnavigation.screens.semester

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.repository.SemesterRepository
import com.rk.attendence.sharedpref.LocalData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddSemesterViewmodel(
    private val semesterRepository: SemesterRepository = SingletonDBConnection.semesterRepo,
) : ViewModel() {

    private var _state = MutableStateFlow(AddSemesterContent())
    val state: StateFlow<AddSemesterContent> = _state
    private val selectedDayList = mutableListOf<String>()

    private fun validateAddBtn() {
        _state.update { it.copy(atLeastOneChecked = state.value.semName.isNotEmpty() && selectedDayList.isNotEmpty()) }
    }

    private fun insertSemester() {
        viewModelScope.launch {
            val semId = LocalData.getInt(LocalData.SEMESTER_ID)
            semesterRepository.upsertSemester(
                SemesterEntity(
                    id = semId + 1,
                    studentName = LocalData.getString(LocalData.NAME),
                    semesterName = state.value.semName,
                    days = selectedDayList,
                    dateCreation = LocalDate.now()
                )
            )
            LocalData.setInt(LocalData.SEMESTER_ID, semId + 1)
            LocalData.setInt(LocalData.CURRENT_SEMESTER_ID, semId + 1)
        }
    }

    fun onClickEventFunction(addSemesterEvent: AddSemesterEvent) {
        when (addSemesterEvent) {
            AddSemesterEvent.Add -> {
                insertSemester()
            }

            is AddSemesterEvent.CheckedDay -> {
                if (addSemesterEvent.isChecked) {
                    selectedDayList.add(addSemesterEvent.day)
                } else {
                    selectedDayList.remove(addSemesterEvent.day)
                }
                validateAddBtn()
            }

            is AddSemesterEvent.SemName -> {
                _state.update { it.copy(semName = addSemesterEvent.name) }
                validateAddBtn()
            }

            else -> {}
        }
    }
}


data class AddSemesterContent(
    val semName: String = "",
    val atLeastOneChecked: Boolean = false
)

sealed interface AddSemesterEvent {
    data object Add : AddSemesterEvent
    data class SemName(val name: String) : AddSemesterEvent
    data class CheckedDay(val day: String, val isChecked: Boolean) : AddSemesterEvent
}