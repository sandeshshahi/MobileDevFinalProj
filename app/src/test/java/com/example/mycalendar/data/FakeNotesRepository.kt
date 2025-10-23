package com.example.mycalendar.data

import com.example.mycalendar.core.database.entity.Note
import com.example.mycalendar.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeNotesRepository : NotesRepository {
    private val all = MutableStateFlow<List<Note>>(emptyList())
    private var nextId = 1L

    override fun notesForUser(username: String): Flow<List<Note>> =
        all.map { list -> list.filter { it.username == username } }.distinctUntilChanged()

    override fun notesForDate(username: String, bsMonth: String, bsDate: String): Flow<List<Note>> =
        all.map { list ->
            list.filter { it.username == username && it.bsMonth == bsMonth && it.bsDate == bsDate }
        }.distinctUntilChanged()

    override suspend fun addNote(note: Note): Long {
        val id = nextId++
        all.update { it + note }
        return id
    }

    override suspend fun updateNote(note: Note) {
        all.update { current ->
            val idx = current.indexOfFirst { it == note }
            if (idx >= 0) current.toMutableList().apply { this[idx] = note } else current
        }
    }

    override suspend fun deleteNote(note: Note) {
        all.update { it.filterNot { n -> n == note } }
    }
}
