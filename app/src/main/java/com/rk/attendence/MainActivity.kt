package com.rk.attendence

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rk.attendence.bottomnavigation.navigation.BottomBar
import com.rk.attendence.database.dbconnection.SingletonDBConnection
import com.rk.attendence.sharedpref.LocalData
import com.rk.attendence.ui.theme.AttendenceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AttendenceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BottomBar()
                }
            }
        }


        SingletonDBConnection.provideContext(this)
        LocalData.initialize(this)
        if (!LocalData.getBoolean(LocalData.NOTIFICATION) && !LocalData.getBoolean(LocalData.ALREADY_INSTALLED)) {
            LocalData.setBoolean(LocalData.ALREADY_INSTALLED, true)
        }
//        scheduledAlarm()
    }

//     private fun notificationPermission(context: Context) {
//        if (!checkNotificationPermission(context)) {
//            val requestPermission =
//                registerForActivityResult(ActivityResultContracts.RequestPermission())
//                {
//                    if (it) {
//                        LocalData.setBoolean(LocalData.NOTIFICATION, true)
//                        scheduledAlarm()
//                        println("Permission granted")
//                    } else {
//                        println("Permission not granted")
//                    }
//                }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }
//
//    private fun scheduledAlarm() {
//        if ( validateAlarm(this)) {
//            val alarm = AlarmSchedulerImplement(this)
//            alarm.schedule()
//        }
//    }


}

