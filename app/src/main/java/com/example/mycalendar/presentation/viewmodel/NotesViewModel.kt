package com.example.mycalendar.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycalendar.core.database.NotesDatabase
import com.example.mycalendar.core.database.entity.Note
import com.example.mycalendar.data.local.PreferencesDataSource
import com.example.mycalendar.data.repository.NotesRepositoryImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = PreferencesDataSource(application)
    private val repo = NotesRepositoryImpl(NotesDatabase.getDatabase(application).noteDao())

    // exposes notes for currently logged in user
    val userNotes = prefs.getUserCredentials()
        .flatMapLatest { creds ->
            val username = creds?.username.orEmpty()
            if (username.isBlank()) repo.notesForUser("") else repo.notesForUser(username)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun notesForDateFlow(username: String, bsMonth: String, bsDate: String) =
        repo.notesForDate(username, bsMonth, bsDate)

    fun addNoteForUser(username: String, bsMonth: String, bsDate: String, enDate: String, text: String) {
        if (username.isBlank()) return
        viewModelScope.launch {
            val note = Note(
                username = username,
                bsMonth = bsMonth,
                bsDate = bsDate,
                enDate = enDate,
                text = text
            )
            repo.addNote(note)
        }
    }

    fun update(note: Note) {
        viewModelScope.launch { repo.updateNote(note) }
    }

    fun delete(note: Note) {
        viewModelScope.launch { repo.deleteNote(note) }
    }
}
