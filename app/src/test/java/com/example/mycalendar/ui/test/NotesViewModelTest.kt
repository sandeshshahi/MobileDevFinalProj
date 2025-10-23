package com.example.mycalendar.ui.test

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.mycalendar.TestApp
import com.example.mycalendar.data.FakeNotesRepository
import com.example.mycalendar.presentation.viewmodel.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = TestApp::class)
class NotesViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var app: Application
    private lateinit var vm: NotesViewModel
    private lateinit var repo: FakeNotesRepository

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        app = ApplicationProvider.getApplicationContext()
        repo = FakeNotesRepository()
        vm = NotesViewModel(app, repo = repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addNoteForUser persists and is queryable by date`() = runTest {
        val username = "alice"
        val bsMonth = "कार्तिक"
        val bsDate = "६"
        val enDate = "Oct 23"
        val text = "Tihar prep note"

        vm.addNoteForUser(username, bsMonth, bsDate, enDate, text)
        advanceUntilIdle()

        val notes = vm.notesForDateFlow(username, bsMonth, bsDate).first()
        assertTrue(notes.isNotEmpty())
        assertEquals(text, notes.first().text)
        assertEquals(username, notes.first().username)
        assertEquals(bsMonth, notes.first().bsMonth)
        assertEquals(bsDate, notes.first().bsDate)
    }

    @Test
    fun `addNoteForUser with blank username does nothing`() = runTest {
        val username = ""
        val bsMonth = "कार्तिक"
        val bsDate = "६"
        val enDate = "Oct 23"

        vm.addNoteForUser(username, bsMonth, bsDate, enDate, "should not be saved")
        advanceUntilIdle()

        val notes = vm.notesForDateFlow(username, bsMonth, bsDate).first()
        assertTrue(notes.isEmpty())
    }
}
