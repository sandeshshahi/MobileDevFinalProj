package com.example.mycalendar.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mycalendar.data.local.PreferencesDataSource
import com.example.mycalendar.data.repository.LoginRepositoryImpl
import com.example.mycalendar.presentation.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel {
        LoginViewModel(
            LoginRepositoryImpl(
                PreferencesDataSource(context)
            )
        )
    }
    val loginUiState by loginViewModel.loginUiState.collectAsStateWithLifecycle()

    LaunchedEffect(loginUiState.loginSuccess) {
        if (loginUiState.loginSuccess) onLoginSuccess()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = loginUiState.username,
                onValueChange = { loginViewModel.onUsernameChange(it) },
                label = { Text(text = "Username") }
            )
            Spacer(modifier.padding(4.dp))
            OutlinedTextField(
                value = loginUiState.password,
                onValueChange = { loginViewModel.onPasswordChange(it) },
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier.padding(8.dp))
            Button(
                onClick = { loginViewModel.login() }
            ) {
                Text("Login")
            }

        }

    }

}