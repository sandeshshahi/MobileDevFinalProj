package com.example.mycalendar.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mycalendar.data.local.PreferencesDataSource
import com.example.mycalendar.data.repository.LoginRepositoryImpl
import com.example.mycalendar.presentation.viewmodel.LoginViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.mycalendar.domain.model.UserCredentials
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onRegistered: () -> Unit = {}
) {
    val context = LocalContext.current
    val loginRepo = remember { LoginRepositoryImpl(PreferencesDataSource(context)) }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
            Spacer(modifier = Modifier.padding(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password")},
                visualTransformation = PasswordVisualTransformation(),
            )
            Spacer(modifier = Modifier.padding(12.dp))
            Button(onClick = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    scope.launch {
                        loginRepo.saveUserCredentials(UserCredentials(username, password))
                        onRegistered()
                    }
                }
            }) {
                Text("Register")
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}
