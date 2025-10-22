package com.example.mycalendar.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val USER_REGION = stringPreferencesKey("user_region")
    }

    val userRegionFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_REGION] ?: "Kathmandu" // Default value
        }

    suspend fun saveUserRegion(region: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_REGION] = region
        }
    }
}
