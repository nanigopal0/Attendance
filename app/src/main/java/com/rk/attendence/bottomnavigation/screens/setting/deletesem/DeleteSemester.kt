package com.rk.attendence.bottomnavigation.screens.setting.deletesem

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rk.attendence.bottomnavigation.screens.setting.SettingEvent

@Composable
fun DeleteSemester(
    onClickEvent: (settingEvent: SettingEvent) -> Unit,
    sem: String,
) {
//    val deleteSemesterViewmodel: DeleteSemesterViewmodel =
//        viewModel(factory = object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                return DeleteSemesterViewmodel(sharedViewmodel.state.value.semesterEntity) as T
//            }
//        })


    AlertDialog(
        onDismissRequest = { onClickEvent(SettingEvent.HideDeleteSemDialog) },
        title = {
            Text(text = "Delete $sem semester?")
        },
        text = {
            Spacer(modifier = Modifier.height(20.dp))
        },
        confirmButton = {
            Button(
                onClick = {
                    onClickEvent(SettingEvent.DeleteSemester)
                    onClickEvent(SettingEvent.HideDeleteSemDialog)
                },

                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            Button(onClick = { onClickEvent(SettingEvent.HideDeleteSemDialog) }) {
                Text(text = "Cancel")
            }
        }
    )
}
