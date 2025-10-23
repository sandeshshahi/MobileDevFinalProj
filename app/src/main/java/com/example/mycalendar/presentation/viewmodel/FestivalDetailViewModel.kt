package com.example.mycalendar.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mycalendar.core.ai.GenerativeModelService
import com.example.mycalendar.core.dispatcher.AppDispatchers
import com.example.mycalendar.core.dispatcher.DefaultAppDispatchers
import com.example.mycalendar.data.repository.FestivalRepositoryImpl
import com.example.mycalendar.domain.repository.FestivalRepository
import com.example.mycalendar.presentation.uistate.FestivalDetailUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class FestivalDetailViewModel(
    private val festivalName: String,
    private val bsMonth: String,
    private val bsDate: String,
    private val enDate: String,
    private val festivalRepository: FestivalRepository,
    private val dispatchers: AppDispatchers = DefaultAppDispatchers
) : ViewModel() {

    private val parsedFestivals: Pair<String, String> = festivalName.split("\n", limit = 2).let { parts ->
        if (parts.size == 2) parts[0].trim() to parts[1].trim() else parts[0].trim() to parts[0].trim()
    }
    private val tithi: String = parsedFestivals.first
    private val specificFestivalNames: String = parsedFestivals.second

    private val displayName: String = if (tithi == specificFestivalNames) tithi else "$tithi, $specificFestivalNames"
    private val aiPromptName: String = specificFestivalNames.ifBlank { tithi }

    private val _uiState = MutableStateFlow(
        FestivalDetailUiState(
            festivalName = displayName.ifBlank { "Festival" },
            bsMonth = bsMonth,
            bsDate = bsDate,
            enDate = enDate,
            isLoading = true
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    private fun buildTextPrompt(): String = buildString {
        append("Write a clear 5–6 sentence description of the Nepali festival \"")
        append(aiPromptName)
        append("\", which occurs on the day of \"")
        append(tithi)
        append("\". Mention its origin or significance, common rituals (e.g., puja, lights, tika, fasting), ")
        append("how families/communities celebrate it, and when it is observed. ")
        if (bsMonth.isNotBlank() && bsDate.isNotBlank()) {
            append("Bikram Samvat reference date: ").append(bsMonth).append(" ").append(bsDate).append(". ")
        }
        if (enDate.isNotBlank()) {
            append("It typically falls around ").append(enDate).append(" in the Gregorian calendar. ")
        }
        append("Avoid lists and marketing tone.")
    }

    private fun buildImagePrompt(translatedName: String): String {
        val base = translatedName.ifBlank { "a Nepali cultural celebration" }
        return "$base in Nepal, cultural celebration, traditional attire, rituals, oil lamps, vibrant colors, photorealistic, 4k, 16:9"
    }

    private fun load() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val englishImageName: String
        val translationError: Throwable?
        withContext(dispatchers.io) {
            val prompt = "Translate the following Nepali festival name to English: \"$aiPromptName\". Provide only the translated English name and nothing else."
            val result = festivalRepository.generateText(prompt)
            translationError = result.exceptionOrNull()
            englishImageName = result.getOrNull()?.trim().orEmpty().ifBlank { "a Nepali cultural celebration" }
        }

        val textPrompt = buildTextPrompt()
        val imagePrompt = buildImagePrompt(englishImageName)

        Log.d("FestivalVM", "Original Name -> $festivalName")
        Log.d("FestivalVM", "English Name for Image -> $englishImageName")
        Log.d("FestivalVM", "textPrompt -> $textPrompt")
        Log.d("FestivalVM", "imagePrompt -> $imagePrompt")

        val textResult = supervisorScope {
            async(dispatchers.io) { festivalRepository.generateText(textPrompt) }.await()
        }
        val imgResult = supervisorScope {
            async(dispatchers.io) { festivalRepository.generateImage(imagePrompt) }.await()
        }

        val description = textResult.getOrElse {
            withContext(dispatchers.io) {
                GenerativeModelService.generateFestivalDescription(aiPromptName).ifBlank {
                    if (festivalName.isBlank())
                        "A Nepali cultural festival observed with rituals and community gatherings."
                    else
                        "“$festivalName” is a Nepali festival marked by rituals, family observances, and community celebrations."
                }
            }
        }
        val images = imgResult.getOrElse { emptyList() }

        val textSucceeded = textResult.isSuccess
        val imageSucceeded = imgResult.isSuccess && images.isNotEmpty()

        val friendlyError = if (!textSucceeded && !imageSucceeded) {
            "Content is temporarily unavailable. Please try again."
        } else null

        _uiState.update {
            it.copy(
                description = description,
                image = images,
                isLoading = false,
                error = friendlyError
            )
        }
    }

    companion object {
        fun factory(
            festivalName: String,
            bsMonth: String,
            bsDate: String,
            enDate: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = com.example.mycalendar.data.repository.FestivalRepositoryImpl(
                    imagenModel = com.example.mycalendar.core.ai.GenerativeModelService.getImageGenModel(),
                    textModel = com.example.mycalendar.core.ai.GenerativeModelService.getTextGenModel()
                )
                return FestivalDetailViewModel(
                    festivalName = festivalName,
                    bsMonth = bsMonth,
                    bsDate = bsDate,
                    enDate = enDate,
                    festivalRepository = repo
                ) as T
            }
        }
    }
}
