package com.rk.attendence.permission

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.core.app.ActivityCompat
import com.rk.attendence.sharedpref.LocalData

class RequestActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).addCategory(Intent.CATEGORY_DEFAULT).setData(
            Uri.parse("package:com.rk.attendence"))
        startActivityForResult( intent, 23, null)
        println("start")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 23 && resultCode == RESULT_OK){
            LocalData.setBoolean(LocalData.NOTIFICATION,true)
        }
    }

}