package com.example.mycalendar.data.remote

import com.google.ai.client.generativeai.GenerativeModel

class GenerativeAiRepository {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "YOUR_API_KEY_HERE" // Load this securely
    )

    suspend fun getCulturalContext(festivalName: String): String {
        val prompt = "Write a 5-sentence summary of the history and " +
                     "cultural significance of the Nepali festival '$festivalName'."

        try {
            val response = generativeModel.generateContent(prompt)
            return response.text ?: "Could not find information."
        } catch (e: Exception) {
            // Handle API exceptions
            return "Error fetching details."
        }
    }
}
