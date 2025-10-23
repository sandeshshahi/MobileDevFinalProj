package com.example.mycalendar.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val bsMonth: String,
    val bsDate: String,
    val enDate: String = "",
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
