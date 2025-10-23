package com.example.mycalendar.domain.repository

import com.example.mycalendar.core.database.entity.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun notesForUser(username: String): Flow<List<Note>>

    fun notesForDate(username: String, bsMonth: String, bsDate: String): Flow<List<Note>>

    suspend fun addNote(note: Note): Long

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(note: Note)
}