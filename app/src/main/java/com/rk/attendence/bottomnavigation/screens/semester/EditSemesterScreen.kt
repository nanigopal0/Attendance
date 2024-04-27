package com.rk.attendence.bottomnavigation.screens.semester

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EditSemesterScreen() {
    var edit by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Edit semester semName",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        BasicTextField(
            value = edit, onValueChange = { edit = it },
            singleLine = true,
            modifier = Modifier.padding(top = 30.dp)
        )

        Button(onClick = { /*TODO*/ }) {
            Text(text = "Edit")
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun EditSemesterScreenPreview() {
    EditSemesterScreen()
}