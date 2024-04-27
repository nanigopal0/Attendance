package com.rk.attendence.bottomnavigation.screens.details

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rk.attendence.alarm.AlarmScheduleForSubject
import com.rk.attendence.bottomnavigation.screens.shared.SharedViewmodel
import com.rk.attendence.calendar.CalendarUiState
import com.rk.attendence.calendar.CalendarViewModel
import com.rk.attendence.calendar.CalendarWidget
import com.rk.attendence.calendar.util.DateUtil
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DetailsScreen(sharedViewmodel: SharedViewmodel) {

    val detailsViewmodel: DetailsViewmodel = viewModel()
    val calendarViewModel: CalendarViewModel = viewModel()
    val onClickEvent = detailsViewmodel::onClickEventFunction
    val state = detailsViewmodel.state.collectAsState()
    var expandLazyListItem by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
//    val configuration = LocalConfiguration.current
//    val maxHeight = configuration.screenHeightDp - 64 - 80
    val uiState by calendarViewModel.uiState.collectAsState()
    val sharedState = sharedViewmodel.state.collectAsState()

    LaunchedEffect(key1 = sharedState.value.semesterToClassToAttendance) {
        detailsViewmodel.initialiseVar(
            semesterEntity = sharedViewmodel.state.value.semesterEntity,
            semesterToClassToAttendance = sharedViewmodel.state.value.semesterToClassToAttendance
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CalendarWidget(
            days = DateUtil.daysOfWeek,
            yearMonth = uiState.yearMonth,
            dates = uiState.dates,
            onPreviousMonthButtonClicked = { prevMonth ->
                calendarViewModel.toPreviousMonth(prevMonth)
            },
            onNextMonthButtonClicked = { nextMonth ->
                calendarViewModel.toNextMonth(nextMonth)
            },
            onDateClickListener = {
                if (it.dayOfMonth != "") {
                    calendarViewModel.setSelected(CalendarUiState.Date(it.dayOfMonth, false))
                    detailsViewmodel.getAttendanceInDay(
                        uiState.yearMonth.year,
                        uiState.yearMonth.month,
                        it.dayOfMonth.toInt()
                    )
                }
            }
        )
        state.value.showAllClassInParticularDay.forEach { classContent ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(10.dp),
                        color = Color.LightGray
                    )
                    .clickable {
                        expandLazyListItem = if (expandLazyListItem.isEmpty())
                            classContent.classEntity.className
                        else
                            ""
                    }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .padding(15.dp)
                    ) {
                        val isNotChangeAttendance = remember {
                            mutableStateOf(classContent.isPresent || classContent.isAbsent || classContent.isCancel)
                        }
                        Row {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = classContent.classEntity.className,
//                                    fontSize = 20.sp,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                classContent.classEntity.classStartTime[detailsViewmodel.selectedDate.dayOfWeek.getDisplayName(
                                    TextStyle.FULL,
                                    Locale.getDefault()
                                )]?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                                if (expandLazyListItem == classContent.classEntity.className) {

                                    Row {
                                        if (classContent.isPresent) Text(
                                            text = "Present",
                                            color = Color(0xFF1CCF27),
                                            modifier = Modifier.align(Alignment.CenterVertically),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        else if (classContent.isAbsent)
                                            Text(
                                                text = "Absent",
                                                color = Color.Red,
                                                modifier = Modifier.align(
                                                    Alignment.CenterVertically
                                                ),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        else if (classContent.isCancel)
                                            Text(
                                                text = "Cancelled",
                                                color = Color.Red,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.align(
                                                    Alignment.CenterVertically
                                                ), style = MaterialTheme.typography.titleMedium
                                            )
                                        TextButton(
                                            onClick = {
                                                isNotChangeAttendance.value = false
                                                if (detailsViewmodel.isExceedCurrentDate()) {
                                                    onClickEvent(
                                                        DetailsEvent.DisableCancel(
                                                            classContent
                                                        )
                                                    )
                                                }
                                            },
                                            enabled = isNotChangeAttendance.value,
                                            modifier = Modifier
                                                .padding(start = 10.dp)
                                                .align(Alignment.CenterVertically)
                                        ) {
                                            Text(text = "Change")
                                        }
                                    }
                                }
                            }
                            if (expandLazyListItem != classContent.classEntity.className) {
                                if (classContent.isPresent)
                                    Text(
                                        text = "Present",
                                        color = Color(0xFF1CCF27),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                else if (classContent.isAbsent)
                                    Text(
                                        text = "Absent",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                else if (classContent.isCancel)
                                    Text(
                                        text = "Cancelled",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                            } else {
                                IconButton(
                                    onClick = {
                                        onClickEvent(DetailsEvent.Present(classContent))
                                        AlarmScheduleForSubject(context).cancelAlarm(
                                            classContent.classEntity.id
                                        )
                                        isNotChangeAttendance.value = true
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(end = 2.dp),
                                    enabled = !isNotChangeAttendance.value && !detailsViewmodel.isExceedCurrentDate()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.TaskAlt,
                                        contentDescription = "Present",
                                        tint = if (isNotChangeAttendance.value) Color(
                                            0xCD27972E
                                        ) else Color(0xFF11A81B)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onClickEvent(DetailsEvent.Absent(classContent))
                                        isNotChangeAttendance.value = true
                                        AlarmScheduleForSubject(context).cancelAlarm(
                                            classContent.classEntity.id
                                        )
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(end = 2.dp),
                                    enabled = !isNotChangeAttendance.value && !detailsViewmodel.isExceedCurrentDate()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Absent",
                                        tint = if (isNotChangeAttendance.value) Color(
                                            0xC1A23028
                                        ) else Color.Red
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onClickEvent(DetailsEvent.Cancel(classContent))
                                        AlarmScheduleForSubject(context).cancelAlarm(
                                            classContent.classEntity.id
                                        )
                                        isNotChangeAttendance.value = true
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically),
                                    enabled = !isNotChangeAttendance.value
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Cancel,
                                        contentDescription = "Cancel",
                                        tint = if (isNotChangeAttendance.value) Color(
                                            0xFF80221C
                                        ) else Color.Red
                                    )
                                }

                            }

                        }
                    }
                }
            }
        }
    }
}
