package com.rk.attendence.bottomnavigation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun BottomBar() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            HomeTopAppBar(navController)
        },
        bottomBar = {
            HomeBottomBar(navController)
        }
    ) {
        Box(Modifier.padding(it)) {
            BottomNavGraph(navController)
        }
    }
}

@Composable
fun HomeBottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.DashBoard,
        BottomNavItem.Details,
        BottomNavItem.Setting
    )

    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarDestination = items.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            items.forEachIndexed { index, item ->
                BottomNavigationItem(
                    selected = currentDestination?.hierarchy?.any {
                        it.route == item.route
                    } == true,
                    onClick = {
                        selectedItemIndex = index
                        navController.navigate(item.route) {
                            navController.graph.findStartDestination().route?.let {
                                popUpTo(it) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        if (item.hasNew)
                            Badge(modifier = Modifier.size(if (selectedItemIndex == index) 7.dp else 5.dp))
                        Icon(
                            imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.route,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(if (selectedItemIndex == index) 30.dp else 25.dp)
                        )
                    })
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Details,
        BottomNavItem.Setting,
        BottomNavItem.DashBoard
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topBarDest = items.any { it.route == currentDestination?.route }
    val statusBarColor = MaterialTheme.colorScheme.secondaryContainer
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(statusBarColor)
    }
    if (topBarDest) {
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
    }
}
