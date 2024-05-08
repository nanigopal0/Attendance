package com.rk.attendence.bottomnavigation.screens.setting.allclass

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.rk.attendence.bottomnavigation.screens.shared.SharedViewmodel
import com.rk.attendence.database.entity.ClassEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllClassScreen(sharedViewmodel: SharedViewmodel, onClick: (Int) -> Unit) {
    val sharedState = sharedViewmodel.state.collectAsState()
    val allClasses = rememberSaveable {
        mutableStateOf(listOf<ClassEntity>())
    }
    var showDeleteClassDialog by remember {
        mutableStateOf(ClassEntity(0, 0, "", emptyMap(), 0, 0, 0))
    }

    LaunchedEffect(key1 = sharedState.value.semesterToClassToAttendance) {
        allClasses.value =
            sharedState.value.semesterToClassToAttendance?.classToAttendance?.map { it?.classEntity!! }
                ?: emptyList()
    }


    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Attendance",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondaryContainer)
        )
    }) { padding ->

        if (showDeleteClassDialog.id != 0) {
            AlertDialog(onDismissRequest = {
                showDeleteClassDialog = showDeleteClassDialog.copy(id = 0)
            },
                confirmButton = {
                    Button(
                        onClick = {
                            sharedViewmodel.deleteClassStaffs(showDeleteClassDialog)
                            showDeleteClassDialog = showDeleteClassDialog.copy(id = 0)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(text = "Delete")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDeleteClassDialog = showDeleteClassDialog.copy(id = 0)
                    }) {
                        Text(text = "Cancel")
                    }
                },
                title = {
                    Text(text = "Delete ${showDeleteClassDialog.className}?")
                },
                text = {
                    Text(text = "After deleted ${showDeleteClassDialog.className} it cannot be undone")
                })
        }

        Column(
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding(),
                start = padding.calculateStartPadding(LayoutDirection.Ltr),
                end = padding.calculateEndPadding(LayoutDirection.Ltr)
            )
        ) {

            Text(
                text = "All classes",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                fontWeight = FontWeight.SemiBold
            )

            LazyColumn(modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp)) {
                items(allClasses.value) {
                    var isDropDownExpanded by remember {
                        mutableStateOf(false)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(6))
                            .padding(10.dp)
                    ) {
                        Row {
                            Text(
                                text = it.className,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(
                                    1f
                                )
                            )
                            IconButton(onClick = { isDropDownExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null
                                )
                            }

                            DropdownMenu(
                                expanded = isDropDownExpanded,
                                onDismissRequest = { isDropDownExpanded = false }) {
                                DropdownMenuItem(text = { Text(text = "Edit") }, onClick = {
                                    isDropDownExpanded = false
                                    sharedViewmodel.editClassEntity = it
                                    onClick(2)
                                })
                                DropdownMenuItem(text = { Text(text = "Delete") }, onClick = {
                                    isDropDownExpanded = false
//                                sharedViewmodel.deleteClassStaffs(it)
                                    showDeleteClassDialog = it
                                })
                            }
                        }
                        Text(text = "Present: ${it.present}", color = Color(0xFF238D0C))
                        Text(text = "Absent: ${it.absent}", color = Color(0xFFB30B0B))
                        Text(text = "Cancelled: ${it.cancel}", color = Color(0xFFEB4B0C))
                    }
                }
            }
        }
    }
}