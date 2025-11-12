package com.example.kesi.data.model

import com.google.firebase.auth.FirebaseUser

sealed class LoginState {
    data object Idle: LoginState()
    data object Loading: LoginState()
    data class Success(val user: FirebaseUser): LoginState()
    data class Error(val message: String?): LoginState()
}