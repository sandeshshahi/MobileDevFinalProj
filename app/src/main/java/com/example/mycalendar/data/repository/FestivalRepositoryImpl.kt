package com.example.mycalendar.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.mycalendar.domain.repository.FestivalRepository
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ImagenModel

class FestivalRepositoryImpl(
    private val imagenModel: ImagenModel,
    private val textModel: GenerativeModel
) : FestivalRepository {

    companion object {
        private const val TAG = "FestivalRepo"
    }

    override suspend fun generateImage(prompt: String): Result<List<Bitmap>> {
        return try {
            if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "generateImage prompt -> $prompt")
            val resp = imagenModel.generateImages(prompt)
            Result.success(resp.images.map { it.asBitmap() })
        } catch (t: Throwable) {
            Log.e(TAG, "generateImage failed", t)
            Result.failure(t)
        }
    }

    override suspend fun generateText(prompt: String): Result<String> {
        return try {
            if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "generateText prompt -> $prompt")
            val text = textModel.generateContent(prompt).text?.trim().orEmpty()
            if (text.isBlank()) {
                Result.failure(IllegalStateException("Empty text from model"))
            } else {
                Result.success(text)
            }
        } catch (t: Throwable) {
            Log.e(TAG, "generateText failed", t)
            Result.failure(t)
        }
    }

    // Optional translator via the same text model
    suspend fun translateToEnglish(text: String): String {
        val prompt = "Translate the following Nepali festival name to English. Respond with only the translated name:\n\"$text\""
        return try {
            textModel.generateContent(prompt).text?.trim().orEmpty().ifBlank { "a Nepali cultural celebration" }
        } catch (t: Throwable) {
            Log.e(TAG, "Translation via text model failed", t)
            "a Nepali cultural celebration"
        }
    }
}
