package com.example.mycalendar.data.repository

import com.example.mycalendar.core.database.dao.NoteDao
import com.example.mycalendar.core.database.entity.Note
import com.example.mycalendar.domain.repository.NotesRepository

class NotesRepositoryImpl(private val dao: NoteDao) : NotesRepository {
    override fun notesForUser(username: String) = dao.notesForUser(username)

    override fun notesForDate(username: String, bsMonth: String, bsDate: String) =
        dao.notesForDate(username, bsMonth, bsDate)

    override suspend fun addNote(note: Note): Long = dao.insert(note)
    override suspend fun updateNote(note: Note) = dao.update(note)
    override suspend fun deleteNote(note: Note) = dao.delete(note)
}