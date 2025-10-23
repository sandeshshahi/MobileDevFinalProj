package com.example.mycalendar.presentation.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mycalendar.data.repository.CalendarRepositoryImpl

class CalendarSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val repo = CalendarRepositoryImpl.from(applicationContext)
            repo.syncCalendar()
            Log.i(TAG, "Calendar sync successful")
            Result.success()
        } catch (t: Throwable) {
            Log.e(TAG, "Calendar sync failed", t)
            Result.retry()
        }
    }

    companion object {
        const val UNIQUE_NAME = "calendar_sync_work"
        private const val TAG = "CalendarSyncWorker"
    }
}