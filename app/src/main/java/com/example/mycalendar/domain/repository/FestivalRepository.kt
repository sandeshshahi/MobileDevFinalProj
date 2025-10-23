package com.example.mycalendar.domain.repository

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface FestivalRepository {

    suspend fun generateImage(prompt: String): Result<List<Bitmap>>

    suspend fun generateText(prompt: String): Result<String>
}
