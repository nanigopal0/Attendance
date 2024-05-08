package com.rk.attendence.bottomnavigation.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.attendence.bottomnavigation.screens.dashboard.ClassContent
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.relations.SemesterToClassToAttendance
import com.rk.attendence.database.repository.AttendanceRepository
import com.rk.attendence.database.repository.ClassRepository
import com.rk.attendence.database.repository.SemesterRepository
import com.rk.attendence.sharedpref.LocalData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class SharedViewmodel(
    private val semesterRepository: SemesterRepository = SingletonDBConnection.semesterRepo,
    private val attendanceRepository: AttendanceRepository = SingletonDBConnection.attendanceRepo,
    private val classRepository: ClassRepository = SingletonDBConnection.classRepo,
) : ViewModel() {

    private var _state = MutableStateFlow(SharedViewmodelContent())
    val state = _state
    private lateinit var job1: Job
    private lateinit var job2: Job
    private val currentDay =
        LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    var editClassEntity: ClassEntity = ClassEntity(0, 0, "", emptyMap(), 0, 0, 0)

    init {
        getAllSemester()
    }

    override fun onCleared() {
        super.onCleared()
        job1.cancel()
        job2.cancel()
    }


    fun getSemToClassToAttend(semId: Int) {
        try {
            job2.cancel()
        } catch (e: Exception) {
            println("Uninitialised property")
        }
        job2 = viewModelScope.launch {
            semesterRepository.getAllClassAttend(semId)
                .collectLatest { semClassAttend ->
                    _state.update {
                        it.copy(
                            semesterToClassToAttendance = semClassAttend,
                            semesterEntity = semClassAttend?.semesterEntity ?: SemesterEntity(
                                0,
                                "",
                                "",
                                emptyList(),
                                LocalDate.now()
                            )
                        )
                    }
                    println("getSemToClassToAttend collected")
                    getTodayClass()
                }
        }

    }

    private fun getTodayClass() {
        val list = mutableListOf<ClassContent>()
        state.value.semesterToClassToAttendance?.classToAttendance?.forEach { classToAttendance ->
            if (classToAttendance?.classEntity?.classStartTime?.keys?.contains(currentDay) == true) {
                val todayAttendance = classToAttendance.attendanceList.find {
                    it?.id == "${
                        LocalDate.now().toEpochDay()
                    }${classToAttendance.classEntity.id}".toLong()
                }
                list.add(
                    ClassContent(
                        classEntity = classToAttendance.classEntity,
//                        present = classToAttendance.classEntity.present,
//                        absent = classToAttendance.classEntity.absent,
                        isTodayPresent = todayAttendance?.present ?: false,
                        isTodayAbsent = todayAttendance?.absent ?: false,
                        isTodayCancel = todayAttendance?.cancel ?: false
                    )
                )
            }
        }
        _state.update { it.copy(todayClasses = list) }
    }


    private fun getAllSemester() {
        job1 = viewModelScope.launch {
            semesterRepository.getAllSemester().collectLatest { sem ->
                _state.update { it.copy(semesterList = sem) }
                println("getAllSemester collected")
                getSemToClassToAttend(LocalData.getInt(LocalData.CURRENT_SEMESTER_ID))
//                getCurrentSemester()
            }
        }
    }

    fun deleteClassStaffs(classEntity: ClassEntity) {
        viewModelScope.launch {
            classRepository.deleteClass(classEntity)
            attendanceRepository.deleteAttendanceCorrespondingClass(classEntity.id)
            val classIds =
                state.value.semesterToClassToAttendance?.classToAttendance?.map { it?.classEntity?.id }
                    ?.sortedBy { it }
            LocalData.setInt(LocalData.CLASS_ID, classIds?.lastOrNull() ?: 0)
        }
    }

//    fun getCurrentSemester() {
//        if (state.value.semesterList.isNotEmpty())
//            _state.update {
//                it.copy(semesterEntity = state.value.semesterList.find { sem ->
//                    sem.id == LocalData.getInt(
//                        LocalData.CURRENT_SEMESTER_ID
//                    )
//                } ?: SemesterEntity(0, "", "", emptyList(), LocalDate.now()))
//            }
//    }


}

data class SharedViewmodelContent(
    val semesterEntity: SemesterEntity = SemesterEntity(0, "", "", emptyList(), LocalDate.now()),
    val semesterList: List<SemesterEntity> = emptyList(),
    val todayClasses: List<ClassContent> = emptyList(),
    val semesterToClassToAttendance: SemesterToClassToAttendance? = null,
)