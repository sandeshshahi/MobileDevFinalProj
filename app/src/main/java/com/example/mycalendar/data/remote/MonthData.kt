package com.example.mycalendar.data.remote

data class MonthData(
    val metadata: Map<String, String>,
    val days: List<Day>,
    val holiFest: List<String>,
    val marriage: List<String>,
    val bratabandha: List<String>
)
