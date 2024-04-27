package com.rk.attendence.bottomnavigation.screens.setting.getpercentage

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rk.attendence.bottomnavigation.screens.setting.getpercentage.GetPercentageViewmodel
import com.rk.attendence.bottomnavigation.screens.shared.SharedViewmodel
import com.rk.attendence.sharedpref.LocalData
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale


@Composable
fun GetPercentage(sharedViewmodel: SharedViewmodel, onClick: () -> Unit) {
    val getPercentageViewmodel: GetPercentageViewmodel = viewModel()
    val sharedState = sharedViewmodel.state.collectAsState()
    val startDate = LocalData.getLong(LocalData.DATE_RANGE_PICKER_START_DATE)
    val endDate = LocalData.getLong(LocalData.DATE_RANGE_PICKER_END_DATE)
    val viewmodelState = getPercentageViewmodel.state.collectAsState()


    LaunchedEffect(key1 = sharedState.value) {
        getPercentageViewmodel.initialiseVar(sharedState.value.semesterToClassToAttendance)
    }
    Column {
        Text(
            text = "Select date range to show attendance percentage",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 10.dp)
                .align(Alignment.CenterHorizontally)
        )

        Row(modifier = Modifier.padding(horizontal = 20.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = (if (startDate != 0L) SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.ENGLISH
                    ).format(Date(startDate)) else "Start date")
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = (if (endDate != 0L) SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.ENGLISH
                    ).format(Date(endDate)) else "End date")
                )
            }
            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                IconButton(
                    onClick = { onClick.invoke() },
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = "editCalender"
                    )
                }
            }
        }
        if (startDate != 0L && endDate != 0L) {
            getPercentageViewmodel.getPercentage(
                Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).toLocalDate(),
                Instant.ofEpochMilli(endDate).atZone(ZoneId.systemDefault()).toLocalDate()
            )
        }

        LazyColumn {
            items(viewmodelState.value.allClasses) { classEntity ->

                val isExpanded = remember {
                    mutableStateOf(false)
                }
                Column(modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 20.dp)
                    .border(
                        1.dp,
                        Color.Gray,
                        RoundedCornerShape(if (isExpanded.value) 10 else 20)
                    )
                    .clickable { isExpanded.value = !isExpanded.value }) {

                    Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            Text(
                                text = classEntity.className,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Text(
                            text = "${
                                try {
                                    classEntity.present * 100 / (classEntity.present + classEntity.absent)
                                } catch (e: Exception) { 0 }
                            }%",
                            modifier = Modifier,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    if (isExpanded.value) {
                        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                            Text(
                                text = "Present ",
                                color = Color(0xFF238D0C),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = " ${classEntity.present} out of ${classEntity.present + classEntity.absent} ",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                            Text(
                                text = "Absent ",
                                color = Color(0xFFB30B0B),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = " ${classEntity.absent} out of ${classEntity.present + classEntity.absent} ",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                            Text(
                                text = "Cancel ",
                                color = Color(0xFFEB4B0C),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = " ${classEntity.cancel}  ",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = true
)
@Composable
fun DateRangePreview() {
    DateRangePicker(state = DateRangePickerState(locale = CalendarLocale("en")))
}