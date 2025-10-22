// kotlin
package com.example.mycalendar.presentation.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class FestivalCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Get tomorrow's BS date using your converter library
            val tomorrowBsDate = "" // ... logic to get tomorrow's BS date string

            // 2. Local simple repository stub (replace with real repository/DB)
            val festivalRepository = object : com.example.mycalendar.domain.repository.FestivalRepository {
                override fun getFestivalsForMonth(bsYearMonth: String) =
                    kotlinx.coroutines.flow.flow { emit(emptyList<com.example.mycalendar.data.local.FestivalEntity>()) }

                override suspend fun getFestivalByName(name: String): com.example.mycalendar.data.local.FestivalEntity? = null

                override suspend fun getFestivalsForDate(bsDate: String): List<com.example.mycalendar.data.local.FestivalEntity> = emptyList()
            }

            // 3. Query repository (which queries Room in real impl)
            val tomorrowsFestivals = festivalRepository.getFestivalsForDate(tomorrowBsDate)

            // 4. If festivals exist, show a notification
            if (tomorrowsFestivals.isNotEmpty()) {
                val festivalNames = tomorrowsFestivals.joinToString { it.name }
                sendNotification("Upcoming Festival: $festivalNames")
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun sendNotification(message: String) {
        // ... standard Android notification builder logic ...
    }
}