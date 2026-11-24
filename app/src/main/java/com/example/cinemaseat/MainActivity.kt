package com.example.cinemaseat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cinemaseat.ui.CinemaMainScreen
import com.example.cinemaseat.ui.theme.CinemaSeatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CinemaSeatTheme {
                CinemaMainScreen()
            }
        }
    }
}