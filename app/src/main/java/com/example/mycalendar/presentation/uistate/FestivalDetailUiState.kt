package com.example.mycalendar.presentation.uistate

import android.graphics.Bitmap

data class FestivalDetailUiState(
    val festivalName: String = "",
    val bsMonth: String = "",
    val bsDate: String = "",
    val enDate: String = "",
    val description: String = "",
    val image: List<Bitmap> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
