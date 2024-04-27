package com.rk.attendence.bottomnavigation.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rk.attendence.bottomnavigation.screens.classes.AddClassScreen
import com.rk.attendence.bottomnavigation.screens.classes.EditClassScreen
import com.rk.attendence.bottomnavigation.screens.dashboard.DashBoardScreen
import com.rk.attendence.bottomnavigation.screens.details.DetailsScreen
import com.rk.attendence.bottomnavigation.screens.semester.AddSemesterScreen
import com.rk.attendence.bottomnavigation.screens.semester.EditSemesterScreen
import com.rk.attendence.bottomnavigation.screens.setting.daterangepicker.DateRangePickerScreen
import com.rk.attendence.bottomnavigation.screens.setting.getpercentage.GetPercentage
import com.rk.attendence.bottomnavigation.screens.setting.notification.Notification
import com.rk.attendence.bottomnavigation.screens.setting.Settings
import com.rk.attendence.bottomnavigation.screens.shared.SharedViewmodel
import com.rk.attendence.sharedpref.LocalData
import com.rk.attendence.splash.NameScreen

@Composable
fun BottomNavGraph(navHostController: NavHostController) {
    val sharedViewmodel: SharedViewmodel = viewModel()
    val name = LocalData.getString(LocalData.NAME)

    NavHost(navController = navHostController,
        startDestination = if (name.isNotEmpty()) BottomNavItem.DashBoard.route else NavGraph.NAME_SCREEN,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
        exitTransition = { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right) }) {
        composable(route = BottomNavItem.DashBoard.route) {
            DashBoardScreen(sharedViewmodel) {
                when (it) {
                    1 -> navHostController.navigate(NavGraph.ADD_CLASS)
                    2 -> navHostController.navigate(NavGraph.ADD_SEMESTER)
                }
            }
        }
        composable(route = BottomNavItem.Details.route) {
            DetailsScreen(sharedViewmodel)
        }
        composable(route = BottomNavItem.Setting.route) {
            Settings(sharedViewmodel) {
                when (it) {
                    1 -> navHostController.navigate(NavGraph.GET_PERCENTAGE_SCREEN)
                    2 -> navHostController.navigate(NavGraph.NOTIFICATION_SCREEN)
                }
            }
        }
        composable(NavGraph.NAME_SCREEN) {
            NameScreen {
                navHostController.popBackStack()
                navHostController.navigate(BottomNavItem.DashBoard.route)
            }
        }
        navGraph(navHostController, sharedViewmodel)

    }
}

fun NavGraphBuilder.navGraph(
    navHostController: NavHostController,
    sharedViewmodel: SharedViewmodel
) {
    navigation(route = NavGraph.NAVIGATION, startDestination = NavGraph.SEARCH) {
        composable(NavGraph.SEARCH) { Log.d("TAG", "navGraph: Search screen") }
        composable(NavGraph.ADD_CLASS) {
            AddClassScreen {
                when (it) {
                    1 -> navHostController.navigate(NavGraph.EDIT_SEMESTER)
                    2 -> navHostController.navigate(NavGraph.ADD_SEMESTER)
                    3 -> {
                        navHostController.popBackStack()
//                        sharedViewmodel.getTodayClass()
                    }
                }
            }
        }
        composable(NavGraph.EDIT_CLASS) {
            EditClassScreen()
        }
        composable(NavGraph.ADD_SEMESTER) {
            AddSemesterScreen {
                navHostController.popBackStack()
                navHostController.navigate(NavGraph.ADD_CLASS)
            }
        }
        composable(NavGraph.EDIT_SEMESTER) {
            EditSemesterScreen()
        }
        composable(NavGraph.GET_PERCENTAGE_SCREEN) {
            GetPercentage(sharedViewmodel = sharedViewmodel) {
                navHostController.navigate(NavGraph.DATE_RANGE_PICKER_SCREEN)
            }
        }
        composable(NavGraph.DATE_RANGE_PICKER_SCREEN) {
            DateRangePickerScreen {
                navHostController.popBackStack()
            }
        }
        composable(NavGraph.NOTIFICATION_SCREEN) {
            Notification { navHostController.popBackStack() }
        }
    }
}

object NavGraph {
    const val ADD_SEMESTER = "add_semester"
    const val EDIT_SEMESTER = "edit_semester"
    const val EDIT_CLASS = "edit_class"
    const val NAVIGATION = "navigation"
    const val ADD_CLASS = "add_class"
    const val SEARCH = "search"
    const val NAME_SCREEN = "name_screen"
    const val GET_PERCENTAGE_SCREEN = "get_percentage_screen"
    const val DATE_RANGE_PICKER_SCREEN = "date_range_picker_screen"
    const val NOTIFICATION_SCREEN = "notification_screen"
}