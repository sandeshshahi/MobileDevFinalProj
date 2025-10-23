package com.example.mycalendar.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mycalendar.core.database.entity.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE username = :username ORDER BY createdAt DESC")
    fun notesForUser(username: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE username = :username AND bsMonth = :bsMonth AND bsDate = :bsDate ORDER BY createdAt DESC")
    fun notesForDate(username: String, bsMonth: String, bsDate: String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}