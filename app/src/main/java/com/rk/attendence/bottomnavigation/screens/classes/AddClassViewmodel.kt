package com.rk.attendence.bottomnavigation.screens.classes

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rk.attendence.alarm.AlarmScheduleForSubject
import com.rk.attendence.alarm.AlarmSchedulerImplement
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.repository.ClassRepository
import com.rk.attendence.database.repository.SemesterRepository
import com.rk.attendence.notification.LocalDataInNotification
import com.rk.attendence.sharedpref.LocalData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

class AddClassViewmodel(
    private val semesterRepository: SemesterRepository = SingletonDBConnection.semesterRepo,
    private val classRepository: ClassRepository = SingletonDBConnection.classRepo
) : ViewModel() {
    private var _state = MutableStateFlow(AddClassContent())
    val state: StateFlow<AddClassContent> = _state
    private lateinit var job1: Job
    private val dayTimeMap: MutableMap<String, String> = mutableMapOf()
    // Map of the particular day and corresponding dayId
    // To get dayId concat semId with days number that is
    // Sun -> 1, Mon -> 2, Tue -> 3, Wed -> 4, Thu -> 5, Fri -> 6, Sat -> 7

    init {
        getAllSemester()
    }

    private fun getSelectedSem() {
        val sem =
            state.value.semesterList.find { it.id == LocalData.getInt(LocalData.CURRENT_SEMESTER_ID) }
        if (sem != null)
            _state.update { it.copy(selectedSemester = sem, dbDayList = sem.days) }
    }

    override fun onCleared() {
        super.onCleared()
        job1.cancel()
    }

    private fun getAllSemester() {
        job1 = viewModelScope.launch {
            semesterRepository.getAllSemester().collect { semList ->
                _state.update { it.copy(semesterList = semList) }
                getSelectedSem()
            }
        }
    }

    private suspend fun insertClass() {
        var classId = LocalData.getInt(LocalData.CLASS_ID)
        classRepository.upsertClass(
            ClassEntity(
                id = ++classId,
                semesterId = LocalData.getInt(LocalData.CURRENT_SEMESTER_ID),
                classStartTime = dayTimeMap.toMap(),
                className = state.value.className,
                present = 0,
                absent = 0,
                cancel = 0
            )
        )
        LocalData.setInt(LocalData.CLASS_ID, classId)
    }

    private fun validateAddBtn() {
        _state.update { it.copy(atLeastOneChecked = dayTimeMap.isNotEmpty() && state.value.className.isNotEmpty()) }
    }

    fun returnCallbackRequestPermission(context: Context): (Boolean) -> Unit {
        return { it ->
            if (it) {
                LocalData.setBoolean(LocalData.NOTIFICATION, true)
                scheduledAlarm(context)
                AlarmScheduleForSubject(context).createNotificationChannel(context)
                println("Permission granted")
            } else {
                println("Permission not granted")
            }
        }
    }

    fun scheduledAlarm(context: Context) {
        val currentDay =
            LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val time = dayTimeMap[currentDay]
        if (time != null) {
//            println("time is $time")
//            val calendar = Calendar.getInstance().apply {
//                timeInMillis = System.currentTimeMillis()
//                set(Calendar.HOUR_OF_DAY, time.substring(0, 2).toInt())
//                set(Calendar.MINUTE, time.substring(3, 5).toInt())
//                set(Calendar.SECOND, 0)
//            }
//            println("add class viewmodel" + calendar.time)
//            println("add class viewmodel" + calendar.timeInMillis)
//            AlarmScheduleForSubject(context).alarmCreate(
//                classId = LocalData.getInt(LocalData.CLASS_ID) + 1,
//                time = calendar.timeInMillis
//            )
            //Scheduled alarm after next day
//            val calendar = Calendar.getInstance().apply {
//                set(Calendar.HOUR_OF_DAY, 0)
//                set(Calendar.MINUTE, 0)
//            }
            LocalData.setBoolean(LocalData.BROADCAST_CLASS, false)
            val alarm = AlarmSchedulerImplement(context as Activity)
            alarm.schedule(Calendar.getInstance().timeInMillis)
        }
    }

    private fun setDataInLocalStorage() {
        var classId = LocalData.getInt(LocalData.CLASS_ID)
        val localDataInNotification = LocalDataInNotification(
            ClassEntity(
                id = ++classId,
                semesterId = LocalData.getInt(LocalData.CURRENT_SEMESTER_ID),
                classStartTime = dayTimeMap.toMap(),
                className = state.value.className,
                present = 0,
                absent = 0,
                cancel = 0
            ), false
        )
        val data = Gson().toJson(localDataInNotification)
        LocalData.setString(LocalData.TODAY_CLASS, data)
    }

    fun onClickEventFunction(onClickEvent: OnClickEvent) {
        when (onClickEvent) {
            is OnClickEvent.Add -> {
                viewModelScope.launch {
                    setDataInLocalStorage()
                    insertClass()
                    onClickEvent.onClick
                }
            }

            is OnClickEvent.Checkbox -> {
                when (onClickEvent.day) {
                    Days.MONDAY -> {
                        if (onClickEvent.isChecked)
                            dayTimeMap[Days.MONDAY] = onClickEvent.time
                        else
                            dayTimeMap.remove(Days.MONDAY)
                    }

                    Days.TUESDAY -> {
                        if (onClickEvent.isChecked)
                            dayTimeMap[Days.TUESDAY] = onClickEvent.time
                        else dayTimeMap.remove(Days.TUESDAY)
                    }

                    Days.WEDNESDAY -> {
                        if (onClickEvent.isChecked)
                            dayTimeMap[Days.WEDNESDAY] = onClickEvent.time
                        else
                            dayTimeMap.remove(Days.WEDNESDAY)
                    }

                    Days.THURSDAY -> {
                        if (onClickEvent.isChecked)
                            dayTimeMap[Days.THURSDAY] = onClickEvent.time
                        else
                            dayTimeMap.remove(Days.THURSDAY)
                    }

                    Days.FRIDAY -> {
                        if (onClickEvent.isChecked)
                            dayTimeMap[Days.FRIDAY] = onClickEvent.time
                        else
                            dayTimeMap.remove(Days.FRIDAY)
                    }

                    Days.SATURDAY -> {
                        if (onClickEvent.isChecked)
                            dayTimeMap[Days.SATURDAY] = onClickEvent.time
                        else
                            dayTimeMap.remove(Days.SATURDAY)
                    }

                    Days.SUNDAY -> {
                        if (onClickEvent.isChecked)
                            dayTimeMap[Days.SUNDAY] = onClickEvent.time
                        else
                            dayTimeMap.remove(Days.SUNDAY)
                    }
                }
                validateAddBtn()
            }

            is OnClickEvent.ClassName -> {
                _state.update { it.copy(className = onClickEvent.name) }
                validateAddBtn()
            }

            OnClickEvent.CompressDropDown -> {
                _state.update { it.copy(expandDropDown = false) }
            }

            OnClickEvent.ExpandDropDown -> {
                _state.update { it.copy(expandDropDown = !state.value.expandDropDown) }
            }

            is OnClickEvent.SelectSemester -> {
                _state.update { it.copy(selectedSemester = onClickEvent.semesterEntity) }
                LocalData.setInt(LocalData.CURRENT_SEMESTER_ID, onClickEvent.semesterEntity.id)
                getSelectedSem()
            }
        }
    }

}

data class AddClassContent(
    val expandDropDown: Boolean = false,
    val className: String = "",
    val classStartTime: String = "",
    val atLeastOneChecked: Boolean = false,
    val dbDayList: List<String> = emptyList(),
    val semesterList: List<SemesterEntity> = emptyList(),
    val selectedSemester: SemesterEntity = SemesterEntity(
        id = 0,
        studentName = "",
        semesterName = "",
        days = emptyList(),
        dateCreation = LocalDate.now()
    )
)

object Days {
    const val MONDAY = "Monday"
    const val TUESDAY = "Tuesday"
    const val WEDNESDAY = "Wednesday"
    const val THURSDAY = "Thursday"
    const val FRIDAY = "Friday"
    const val SATURDAY = "Saturday"
    const val SUNDAY = "Sunday"
}

sealed interface OnClickEvent {
    data object ExpandDropDown : OnClickEvent
    data object CompressDropDown : OnClickEvent
    data class SelectSemester(val semesterEntity: SemesterEntity) : OnClickEvent
    data class ClassName(val name: String) : OnClickEvent
    data class Checkbox(val day: String, val time: String, val isChecked: Boolean) : OnClickEvent
    data class Add(val onClick: Unit) : OnClickEvent
}