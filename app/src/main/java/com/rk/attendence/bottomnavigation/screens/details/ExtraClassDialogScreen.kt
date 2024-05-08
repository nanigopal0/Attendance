package com.rk.attendence.bottomnavigation.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rk.attendence.database.entity.ClassEntity
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraClassDialogScreen(
    remainingClasses: List<ClassEntity>,
    localDate: LocalDate,
    onClickEvent: (detailsEvent: DetailsEvent) -> Unit,
) {
    var classId by remember {
        mutableIntStateOf(-1)
    }

    BasicAlertDialog(
        onDismissRequest = { onClickEvent(DetailsEvent.HideExtraClassDialog) },
        modifier = Modifier
            .fillMaxWidth(.9f)
            .fillMaxHeight(.6f)
            .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10))
            .padding(vertical = 20.dp, horizontal = 12.dp)
    ) {
        Column(modifier = Modifier) {
            Text(
                text = "Add class",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            LazyColumn(
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 15.dp)
                    .weight(1f)
            ) {
                items(remainingClasses) {
                    Row {
                        RadioButton(selected = classId == it.id, onClick = {
                            classId = it.id
                        })
                        Text(
                            text = it.className, modifier = Modifier
                                .padding(start = 10.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
            Row(modifier = Modifier.align(Alignment.End)) {
                Button(
                    onClick = { onClickEvent(DetailsEvent.HideExtraClassDialog) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Cancel")
                }
                Button(onClick = {
                    onClickEvent(
                        DetailsEvent.AddExtraClass(
                            classId = classId,
                            localDate = localDate
                        )
                    )
                    onClickEvent(DetailsEvent.HideExtraClassDialog)
                }, modifier = Modifier.padding(start = 10.dp)) {
                    Text(text = "Add")
                }

            }

        }
    }
}