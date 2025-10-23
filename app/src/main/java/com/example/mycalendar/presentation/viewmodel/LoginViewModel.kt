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

    init {
        viewModelScope.launch {
            val existing = loginRepository.getUserCredentials().firstOrNull()
            if (existing?.username == "admin" && existing.password == "admin") {
                Log.i("LoginViewModel", "Existing already logged in")
            }
        }
    }

    fun onUsernameChange(username: String) {
        _loginUiState.update {
            it.copy(username = username)
        }
    }

    fun onPasswordChange(password: String) {
        _loginUiState.update {
            it.copy(password = password)
        }
    }

    fun login(){
        viewModelScope.launch {
            val username = loginUiState.value.username
            val password = loginUiState.value.password

            if (username == "admin" && password == "admin") {
                //write login credentials to datastore object(auth_preferences)
                loginRepository.saveUserCredentials(
                    UserCredentials(
                        username = username,
                        password = password
                    )
                )
                // go to home screen
                Log.i("LoginViewModel", "Login Success")
            } else {
                // show error message
                Log.i("LoginViewModel", "Login Failed")
            }
        }
    }

}