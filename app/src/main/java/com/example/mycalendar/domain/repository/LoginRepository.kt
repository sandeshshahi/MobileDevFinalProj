package com.example.mycalendar.domain.repository

import com.example.mycalendar.domain.model.UserCredentials
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun saveUserCredentials(userCredentials: UserCredentials)
    fun getUserCredentials(): Flow<UserCredentials?>
}