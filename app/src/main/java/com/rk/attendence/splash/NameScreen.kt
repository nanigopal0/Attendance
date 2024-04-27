package com.rk.attendence.splash

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rk.attendence.sharedpref.LocalData

@Composable
fun NameScreen(onClick: () -> Unit) {
    var name by rememberSaveable {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Enter your name ", modifier = Modifier.padding(bottom = 20.dp))
        TextField(
            value = name,
            onValueChange = { name = it },
            maxLines = 2,
            modifier = Modifier
                .padding(bottom = 30.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(30)
                )
                .clip(RoundedCornerShape(30))
        )
        Button(
            onClick = {
                LocalData.setString(LocalData.NAME, name)
                onClick()
            },
            enabled = name.isNotEmpty()
        ) {
            Text(text = "Save")
        }
    }
}