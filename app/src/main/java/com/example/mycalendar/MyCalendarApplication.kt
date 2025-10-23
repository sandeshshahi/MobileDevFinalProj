package com.example.mycalendar

import android.app.Application
import com.example.mycalendar.presentation.worker.CalendarWorkScheduler

class MyCalendarApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        CalendarWorkScheduler.schedule(this)
    }
}
