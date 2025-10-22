package com.example.mycalendar.data.remote

data class Day(
    val n: String,   // Nepali date
    val e: String,   // English date
    val t: String,   // tithi
    val f: String,   // festival
    val h: Boolean,  // holiday (red color)
    val d: Int,      // weekday index
    val wd: String? = null // weekday text (e.g., "आईतवार")
)
