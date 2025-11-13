package com.example.kesi.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val dataSource: DataStore<Preferences>
) {

    suspend fun saveToken(token: String) {
        dataSource.edit { prefs ->
//            prefs["AUTH_TOKEN"] = token
        }
    }
}