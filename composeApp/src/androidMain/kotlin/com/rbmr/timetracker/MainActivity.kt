package com.rbmr.timetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rbmr.timetracker.data.database.appContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the global context for Database and ShareHelper
        appContext = applicationContext

        setContent {
            App()
        }
    }
}