package com.rk.attendence.bottomnavigation.screens.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.repository.ClassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditClassViewmodel(
    private val classRepository: ClassRepository = SingletonDBConnection.classRepo,
) : ViewModel() {
    private var _state = MutableStateFlow(EditClassContent())
    val state: StateFlow<EditClassContent> = _state

    fun initialiseVar(classEntity: ClassEntity, semesterEntity: SemesterEntity) {
        val map = classEntity.classStartTime.toMutableMap()
        semesterEntity.days.forEach {
            if (!map.contains(it)) {
                map[it] = "10:00 AM"
            }
        }
        _state.value = EditClassContent(
            classEntity = classEntity,
            className = classEntity.className,
            previousClassStartTimeMap = map,
            currentClassStartTime = classEntity.classStartTime
        )
    }


    fun onEvent(event: EditClassEvent) {
        when (event) {
            is EditClassEvent.ClassNameChange -> {
                _state.value = _state.value.copy(
                    className = event.name.trim(),
                    atLeastOneCheck = state.value.classEntity.className != event.name.trim()
                )
            }

            EditClassEvent.EditClass -> {
                val classEntity = state.value.classEntity.copy(
                    className = state.value.className,
                    classStartTime = state.value.currentClassStartTime
                )
                viewModelScope.launch {
                    classRepository.upsertClass(classEntity)
                }

            }

            is EditClassEvent.OnCheckChange -> {
                if (event.isChecked) {
                    val map = state.value.currentClassStartTime.toMutableMap()
                    map[event.day] = event.time
                    _state.update {
                        it.copy(
                            currentClassStartTime = map,
                            atLeastOneCheck = map != state.value.classEntity.classStartTime
                        )
                    }
                } else {
                    val map = state.value.currentClassStartTime.toMutableMap()
                    map.remove(event.day)
                    _state.update {
                        it.copy(
                            currentClassStartTime = map,
                            atLeastOneCheck = map.isNotEmpty() && state.value.classEntity.classStartTime != map
                        )
                    }
                }

            }
        }
    }
}

data class EditClassContent(
    val classEntity: ClassEntity = ClassEntity(0, 0, "", emptyMap(), 0, 0, 0),
    val className: String = "",
    val currentClassStartTime: Map<String, String> = emptyMap(),
    val previousClassStartTimeMap: Map<String, String> = emptyMap(),
    val atLeastOneCheck: Boolean = false,
)

sealed interface EditClassEvent {
    data class ClassNameChange(val name: String) : EditClassEvent
    data object EditClass : EditClassEvent
    data class OnCheckChange(val day: String, val time: String, val isChecked: Boolean) :
        EditClassEvent
}