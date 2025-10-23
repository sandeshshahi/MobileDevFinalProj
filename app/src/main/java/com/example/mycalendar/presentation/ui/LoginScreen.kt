package com.example.mycalendar.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
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

    LaunchedEffect(Unit) {
        loginViewModel.resetLoginState()
    }

    LaunchedEffect(loginUiState.loginSuccess) {
        if (loginUiState.loginSuccess) onLoginSuccess()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = loginUiState.username,
                onValueChange = { loginViewModel.onUsernameChange(it) },
                label = { Text(text = "Username") },
                singleLine = true,
//                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(12.dp))
            OutlinedTextField(
                value = loginUiState.password,
                onValueChange = { loginViewModel.onPasswordChange(it) },
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
//                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(8.dp))
            Button(
                onClick = { loginViewModel.login() },
//                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            loginUiState.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(text = it)
            }

        }

    }

}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}