// Kotlin
package com.example.mycalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.mycalendar.navigation.AppNavGraph
import com.example.mycalendar.presentation.ui.CalendarScreen
import com.example.mycalendar.ui.theme.MyCalendarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyCalendarTheme {

                    AppNavGraph(modifier = Modifier.fillMaxSize())

            }
        }
    }
}
