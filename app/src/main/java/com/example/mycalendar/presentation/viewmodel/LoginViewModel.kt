package com.example.mycalendar.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycalendar.domain.model.UserCredentials
import com.example.mycalendar.domain.repository.LoginRepository
import com.example.mycalendar.presentation.uistate.LoginUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
): ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun resetLoginState() {
        _loginUiState.value = LoginUiState()
    }

//    init {
//        viewModelScope.launch {
//            val existing = loginRepository.getUserCredentials().firstOrNull()
//            if (existing != null) {
//                Log.i("LoginViewModel", "Existing already logged in")
//            }
//        }
//    }

    fun onUsernameChange(username: String) {
        _loginUiState.update {
            it.copy(username = username, error = null, loginSuccess = false)
        }
    }

    fun onPasswordChange(password: String) {
        _loginUiState.update {
            it.copy(password = password, error = null, loginSuccess = false)
        }
    }

    fun login(){
        viewModelScope.launch {
            val username = loginUiState.value.username
            val password = loginUiState.value.password

            if (username.isBlank() || password.isBlank()) {
                _loginUiState.update { it.copy(error = "Enter username and password") }
                return@launch
            }

            val registered = loginRepository.getUserCredentials().firstOrNull()

            when {
                // No registered user found (fresh or after logout) -> accept and save this as the account
                registered == null -> {
                    loginRepository.saveUserCredentials(UserCredentials(username, password))
                    _loginUiState.update { it.copy(loginSuccess = true, error = null) }
                }
                // Match registered user
                username == registered.username && password == registered.password -> {
                    _loginUiState.update { it.copy(loginSuccess = true, error = null) }
                }
                else -> {
                    _loginUiState.update { it.copy(error = "Invalid credentials") }
                }
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            // Ensure your repository clears saved credentials
            // e.g., loginRepository.clearUserCredentials()
            resetLoginState()
        }
    }

}