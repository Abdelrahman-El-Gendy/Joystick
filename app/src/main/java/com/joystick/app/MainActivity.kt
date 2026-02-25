package com.joystick.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.joystick.app.navigation.JoystickNavGraph
import com.joystick.app.ui.theme.JoystickTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JoystickTheme {
                val navController = rememberNavController()
                JoystickNavGraph(navController = navController)
            }
        }
    }
}