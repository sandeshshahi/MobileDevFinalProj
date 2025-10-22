package com.example.mycalendar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FestivalEntity::class], version = 1, exportSchema = false)
abstract class MyCalendarDatabase : RoomDatabase() {
    abstract fun festivalDao(): FestivalDao
}
