package com.rk.attendence.bottomnavigation.screens.semester

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rk.attendence.bottomnavigation.screens.classes.Days
import kotlin.reflect.KFunction1


@Composable
fun AddSemesterScreen(function: () -> Unit) {

    val addSemesterViewmodel: AddSemesterViewmodel = viewModel()
    val onClickEvent = addSemesterViewmodel::onClickEventFunction
    val state = addSemesterViewmodel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Semester",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        TextField(
            value = state.value.semName,
            onValueChange = { onClickEvent(AddSemesterEvent.SemName(it)) },
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

        DaysCheckbox(day = Days.MONDAY, onClickEvent)
        DaysCheckbox(day = Days.TUESDAY, onClickEvent)
        DaysCheckbox(day = Days.WEDNESDAY, onClickEvent)
        DaysCheckbox(day = Days.THURSDAY, onClickEvent)
        DaysCheckbox(day = Days.FRIDAY, onClickEvent)
        DaysCheckbox(day = Days.SATURDAY, onClickEvent)
        DaysCheckbox(day = Days.SUNDAY, onClickEvent)

        Button(
            onClick = {
                onClickEvent(AddSemesterEvent.Add)
                function()
            },
            enabled = state.value.atLeastOneChecked
        ) {
            Text(text = "Add")
        }
    }
}

@Composable
fun DaysCheckbox(day: String, onClick: KFunction1<AddSemesterEvent, Unit>) {
    var isCheck by rememberSaveable {
        mutableStateOf(false)
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Checkbox(checked = isCheck,
            onCheckedChange = {
                isCheck = it
                onClick(AddSemesterEvent.CheckedDay(day, isCheck))
            })

        Text(
            text = day,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AddSemesterScreenPreview() {
    AddSemesterScreen {}
}