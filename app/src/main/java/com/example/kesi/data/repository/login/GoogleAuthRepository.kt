package com.example.kesi.data.repository.login

import android.content.Context
import android.content.Intent
import com.example.kesi.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope

class GoogleAuthRepository(private val googleSignClient: GoogleSignInClient) {
//    private val googleSignClient = GoogleSignIn.getClient(
//        context,
//        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //로그인 옵션
//            .requestIdToken(context.getString(R.string.default_web_client_id)) //User Token
//            .requestServerAuthCode(context.getString(R.string.default_web_client_id)) //AuthCode를 받기 위한 설정
//            .requestEmail()
//            .requestScopes(Scope("https://www.googleapis.com/auth/calendar")) //특정 권한(캘린더 권한) 추가 요청
//            .build()
//    )

    fun getSignInIntent(): Intent = googleSignClient.signInIntent

    fun getAccountFromIntent(data: Intent?): GoogleSignInAccount? {
        return GoogleSignIn.getSignedInAccountFromIntent(data).result
    }
}