package com.rk.attendence.bottomnavigation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Details
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val hasNew: Boolean,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {

    data object DashBoard : BottomNavItem(
        route = "dashboard",
        hasNew = true,
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    )

    data object Details : BottomNavItem(
        route = "details",
        hasNew = false,
        selectedIcon = Icons.Filled.Details,
        unselectedIcon = Icons.Outlined.Details
    )

    data object Setting : BottomNavItem(
        route = "setting",
        hasNew = false,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}