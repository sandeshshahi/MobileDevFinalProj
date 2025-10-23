// Kotlin
package com.example.mycalendar.ui.test

import com.example.mycalendar.data.FakeLoginRepository
import com.example.mycalendar.domain.model.UserCredentials
import com.example.mycalendar.presentation.viewmodel.LoginViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var fakeRepo: FakeLoginRepository
    private lateinit var vm: LoginViewModel

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeLoginRepository(initial = null)
        vm = LoginViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with empty fields returns error`() = runTest {
        vm.login()
        advanceUntilIdle()
        val state = vm.loginUiState.value
        assertEquals("Enter username and password", state.error)
    }

    @Test
    fun `first time login saves credentials and reports success`() = runTest {
        vm.onUsernameChange("alice")
        vm.onPasswordChange("secret")
        vm.login()
        advanceUntilIdle()
        val state = vm.loginUiState.value
        assertTrue(state.loginSuccess)
        val saved = fakeRepo.getUserCredentials().first()
        assertEquals("alice", saved?.username)
        assertEquals("secret", saved?.password)
    }

    @Test
    fun `login with wrong password returns invalid credentials`() = runTest {
        fakeRepo = FakeLoginRepository(UserCredentials("bob", "rightpass"))
        vm = LoginViewModel(fakeRepo)

        vm.onUsernameChange("bob")
        vm.onPasswordChange("wrong")
        vm.login()
        advanceUntilIdle()
        val state = vm.loginUiState.value
        assertEquals("Invalid credentials", state.error)
    }
}
