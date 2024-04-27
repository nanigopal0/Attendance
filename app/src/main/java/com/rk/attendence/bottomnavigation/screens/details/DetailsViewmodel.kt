package com.rk.attendence.bottomnavigation.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.database.entity.AttendanceEntity
import com.rk.attendence.database.entity.ClassEntity
import com.rk.attendence.database.entity.SemesterEntity
import com.rk.attendence.database.relations.ClassToAttendance
import com.rk.attendence.database.relations.SemesterToClassToAttendance
import com.rk.attendence.database.repository.AttendanceRepository
import com.rk.attendence.database.repository.ClassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

class DetailsViewmodel(
    private val attendanceRepository: AttendanceRepository = SingletonDBConnection.attendanceRepo,
    private val classRepository: ClassRepository = SingletonDBConnection.classRepo
) : ViewModel() {


    private var _state = MutableStateFlow(DetailsContent())
    val state: StateFlow<DetailsContent> = _state
    private val nowLocalDate = LocalDate.now()
    var selectedDate: LocalDate = LocalDate.now()       //Name of the day of the selected date
    private var allClassesSeparateDay: Map<String, List<ClassToAttendance>> = emptyMap()

    //Contain all classes in everyday here string is day
    private var currentSemester = SemesterEntity(
        0, "", "", emptyList(), LocalDate.now()
    )
    private lateinit var semToClassToAttend: SemesterToClassToAttendance
//    private val currentDay =
//        LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())


    fun initialiseVar(
        semesterEntity: SemesterEntity,
        semesterToClassToAttendance: SemesterToClassToAttendance?
    ) {
        currentSemester = semesterEntity
        semToClassToAttend = semesterToClassToAttendance ?: SemesterToClassToAttendance(
            SemesterEntity(0, "", "", emptyList(), LocalDate.now()), emptyList()
        )
        getAllAttendance()
        getAllClasses()
    }


    private fun getAllClasses() {
        val list: List<ClassEntity> = semToClassToAttend.classToAttendance.map { it?.classEntity!! }
        _state.update { it.copy(allClasses = list) }

    }

    fun getAttendanceInDay(year: Int, month: Month, day: Int) {
        val localDate = LocalDate.of(year, month, day)
        val id = localDate.toEpochDay()
        val dayName = localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        selectedDate = localDate
        val list = mutableListOf<AllClassShow>()
        if (localDate >= currentSemester.dateCreation) {
            allClassesSeparateDay[dayName]?.forEach { cta ->
                val attendanceEntity =
                    cta.attendanceList.find { a -> a?.id == "$id${cta.classEntity.id}".toLong() }

                list.add(
                    AllClassShow(
                        classEntity = cta.classEntity,
                        isPresent = attendanceEntity?.present ?: false,
                        isAbsent = attendanceEntity?.absent ?: false,
                        isCancel = attendanceEntity?.cancel ?: false
                    )
                )
            }
        }

        _state.update { it.copy(showAllClassInParticularDay = list) }

    }

    fun isExceedCurrentDate(): Boolean {
        return selectedDate > nowLocalDate
    }

    private fun getAllAttendance() {

        if (semToClassToAttend.classToAttendance.isNotEmpty()) {
            val day = mutableMapOf<String, List<ClassToAttendance>>()
            semToClassToAttend.classToAttendance.forEach { cl ->
                cl?.classEntity?.classStartTime?.keys?.forEach {
                    var list = mutableListOf<ClassToAttendance>()
                    if (day[it] != null)
                        list = day[it]?.toMutableList()!!
                    list.add(cl)
                    day[it] = list
                }
            }
            allClassesSeparateDay = day
            if (state.value.showAllClassInParticularDay.isEmpty())
                getAttendanceInDay(
                    nowLocalDate.year,
                    nowLocalDate.month,
                    nowLocalDate.dayOfMonth
                )
            else {
                getAttendanceInDay(
                    selectedDate.year,
                    selectedDate.month,
                    selectedDate.dayOfMonth
                )

            }
        }
    }

    private fun updateAttendanceAndClass(attendance: AttendanceEntity, classEntity: ClassEntity) {
        viewModelScope.launch {
            attendanceRepository.upsertAttendance(attendance)
            classRepository.upsertClass(classEntity)
        }
    }

    fun onClickEventFunction(detailsEvent: DetailsEvent) {
        when (detailsEvent) {
            DetailsEvent.AllClass -> {
                _state.update { it.copy(isAllClassShown = !state.value.isAllClassShown) }
            }

            DetailsEvent.TodayClass -> {
                _state.update { it.copy(isTodayClassShown = !state.value.isTodayClassShown) }
            }

            is DetailsEvent.Absent -> {
                if (!detailsEvent.classContent.isAbsent) {
                    val classId = detailsEvent.classContent.classEntity.id
                    val id = "${selectedDate.toEpochDay()}$classId".toLong()
                    val attendanceEntity = AttendanceEntity(
                        id = id,
                        date = selectedDate,
                        semesterId = currentSemester.id,
                        present = false,
                        absent = true,
                        cancel = false,
                        classId = classId
                    )
                    var present = 0
                    var cancel = 0
                    if (detailsEvent.classContent.isPresent)
                        present = detailsEvent.classContent.classEntity.present - 1
                    if (detailsEvent.classContent.isCancel)
                        cancel = detailsEvent.classContent.classEntity.cancel - 1
                    val absent = detailsEvent.classContent.classEntity.absent + 1
                    updateAttendanceAndClass(
                        attendanceEntity,
                        detailsEvent.classContent.classEntity.copy(
                            absent = absent,
                            present = present,
                            cancel = cancel
                        )
                    )
                }
            }

            is DetailsEvent.Present -> {
                if (!detailsEvent.classContent.isPresent) {     //Means that previous one may be absent or cancel
                    val classId = detailsEvent.classContent.classEntity.id
                    val id = "${selectedDate.toEpochDay()}$classId".toLong()
                    val attendanceEntity = AttendanceEntity(
                        id = id,
                        date = selectedDate,
                        semesterId = currentSemester.id,
                        present = true,
                        absent = false,
                        cancel = false,
                        classId = classId
                    )
                    var absent = 0
                    var cancel = 0
                    if (detailsEvent.classContent.isAbsent) absent =
                        detailsEvent.classContent.classEntity.absent - 1
                    if (detailsEvent.classContent.isCancel)
                        cancel = detailsEvent.classContent.classEntity.cancel - 1
                    val present = detailsEvent.classContent.classEntity.present + 1
                    updateAttendanceAndClass(
                        attendanceEntity,
                        detailsEvent.classContent.classEntity.copy(
                            absent = absent,
                            present = present,
                            cancel = cancel
                        )
                    )
                }
            }

            is DetailsEvent.Cancel -> {
                if (!detailsEvent.classContent.isCancel) {
                    val classId = detailsEvent.classContent.classEntity.id
                    val id = "${selectedDate.toEpochDay()}$classId".toLong()
                    val attendanceEntity = AttendanceEntity(
                        id = id,
                        date = selectedDate,
                        semesterId = currentSemester.id,
                        present = false,
                        absent = false,
                        cancel = true,
                        classId = classId
                    )
                    var absent = 0
                    var present = 0
                    if (detailsEvent.classContent.isAbsent)
                        absent = detailsEvent.classContent.classEntity.absent - 1
                    if (detailsEvent.classContent.isPresent)
                        present = detailsEvent.classContent.classEntity.present - 1
                    val cancel = detailsEvent.classContent.classEntity.cancel + 1
                    updateAttendanceAndClass(
                        attendanceEntity,
                        detailsEvent.classContent.classEntity.copy(
                            absent = absent,
                            present = present,
                            cancel = cancel
                        )
                    )
                }
            }

            is DetailsEvent.DisableCancel -> {
                val classId = detailsEvent.classContent.classEntity.id
                val id = "${selectedDate.toEpochDay()}$classId".toLong()
                val attendanceEntity = AttendanceEntity(
                    id = id,
                    date = selectedDate,
                    semesterId = currentSemester.id,
                    present = false,
                    absent = false,
                    cancel = false,
                    classId = classId
                )
                viewModelScope.launch {
                    attendanceRepository.upsertAttendance(attendanceEntity)
                }
            }
        }
    }
}

data class DetailsContent(
    val allClasses: List<ClassEntity> = emptyList(),
    val isAllClassShown: Boolean = false,
    val isTodayClassShown: Boolean = true,
    val showAllClassInParticularDay: List<AllClassShow> = emptyList()
)

data class AllClassShow(
    val classEntity: ClassEntity = ClassEntity(0, 0, "", emptyMap(), 0, 0, 0),
    val isPresent: Boolean = false,
    val isAbsent: Boolean = false,
    val isCancel: Boolean = false
)

sealed interface DetailsEvent {
    data object TodayClass : DetailsEvent
    data object AllClass : DetailsEvent
    data class Present(val classContent: AllClassShow) : DetailsEvent
    data class Absent(val classContent: AllClassShow) : DetailsEvent
    data class Cancel(val classContent: AllClassShow) : DetailsEvent
    data class DisableCancel(val classContent: AllClassShow) : DetailsEvent
}