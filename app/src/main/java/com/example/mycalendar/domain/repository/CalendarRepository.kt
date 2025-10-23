package com.example.mycalendar.domain.repository

interface CalendarRepository {
    suspend fun syncCalendar()
}
