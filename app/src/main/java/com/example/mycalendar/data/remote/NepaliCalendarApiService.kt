package com.example.mycalendar.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NepaliCalendarApiService {
    @GET("api/scrape")
    suspend fun getMonth(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<MonthData>
}