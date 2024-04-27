package com.rk.attendence.bottomnavigation.screens.setting.daterangepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rk.attendence.sharedpref.LocalData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerScreen(onClick: () -> Unit) {
    val dateRangePickerState = rememberDateRangePickerState()
    Column(modifier = Modifier.fillMaxSize()) {

        DateRangePicker(
            state = dateRangePickerState,
            modifier = Modifier
                .fillMaxHeight(.8f)
                .padding(horizontal = 10.dp),

            title = {
                Text(
                    text = "Select date range to show attendance percentage",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        )
        HorizontalDivider(modifier = Modifier.padding(top = 15.dp))
        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 10.dp, top = 20.dp)
        ) {
            Button(onClick = { onClick.invoke() }, modifier = Modifier.padding(end = 10.dp)) {
                Text(text = "Close")
            }
            Button(onClick = {
                LocalData.setLong(
                    LocalData.DATE_RANGE_PICKER_START_DATE,
                    dateRangePickerState.selectedStartDateMillis ?: 0
                )
                LocalData.setLong(
                    LocalData.DATE_RANGE_PICKER_END_DATE,
                    dateRangePickerState.selectedEndDateMillis ?: 0
                )
                onClick.invoke()
            }) {
                Text(text = "Done")
            }
        }
    }
}