package com.example.mycalendar.ui.test

import com.example.mycalendar.data.FakeCalendarRepository
import com.example.mycalendar.presentation.viewmodel.CalendarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init triggers repository sync and settles`() = runTest {
        val repo = FakeCalendarRepository()
        val vm = CalendarViewModel(repository = repo, skipNetwork = true)

        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
        assertTrue(vm.uiState.value.days.isEmpty())
        assertTrue(repo.calls >= 1)
    }

    @Test
    fun `onNextMonth triggers another repository sync`() = runTest {
        val repo = FakeCalendarRepository()
        val vm = CalendarViewModel(repository = repo, skipNetwork = true)

        advanceUntilIdle()
        val callsAfterInit = repo.calls

        vm.onNextMonth()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
        assertTrue(repo.calls >= callsAfterInit + 1)
    }

    @Test
    fun `repository sync failure is caught and UI settles`() = runTest {
        val repo = FakeCalendarRepository(throwOnSync = IllegalStateException("boom"))
        val vm = CalendarViewModel(repository = repo, skipNetwork = true)

        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
        assertEquals(true, repo.calls >= 1)
    }
}
