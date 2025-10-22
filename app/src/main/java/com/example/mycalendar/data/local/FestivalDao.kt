package com.example.mycalendar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FestivalDao {
    // We need a way to populate this DB. 
    // You'll likely pre-bundle a database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(festivals: List<FestivalEntity>)

    // Get festivals for a given BS month
    @Query("SELECT * FROM festivals WHERE bsDate LIKE :bsYearMonth || '%'")
    fun getFestivalsForMonth(bsYearMonth: String): Flow<List<FestivalEntity>> // e.g., "2081-07"

    @Query("SELECT * FROM festivals WHERE name = :name LIMIT 1")
    suspend fun getFestivalByName(name: String): FestivalEntity?
}
