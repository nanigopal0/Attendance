package com.rk.attendence.bottomnavigation.screens.classes

import android.app.TimePickerDialog
import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rk.attendence.bottomnavigation.screens.setting.notification.checkNotificationPermission
import com.rk.attendence.bottomnavigation.screens.setting.notification.notificationRequest
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClassScreen(onClick: (Int) -> Unit) {
    println("Enter screen")
    val addClassViewmodel: AddClassViewmodel = viewModel()
    val onClickViewmodel = addClassViewmodel::onClickEventFunction
    val state = addClassViewmodel.state.collectAsState()
    val context = LocalContext.current
    val shouldShowNotificationReqDialog = remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Attendance",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = it.calculateTopPadding() + 30.dp,
                    bottom = it.calculateBottomPadding(),
                    start = it.calculateStartPadding(LayoutDirection.Ltr) + 20.dp,
                    end = it.calculateEndPadding(LayoutDirection.Rtl) + 20.dp
                )
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Semester ${state.value.selectedSemester.semesterName}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(end = 1.dp)
                )

                IconButton(
                    onClick = { onClickViewmodel(OnClickEvent.ExpandDropDown) },
                    modifier = Modifier,
                    enabled = state.value.semesterList.size > 1
                ) {
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = state.value.expandDropDown,
                    onDismissRequest = { onClickViewmodel(OnClickEvent.CompressDropDown) }) {
                    state.value.semesterList.forEach {
                        DropdownMenuItem(text = { Text(text = it.semesterName) }, onClick = {
                            onClickViewmodel(OnClickEvent.SelectSemester(it))
                            onClickViewmodel(OnClickEvent.CompressDropDown)
                        })
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {

                TextButton(onClick = { onClick(1) }) {
                    Text(text = "Edit Sem")
                }

                TextButton(onClick = { onClick(2) }) {
                    Text(text = "Add a new semester")
                }
            }

            Text(
                text = "Add Class",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .align(Alignment.CenterHorizontally)
            )

            TextField(
                value = state.value.className,
                onValueChange = { change -> onClickViewmodel(OnClickEvent.ClassName(change)) },
                singleLine = true,
                placeholder = { Text(text = "Enter class name") },
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .align(Alignment.CenterHorizontally)
                    .border(
                        width = 1.dp, color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(shape = RoundedCornerShape(8.dp))
            )
            Text(
                text = "Class present at",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 25.dp, bottom = 15.dp)
            )

            LazyColumn {
                items(state.value.dbDayList) { dbDay ->
                    CheckBoxDay(text = dbDay, onClickViewmodel)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !checkNotificationPermission(
                    context
                )
            ) {
                val notificationReq =
                    notificationRequest(addClassViewmodel.returnCallbackRequestPermission(context))
                if (shouldShowNotificationReqDialog.value) {
                    AlertDialog(onDismissRequest = {
                        shouldShowNotificationReqDialog.value = false
                    }, text = {
                        Text(text = "Please allow the notification permission so that app can notify at the time of attendance")
                    },
                        confirmButton = {
                            Button(onClick = {
                                notificationReq.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                shouldShowNotificationReqDialog.value = false
                                addClassViewmodel.scheduledAlarm(context)
                                onClickViewmodel(OnClickEvent.Add(onClick(3)))
                            }) {
                                Text(text = "Request permission")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { shouldShowNotificationReqDialog.value = false }) {
                                Text(text = "Cancel")
                            }
                        })
                }
            }
            Button(
                onClick = {
                    shouldShowNotificationReqDialog.value = true
                    if (checkNotificationPermission(context)) {
                        addClassViewmodel.scheduledAlarm(context)
                        onClickViewmodel(OnClickEvent.Add(onClick(3)))
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = state.value.atLeastOneChecked
            ) {
                Text(
                    text = "Add",
                    modifier = Modifier
                )
            }
        }


    }
}

@Composable
fun CheckBoxDay(
    text: String,
    onClickViewmodel: (onClickEvent: OnClickEvent) -> Unit
) {
    val context = LocalContext.current
    var isCheck by rememberSaveable {
        mutableStateOf(false)
    }
    val selectedTime = Calendar.getInstance()
    var time by remember {
        mutableStateOf("10:00 AM")
    }

    Row {
        Checkbox(
            checked = isCheck,
            onCheckedChange = {
                isCheck = it
                onClickViewmodel(OnClickEvent.Checkbox(text, time, isCheck))
            }
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        val timePicker = TimePickerDialog(
            context, { _, selectedHour: Int, selectedMinute: Int ->
                selectedTime.apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }
                time = if (selectedTime.get(Calendar.AM_PM) == Calendar.AM)
                    "${String.format("%02d", selectedHour)}:${
                        String.format("%02d", selectedMinute)
                    } AM"
                else "${String.format("%02d", selectedHour)}:${
                    String.format("%02d", selectedMinute)
                } PM"
                onClickViewmodel(OnClickEvent.Checkbox(text, time, isCheck))
            }, selectedTime.get(Calendar.HOUR_OF_DAY), selectedTime.get(Calendar.MINUTE), false
        )

        TextButton(onClick = { timePicker.show() }) {
            Text(text = time)
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AddClassScreenPreview() {
    AddClassScreen {}
}