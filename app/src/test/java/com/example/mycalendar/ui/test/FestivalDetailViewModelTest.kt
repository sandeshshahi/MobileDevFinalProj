package com.example.mycalendar.ui.test

import android.graphics.Bitmap
import com.example.mycalendar.TestApp
import com.example.mycalendar.core.dispatcher.AppDispatchers
import com.example.mycalendar.data.FakeFestivalRepository
import com.example.mycalendar.presentation.viewmodel.FestivalDetailViewModel
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = TestApp::class, manifest = Config.NONE)
class FestivalDetailViewModelTest {
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var fakeRepo: FakeFestivalRepository

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeFestivalRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun onePxBitmap(): Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    @Test
    fun `when repository returns success uiState contains description, image and no error`() = runTest {
        fakeRepo.textResponse = Result.success("Fake festival description")
        fakeRepo.imageResponse = Result.success(listOf(onePxBitmap()))

        val vm = FestivalDetailViewModel(
            festivalName = "भाइटीका\nBrother-Sister Tika",
            bsMonth = "कार्तिक",
            bsDate = "६",
            enDate = "Oct 23",
            festivalRepository = fakeRepo,
            dispatchers = object : AppDispatchers { override val io = testDispatcher }
        )
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(!state.isLoading)
        assertNull(state.error)
        assertTrue(state.description?.isNotBlank() == true)
        assertTrue(state.image.isNotEmpty())
    }

    @Test
    fun `when both text and image fail friendly error is set`() = runTest {
        fakeRepo.textResponse = Result.failure(IllegalStateException("text fail"))
        fakeRepo.imageResponse = Result.failure(IllegalStateException("image fail"))

        val vm = FestivalDetailViewModel(
            festivalName = "द्दितीया\nभाइटीका, किजा पूजा",
            bsMonth = "कार्तिक",
            bsDate = "६",
            enDate = "Oct 23",
            festivalRepository = fakeRepo,
            dispatchers = object : AppDispatchers { override val io = testDispatcher }
        )
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(!state.isLoading)
        assertEquals("Content is temporarily unavailable. Please try again.", state.error)
        assertNotNull(state.description)
        assertTrue(state.description!!.isNotBlank())
        assertTrue(state.image.isEmpty())
    }
}
