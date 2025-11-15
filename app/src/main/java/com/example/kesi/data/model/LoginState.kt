package com.example.kesi.data.model

import com.google.firebase.auth.FirebaseUser

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Error(val message: String?) : LoginState()

    // Firebase 로그인 성공
    data class FirebaseLoggedIn(val user: FirebaseUser) : LoginState()

    // 회원 정보 서버 확인 완료, 입력 필요 없음
    data object Success : LoginState()

    // 회원 정보 서버에 없거나 입력 필요
    data object RequireProfileInput : LoginState()
}