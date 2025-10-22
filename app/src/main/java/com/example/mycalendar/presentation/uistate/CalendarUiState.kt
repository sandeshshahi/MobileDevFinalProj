package com.example.mycalendar.presentation.uistate

data class CalendarDay(
    val adDate: String,
    val bsDate: String,
    val isToday: Boolean,
    val isCurrentMonth: Boolean,
    val festivalName: String? = null,
    val isHoliday: Boolean = false
)

data class CalendarUiState(
    val isLoading: Boolean = true,
    val bsMonthName: String = "",
    val adMonthSpan: String = "",
    val days: List<CalendarDay> = emptyList()
)
