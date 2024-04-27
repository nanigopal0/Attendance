package com.rk.attendence.bottomnavigation.screens.dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rk.attendence.alarm.AlarmScheduleForSubject
import com.rk.attendence.bottomnavigation.screens.shared.SharedViewmodel
import com.rk.attendence.notification.NotificationService
import com.rk.attendence.sharedpref.LocalData

@Composable
fun DashBoardScreen(sharedViewmodel: SharedViewmodel, function: (Int) -> Unit) {
    val dashBoardViewmodel: DashBoardViewmodel = viewModel()
    val state = sharedViewmodel.state.collectAsState()
    val onClickEvent = dashBoardViewmodel::onClickEventFunction
    val context = LocalContext.current
    val scroll = rememberLazyListState()
    var isFabVisible by remember {
        mutableStateOf(true)
    }
    var expandLazyListItem by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = state.value.semesterEntity) {
        dashBoardViewmodel.initiateCurrentSem(state.value.semesterEntity)
    }

    Scaffold(
        topBar = {
            Text(
                text = if (state.value.semesterEntity.semesterName.isEmpty())
                    "Hello ${LocalData.getString(LocalData.NAME)}"
                else "Hello ${state.value.semesterEntity.studentName}",   //Max character 16
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                maxLines = 1
            )
        },
        floatingActionButton = {
            if (isFabVisible) {
                FloatingActionButton(onClick = {
                    if (state.value.semesterEntity.semesterName.isEmpty()) { function(2) }
                    else { function(1) }
                }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add class")
                }
            }
        }
    ) {
        Box(
            modifier = Modifier.padding(
                start = 20.dp,
                top = it.calculateTopPadding() + 30.dp,
                end = 20.dp,
                bottom = it.calculateBottomPadding()
            )
        ) {
            LaunchedEffect(key1 = scroll) {
                snapshotFlow { scroll.firstVisibleItemIndex }.collect { index ->
                    isFabVisible = index == 0
                }
            }
            if (state.value.semesterEntity.semesterName.isEmpty()) {
                Text(
                    text = "Nothing to show here, please add semester and classes",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Text(
                    text = "Today's Classes in ${state.value.semesterEntity.semesterName}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                )
                if (state.value.todayClasses.isEmpty()) {
                    Text(
                        text = "You are not added any class, please add some class",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = it.calculateTopPadding())
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(5.dp),
                        state = scroll,
                        modifier = Modifier.padding(top = it.calculateTopPadding())
                    ) {
                        items(state.value.todayClasses) { classEntity ->
                            val isNotChangeAttendance = remember {
                                mutableStateOf(classEntity.isTodayAbsent || classEntity.isTodayPresent)
                            }
                            Spacer(modifier = Modifier.height(5.dp))
//                            if (expandLazyListItem != classEntity.classEntity.className) {
                            Row(modifier = Modifier
                                .border(
                                    1.dp,
                                    color =  if (expandLazyListItem == classEntity.classEntity.className) MaterialTheme.colorScheme.onSurface else Color.LightGray,
                                    RoundedCornerShape(if (expandLazyListItem == classEntity.classEntity.className) 10 else 20)
                                )
                                .clickable {
                                    expandLazyListItem = if (expandLazyListItem.isEmpty())
                                        classEntity.classEntity.className
                                    else ""
                                }
                                .padding(horizontal = 15.dp, vertical = 10.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = classEntity.classEntity.className,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Text(
                                        text = dashBoardViewmodel.getStartTime(classEntity.classEntity),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Normal
                                    )
//                                    if (expandLazyListItem == classEntity.classEntity.className) {
//                                        Row {
//                                            if (classEntity.isTodayPresent) Text(
//                                                text = "Present",
//                                                color = Color(0xFF1CCF27),
//                                                modifier = Modifier.align(Alignment.CenterVertically),
//                                                style = MaterialTheme.typography.titleMedium
//                                            )
//                                            else if (classEntity.isTodayAbsent)
//                                                Text(
//                                                    text = "Absent",
//                                                    color = Color.Red,
//                                                    modifier = Modifier.align(Alignment.CenterVertically),
//                                                    style = MaterialTheme.typography.titleMedium
//                                                )
//                                            else if (classEntity.isTodayCancel)
//                                                Text(
//                                                    text = "Cancelled",
//                                                    fontWeight = FontWeight.Medium,
//                                                    color = Color.Red,
//                                                    modifier = Modifier.align(Alignment.CenterVertically),
//                                                    style = MaterialTheme.typography.titleMedium
//                                                )
//
//                                            TextButton(
//                                                onClick = {
//                                                    isNotChangeAttendance.value = false
//                                                },
//                                                enabled = isNotChangeAttendance.value,
//                                                modifier = Modifier
//                                                    .padding(start = 10.dp)
//                                                    .align(Alignment.CenterVertically)
//                                            ) {
//                                                Text(text = "Change")
//                                            }
//
//                                        }
//                                    }
//                                    Box(modifier = Modifier.align(Alignment.CenterVertically)) {
//                                        CircularProgressIndicator(
//                                            progress = {
//                                                try {
//                                                    if (classEntity.present > 0 || classEntity.absent > 0)
//                                                        classEntity.present.toFloat() / (classEntity.present + classEntity.absent)
//                                                    else 0f
//                                                } catch (e: Exception) {
//                                                    0f
//                                                }
//                                            },
//                                            color = MaterialTheme.colorScheme.primary,
//                                            trackColor = MaterialTheme.colorScheme.primaryContainer,
//                                            modifier = Modifier.size(55.dp)
//                                        )
//                                        Text(
//                                            text = "${
//                                                try {
//                                                    classEntity.present * 100 / (classEntity.present + classEntity.absent)
//                                                } catch (e: Exception) {
//                                                    0
//                                                }
//                                            }% ",
//                                            modifier = Modifier.align(Alignment.Center),
//                                            style = MaterialTheme.typography.titleMedium
//                                        )
//                                    }


//                            if (expandLazyListItem == classEntity.classEntity.className) {
//                                val isNotChangeAttendance = remember {
//                                    mutableStateOf(classEntity.isTodayAbsent || classEntity.isTodayPresent)
//                                }
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .border(
//                                            width = 1.dp,
//                                            shape = RoundedCornerShape(10.dp),
//                                            color = MaterialTheme.colorScheme.onBackground
//                                        )
//                                        .clickable {
//                                            expandLazyListItem = ""
//                                        }
//                                ) {
//                                    Column(
//                                        modifier = Modifier
//                                            .padding(15.dp)
//                                            .weight(1f)
//                                    ) {
//
//                                            Text(
//                                                text = classEntity.classEntity.className,
//                                                style = MaterialTheme.typography.titleMedium
//                                            )
//
//                                        Text(text = dashBoardViewmodel.getStartTime(classEntity.classEntity),
//                                            style = MaterialTheme.typography.titleMedium,
//                                            fontWeight = FontWeight.Normal
//                                        )
                                    if (expandLazyListItem == classEntity.classEntity.className) {
                                        Row {
                                            if (classEntity.isTodayPresent) Text(
                                                text = "Present",
                                                color = Color(0xFF1CCF27),
                                                modifier = Modifier.align(Alignment.CenterVertically),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            else if (classEntity.isTodayAbsent)
                                                Text(
                                                    text = "Absent",
                                                    color = Color.Red,
                                                    modifier = Modifier.align(Alignment.CenterVertically),
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            else if (classEntity.isTodayCancel)
                                                Text(
                                                    text = "Cancelled",
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color.Red,
                                                    modifier = Modifier.align(Alignment.CenterVertically),
                                                    style = MaterialTheme.typography.titleMedium
                                                )

                                            TextButton(
                                                onClick = {
                                                    isNotChangeAttendance.value = false
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

                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                ) {
                                    Row(modifier = Modifier.align(Alignment.End)) {
                                        if (expandLazyListItem == classEntity.classEntity.className) {
                                            Text(
                                                text = "${classEntity.present} / ${classEntity.present + classEntity.absent}",
                                                modifier = Modifier
                                                    .align(Alignment.CenterVertically)
                                                    .padding(end = 4.dp),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                                            CircularProgressIndicator(
                                                progress = {
                                                    try {
                                                        if (classEntity.present > 0 || classEntity.absent > 0)
                                                            classEntity.present.toFloat() / (classEntity.present + classEntity.absent)
                                                        else 0f
                                                    } catch (e: Exception) {
                                                        println(0f)
                                                        0f
                                                    }
                                                },
                                                color = MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.primaryContainer,
                                                strokeCap = StrokeCap.Round,
                                                modifier = Modifier.size(60.dp)
                                            )
                                            Text(
                                                text = "${
                                                    try {
                                                        classEntity.present * 100 / (classEntity.present + classEntity.absent)
                                                    } catch (e: Exception) {
                                                        0
                                                    }
                                                }% ",
                                                modifier = Modifier.align(Alignment.Center),
                                            )
                                        }
                                    }
                                    if (expandLazyListItem == classEntity.classEntity.className) {
                                        Row(modifier = Modifier.padding(top = 3.dp)) {
                                            IconButton(
                                                onClick = {
                                                    onClickEvent(
                                                        DashboardEvent.Present(
                                                            classEntity
                                                        )
                                                    )
                                                    AlarmScheduleForSubject(context).cancelAlarm(
                                                        classEntity.classEntity.id
                                                    )
                                                    isNotChangeAttendance.value = true
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.CenterVertically)
                                                    .padding(end = 2.dp),
                                                enabled = !isNotChangeAttendance.value
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
                                                    onClickEvent(
                                                        DashboardEvent.Absent(
                                                            classEntity
                                                        )
                                                    )
                                                    AlarmScheduleForSubject(context).cancelAlarm(
                                                        classEntity.classEntity.id
                                                    )
                                                    isNotChangeAttendance.value = true
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.CenterVertically)
                                                    .padding(end = 2.dp),
                                                enabled = !isNotChangeAttendance.value
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
                                                    onClickEvent(
                                                        DashboardEvent.Cancel(
                                                            classEntity
                                                        )
                                                    )
                                                    AlarmScheduleForSubject(context).cancelAlarm(
                                                        classEntity.classEntity.id
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
        }
    }
}
