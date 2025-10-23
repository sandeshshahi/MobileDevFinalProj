package com.example.mycalendar.core.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ImagenModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagenAspectRatio
import com.google.firebase.ai.type.ImagenImageFormat
import com.google.firebase.ai.type.ImagenPersonFilterLevel
import com.google.firebase.ai.type.ImagenSafetyFilterLevel
import com.google.firebase.ai.type.ImagenSafetySettings
import com.google.firebase.ai.type.imagenGenerationConfig

object GenerativeModelService {
    @Volatile
    private var imageGenModel: ImagenModel? = null

    @Volatile
    private var textModel: GenerativeModel? = null

    fun getTextGenModel(): GenerativeModel {
        return textModel ?: synchronized(this) {
            val m = Firebase.ai(backend = GenerativeBackend.googleAI())
                .generativeModel(modelName = "gemini-2.5-flash")
            textModel = m
            m
        }
    }

    fun getImageGenModel(): ImagenModel {
        val config = imagenGenerationConfig {
            numberOfImages = 2
            aspectRatio = ImagenAspectRatio.LANDSCAPE_16x9
            imageFormat = ImagenImageFormat.jpeg(compressionQuality = 100)
        }

        return imageGenModel ?: synchronized(this) {
            val model = Firebase.ai(backend = GenerativeBackend.googleAI()).imagenModel(
                modelName = "imagen-4.0-generate-001",
                generationConfig = config,
                safetySettings = ImagenSafetySettings(
                    safetyFilterLevel = ImagenSafetyFilterLevel.BLOCK_LOW_AND_ABOVE,
                    personFilterLevel = ImagenPersonFilterLevel.ALLOW_ALL
                )
            )
            imageGenModel = model
            model
        }
    }

    suspend fun generateFestivalDescription(festivalName: String): String {
        val prompt = buildString {
            append("Write a neutral, concise 5-6 sentence description of the Nepali festival \"")
            append(festivalName.ifBlank { "Festival" })
            append("\". Include: what it commemorates, typical rituals or customs, regional variations (if any), ")
            append("how families and communities observe it, and the usual time of year. Avoid lists and marketing tone.")
        }
        return try {
            val result = getTextGenModel().generateContent(prompt)
            result.text?.trim().orEmpty()
        } catch (t: Throwable) {
            ""
        }
    }
}
