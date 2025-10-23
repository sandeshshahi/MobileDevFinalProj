package com.example.mycalendar.data

import com.example.mycalendar.domain.model.UserCredentials
import com.example.mycalendar.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLoginRepository(
    initial: UserCredentials? = null
) : LoginRepository {
    private val flow = MutableStateFlow(initial)

    override suspend fun saveUserCredentials(userCredentials: UserCredentials) {
        flow.value = userCredentials
    }

    override fun getUserCredentials(): Flow<UserCredentials?> = flow
}