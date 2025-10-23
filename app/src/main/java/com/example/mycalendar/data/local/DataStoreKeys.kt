package com.example.mycalendar.data.local

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
    val USER_NAME: Preferences.Key<String> = stringPreferencesKey("USER_NAME")
    val PASSWORD: Preferences.Key<String> = stringPreferencesKey("PASSWORD")
}