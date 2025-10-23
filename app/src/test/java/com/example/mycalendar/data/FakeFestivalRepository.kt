package com.example.mycalendar.data

import android.graphics.Bitmap
import com.example.mycalendar.domain.repository.FestivalRepository

class FakeFestivalRepository : FestivalRepository {
    var textResponse: Result<String> = Result.success("Fake festival description")
    var imageResponse: Result<List<Bitmap>> = Result.success(emptyList())

    override suspend fun generateImage(prompt: String): Result<List<Bitmap>> {
        return imageResponse
    }

    override suspend fun generateText(prompt: String): Result<String> {
        return textResponse
    }
}