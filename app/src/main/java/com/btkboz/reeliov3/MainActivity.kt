package com.btkboz.reeliov3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.btkboz.reeliov3.ui.settings.ReelioSettingsCorrectedApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReelioSettingsCorrectedApp()
        }
    }
}
