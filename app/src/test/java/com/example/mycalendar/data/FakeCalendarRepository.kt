package com.example.mycalendar.data

import com.example.mycalendar.domain.repository.CalendarRepository
import kotlinx.coroutines.delay

class FakeCalendarRepository(
    var delayMs: Long = 0,
    var throwOnSync: Throwable? = null
) : CalendarRepository {

    var calls: Int = 0
        private set

    override suspend fun syncCalendar() {
        calls += 1
        if (delayMs > 0) delay(delayMs)
        throwOnSync?.let { throw it }
    }
}
