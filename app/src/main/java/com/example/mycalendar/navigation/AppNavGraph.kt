package com.example.mycalendar.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.mycalendar.presentation.ui.CalendarScreen
import com.example.mycalendar.presentation.ui.FestivalDetailScreen
import com.example.mycalendar.presentation.ui.InfoScreen
import com.example.mycalendar.presentation.ui.LoginScreen
import com.example.mycalendar.presentation.ui.RegisterScreen
import kotlin.text.clear

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {

    val backStack = rememberNavBackStack<AppNavKey>(Calendar)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = backStack.lastOrNull() == Calendar,
                    onClick = {
                        // remove all the keys from the backstack
                        backStack.clear()
                        // add Home key to backstack
                        backStack.add(Calendar)
                    },
                    label = { Text(text = "Calendar") },
                    icon = {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar")
                    }
                )
                NavigationBarItem(
                    selected = backStack.lastOrNull() == Info,
                    onClick = {
                        // remove all the keys from the backstack
                        backStack.clear()
                        // add Settings key to backstack
                        backStack.add(Info)
                    },
                    label = { Text(text = "Info") },
                    icon = {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Info")
                    },
                )
            }
        }
    ) { innerPadding ->
        //setup navigation display
//        NavDisplay(
//            backStack = backStack,
//            onBack = {backStack.removeLastOrNull()},
//            entryDecorators = listOf(
//                rememberSavedStateNavEntryDecorator(),
//                rememberViewModelStoreNavEntryDecorator()
//            ),
//            entryProvider = entryProvider {
//                entry <Calendar> {
//                    //content of home goes here // HomeScreen()
//                    CalendarScreen(
//                        modifier = Modifier.padding(innerPadding),
//                        onOpenFestival = { name, bsMonth, bsDate, enDate ->
//                            backStack.add(FestivalDetail(name, bsMonth, bsDate, enDate))
//                        }
//                    )
//                }
//                entry<FestivalDetail> { key ->
//                    FestivalDetailScreen(
//                        args = key,
//                        onBack = { backStack.removeLastOrNull() }
//                    )
//                }
//                entry<Info> {
//                    InfoScreen()
//                }
//            },
//        )

        when (val screen = backStack.lastOrNull() ?: Calendar) {
            is Calendar -> CalendarScreen(
                modifier = Modifier.padding(innerPadding),
                onOpenFestival = { name, bsMonth, bsDate, enDate ->
                    backStack.add(FestivalDetail(name, bsMonth, bsDate, enDate))
                },
                onRequireLogin = { backStack.add(Login) }
            )

            is Info -> InfoScreen(
                onNavigateToLogin = { backStack.add(Login) },
                onNavigateToRegister = { backStack.add(Register) },
                modifier = modifier.padding(innerPadding)
            )

            is Login -> LoginScreen(
                onBack = { if (backStack.isNotEmpty()) backStack.removeLast() },
                onLoginSuccess = {
                    // Go to Info (and optionally remove Login from stack)
                    if (backStack.lastOrNull() == Login) backStack.removeLast()
                    backStack.add(Info)
                },
                modifier = modifier.padding(innerPadding)
            )

            is Register -> RegisterScreen(
                onBack = { if (backStack.isNotEmpty()) backStack.removeLast() },
                onRegistered = {
                    if (backStack.lastOrNull() == Register) backStack.removeLast()
                    backStack.add(Info)
                },
                modifier = modifier.padding(innerPadding)
            )

            is FestivalDetail -> FestivalDetailScreen(
                args = screen,
                onBack = { if (backStack.isNotEmpty()) backStack.removeLast() },
                modifier = modifier.padding(innerPadding)
            )
        }

    }


}