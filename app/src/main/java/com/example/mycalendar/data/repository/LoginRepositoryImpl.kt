package com.example.mycalendar.data.repository

import com.example.mycalendar.data.local.PreferencesDataSource
import com.example.mycalendar.domain.model.UserCredentials
import com.example.mycalendar.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow

class LoginRepositoryImpl(
    private val preferencesDataSource: PreferencesDataSource
): LoginRepository {
    override suspend fun saveUserCredentials(userCredentials: UserCredentials) {
        preferencesDataSource.saveUserCredentials(userCredentials)
    }
    override fun getUserCredentials(): Flow<UserCredentials?> {
        return preferencesDataSource.getUserCredentials()
    }
}