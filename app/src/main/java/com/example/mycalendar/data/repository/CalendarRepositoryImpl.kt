package com.example.mycalendar.data.repository

import android.content.Context
import com.example.mycalendar.domain.repository.CalendarRepository
import kotlinx.coroutines.delay

class CalendarRepositoryImpl(
    private val context: Context
) : CalendarRepository {

    override suspend fun syncCalendar() {
        // TODO: Replace with real API call + DB cache.

        delay(250) // placeholder
    }

    companion object {
        fun from(context: Context) = CalendarRepositoryImpl(context.applicationContext)
    }
}
