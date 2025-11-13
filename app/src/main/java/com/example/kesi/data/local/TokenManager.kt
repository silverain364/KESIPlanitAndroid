package com.example.kesi.data.local

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor() {
    @Volatile
    private var token: String? = null

    fun setToken(value: String?) {
        this.token= value
    }

    fun getToken(): String? = token
}