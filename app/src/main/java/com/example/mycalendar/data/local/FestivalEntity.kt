package com.example.mycalendar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "festivals")
data class FestivalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val bsDate: String, // e.g., "2081-07-10" (Kartik 10)
    val adDate: String, // Store as ISO 8601 string: "2025-10-27"
    val description: String,
    val region: String? = null // For regional festivals
)
