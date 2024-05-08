package com.rk.attendence.bottomnavigation.screens.semester

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rk.attendence.bottomnavigation.screens.shared.SharedViewmodel

@Composable
fun EditSemesterScreen(sharedViewmodel: SharedViewmodel, onClick: () -> Unit) {

    val editSemesterViewmodel: EditSemesterViewmodel = viewModel()
    val onClickEvent = editSemesterViewmodel::onClickEvent
    val state = editSemesterViewmodel.state.collectAsState()

    LaunchedEffect(key1 = true) {
        editSemesterViewmodel.initialiseVar(
            sharedViewmodel.state.value.semesterEntity,
            sharedViewmodel.state.value.semesterToClassToAttendance?.classToAttendance?.map { it?.classEntity!! }
                ?: emptyList()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Edit Semester",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        TextField(
            value = state.value.semName,
            onValueChange = { onClickEvent(EditSemesterEvent.SetSemName(it)) },
            modifier = Modifier
                .padding(top = 30.dp, bottom = 20.dp)
                .border(
                    width = 1.dp, color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(shape = RoundedCornerShape(8.dp)),
            singleLine = true,
            placeholder = { Text(text = "Enter semester name") }
        )

        Text(
            text = "Teaching days of this semester",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        LazyColumn {
            items(state.value.currentCheckedDates.toList()) { day ->
                var isCheck by rememberSaveable {
                    mutableStateOf(day.second)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Checkbox(checked = isCheck,
                        onCheckedChange = {
                            isCheck = it
                            onClickEvent(EditSemesterEvent.OnCheckedDatesChanged(day.first, it))
                        })

                    Text(
                        text = day.first,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                }
            }
        }


        Button(
            onClick = {
                onClickEvent(EditSemesterEvent.Edit)
                onClick.invoke()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = state.value.atLeastOneChecked
        ) {
            Text(text = "Edit")
        }
    }
}

