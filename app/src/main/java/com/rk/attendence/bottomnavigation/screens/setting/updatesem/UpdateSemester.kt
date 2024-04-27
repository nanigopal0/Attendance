package com.rk.attendence.bottomnavigation.screens.setting.updatesem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rk.attendence.bottomnavigation.screens.setting.SettingEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateSemesterDialog(onClickEvent: (settingEvent: SettingEvent) -> Unit, semesterName: String) {
    var sem by remember {
        mutableStateOf("")
    }
    BasicAlertDialog(onDismissRequest = { onClickEvent(SettingEvent.HideUpdateSemesterDialog) }) {
        Box(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(10.dp)
            )
        ) {
            Column(modifier = Modifier.padding(30.dp)) {
                Text(
                    text = "Update semester $semesterName",
                    modifier = Modifier.padding(vertical = 20.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                TextField(value = sem, onValueChange = { sem = it })
                Row(
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .align(Alignment.End)
                ) {
                    Button(
                        onClick = { onClickEvent(SettingEvent.HideUpdateSemesterDialog) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = {
                            onClickEvent(SettingEvent.UpdateSem(sem))
                            onClickEvent(SettingEvent.HideUpdateSemesterDialog)
                        },
                        modifier = Modifier.padding(end = 20.dp)
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

