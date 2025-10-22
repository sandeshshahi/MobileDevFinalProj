package com.example.mycalendar.domain.repository

import com.example.mycalendar.data.local.FestivalEntity
import kotlinx.coroutines.flow.Flow

interface FestivalRepository {
    fun getFestivalsForMonth(bsYearMonth: String): Flow<List<FestivalEntity>>
    suspend fun getFestivalByName(name: String): FestivalEntity?
    suspend fun getFestivalsForDate(bsDate: String): List<FestivalEntity>
}
