package com.example.mycalendar.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


interface AppNavKey: NavKey

@Serializable
object Calendar: AppNavKey

@Serializable
data class FestivalDetail(
    val festivalName: String,
    val bsMonth: String,
    val bsDate: String,
    val enDate: String = ""
): AppNavKey

@Serializable
object Info: AppNavKey

@Serializable
object Login: AppNavKey

@Serializable
object Register: AppNavKey