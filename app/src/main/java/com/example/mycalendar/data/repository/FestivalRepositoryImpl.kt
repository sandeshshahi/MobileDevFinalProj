// kotlin
package com.example.mycalendar.data.repository

import com.example.mycalendar.data.local.FestivalDao
import com.example.mycalendar.data.local.FestivalEntity
import com.example.mycalendar.domain.repository.FestivalRepository
import kotlinx.coroutines.flow.Flow

class FestivalRepositoryImpl constructor(private val dao: FestivalDao) : FestivalRepository {
    override fun getFestivalsForMonth(bsYearMonth: String): Flow<List<FestivalEntity>> {
        return dao.getFestivalsForMonth(bsYearMonth)
    }

    override suspend fun getFestivalByName(name: String): FestivalEntity? {
        return dao.getFestivalByName(name)
    }

    override suspend fun getFestivalsForDate(bsDate: String): List<FestivalEntity> {
        // This is not efficient, but it will work for now.
        val yearMonth = bsDate.substring(0, 7)
        val festivals = dao.getFestivalsForMonth(yearMonth)
        val festivalsForDate = mutableListOf<FestivalEntity>()
        festivals.collect { festivalList ->
            festivalsForDate.addAll(festivalList.filter { it.bsDate == bsDate })
        }
        return festivalsForDate
    }
}