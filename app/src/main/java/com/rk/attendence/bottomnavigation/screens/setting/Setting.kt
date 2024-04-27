package com.rk.attendence.bottomnavigation.screens.setting

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rk.attendence.bottomnavigation.screens.setting.deletesem.DeleteSemester
import com.rk.attendence.bottomnavigation.screens.setting.updatesem.UpdateSemesterDialog
import com.rk.attendence.bottomnavigation.screens.shared.SharedViewmodel
import com.rk.attendence.sharedpref.LocalData

@Composable
fun Settings(sharedViewmodel: SharedViewmodel, onClick: (Int) -> Unit) {
    val sharedState = sharedViewmodel.state.collectAsState()
    val settingsViewmodel: SettingsViewmodel = viewModel()
    val settingState = settingsViewmodel.state.collectAsState()
    var showDropDown by remember {
        mutableStateOf(false)
    }
    val onClickEvent = settingsViewmodel::onClickSettingEvent

    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    LaunchedEffect(key1 = sharedState.value) {
        settingsViewmodel.initialiseVar(sharedState.value.semesterToClassToAttendance)
    }
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .fillMaxSize()
    ) {

        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = sharedState.value.semesterEntity.studentName.ifEmpty {
                LocalData.getString(
                    LocalData.NAME
                )
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Total attendance ${settingState.value.totalAttendance}%",
            style = MaterialTheme.typography.titleMedium
        )
        HorizontalDivider(
            color = Color.LightGray,
            modifier = Modifier.padding(vertical = 40.dp)
        )

        Row(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = "Default Semester ",
                style = MaterialTheme.typography.titleMedium
            )
            Box(
                modifier = Modifier.pointerInput(true) {
                    detectTapGestures(
                        onPress = {
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                            showDropDown = !showDropDown
                        }
                    )
                },
            ) {
                Row {
                    Text(
                        text = sharedState.value.semesterEntity.semesterName,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        imageVector = if (showDropDown) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }

            DropdownMenu(
                expanded = showDropDown,
                offset = pressOffset,
                onDismissRequest = { showDropDown = false }) {
                sharedState.value.semesterList.forEach {
                    DropdownMenuItem(text = { Text(text = it.semesterName) }, onClick = {
                        LocalData.setInt(LocalData.CURRENT_SEMESTER_ID, it.id)
                        sharedViewmodel.getSemToClassToAttend(it.id)
                        showDropDown = false
                    })
                }
            }
        }

        if (settingState.value.showUpdateSemDialog)
            UpdateSemesterDialog(onClickEvent, settingState.value.currentSemester.semesterName)

        if (settingState.value.showDeleteSemDialog)
            DeleteSemester(onClickEvent, settingState.value.currentSemester.semesterName)

        HorizontalDivider(
            color = Color.LightGray,
            modifier = Modifier.padding(top = 20.dp, bottom = 5.dp)
        )
        TextButton(
            onClick = { onClick(2) },
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Text(
                text = "Notification",
                style = MaterialTheme.typography.titleMedium
            )
        }
        HorizontalDivider(
            color = Color.LightGray,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        TextButton(
            onClick = {
                onClickEvent(SettingEvent.ShowUpdateSemesterDialog)
            },
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Text(
                text = "Update Semester",
                style = MaterialTheme.typography.titleMedium
            )
        }
        HorizontalDivider(
            color = Color.LightGray,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        TextButton(
            onClick = {
                onClick.invoke(1)
            },
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Text(
                text = "Get percentage",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        HorizontalDivider(
            color = Color.LightGray,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        TextButton(
            onClick = {
                onClickEvent(SettingEvent.ShowDeleteSemDialog)
            },
            enabled = sharedState.value.semesterEntity.id > 0,
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Text(
                text = "Delete Semester",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Red
            )
        }

    }
}