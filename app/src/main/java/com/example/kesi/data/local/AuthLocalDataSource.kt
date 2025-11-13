package com.example.kesi.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val dataSource: DataStore<Preferences>,
    private val tokenManager: TokenManager
) {
    companion object {
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    suspend fun saveToken(token: String) {
        dataSource.edit { prefs ->
            prefs[KEY_AUTH_TOKEN] = token
        }
        tokenManager.setToken(token)
    }

    fun getToken(): Flow<String?> {
        return dataSource.data.map { prefs ->
            prefs[KEY_AUTH_TOKEN]
        }
    }

    suspend fun clearToken() {
        dataSource.edit { pref ->
            pref.remove(KEY_AUTH_TOKEN)
        }
        tokenManager.setToken(null)
    }
}