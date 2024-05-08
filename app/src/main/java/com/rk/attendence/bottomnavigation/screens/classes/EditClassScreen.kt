package com.rk.attendence.bottomnavigation.screens.classes

import android.app.TimePickerDialog
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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rk.attendence.bottomnavigation.screens.shared.SharedViewmodel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClassScreen(sharedViewmodel: SharedViewmodel, onClick: () -> Unit) {

    val editClassViewmodel: EditClassViewmodel = viewModel()
    val state = editClassViewmodel.state.collectAsState()
    val onClickEvent = editClassViewmodel::onEvent
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        editClassViewmodel.initialiseVar(
            sharedViewmodel.editClassEntity,
            sharedViewmodel.state.value.semesterEntity
        )
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
                onValueChange = { change -> onClickEvent(EditClassEvent.ClassNameChange(change)) },
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
                items(state.value.previousClassStartTimeMap.toList()) { dbDay ->

                    var isCheck by rememberSaveable {
                        mutableStateOf(state.value.classEntity.classStartTime[dbDay.first]?.isNotEmpty() == true)
                    }
                    val selectedTime = Calendar.getInstance()
                    var time by rememberSaveable {
                        mutableStateOf(dbDay.second)
                    }
                    Row {
                        Checkbox(
                            checked = isCheck,
                            onCheckedChange = { check ->
                                isCheck = check
                                onClickEvent(
                                    EditClassEvent.OnCheckChange(
                                        dbDay.first,
                                        time,
                                        isCheck
                                    )
                                )
                            }
                        )

                        Text(
                            text = dbDay.first,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        val timePicker = TimePickerDialog(
                            context,
                            { _, selectedHour: Int, selectedMinute: Int ->
                                selectedTime.apply {
                                    set(Calendar.HOUR_OF_DAY, selectedHour)
                                    set(Calendar.MINUTE, selectedMinute)
                                }
                                time = if (selectedTime.get(Calendar.AM_PM) == Calendar.AM)
                                    "${String.format(Locale.getDefault(), "%02d", selectedHour)}:${
                                        String.format(Locale.getDefault(), "%02d", selectedMinute)
                                    } AM"
                                else "${String.format(Locale.getDefault(), "%02d", selectedHour)}:${
                                    String.format(Locale.getDefault(), "%02d", selectedMinute)
                                } PM"
                                onClickEvent(
                                    EditClassEvent.OnCheckChange(
                                        dbDay.first,
                                        time,
                                        isCheck
                                    )
                                )
                            },
                            selectedTime.get(Calendar.HOUR_OF_DAY),
                            selectedTime.get(Calendar.MINUTE),
                            false
                        )

                        TextButton(onClick = { timePicker.show() }) {
                            Text(text = time)
                        }
                    }
                }
            }
            Button(
                onClick = {
                    onClickEvent(EditClassEvent.EditClass)
                    onClick.invoke()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = state.value.atLeastOneCheck
            ) {
                Text(text = "Edit")
            }
        }
    }

}


