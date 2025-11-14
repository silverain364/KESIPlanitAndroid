package com.example.kesi.data.repository.login

import android.content.Context
import android.content.Intent
import com.example.kesi.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import javax.inject.Inject
import javax.inject.Named

class GoogleAuthRepository @Inject constructor(
    @param:Named("login") private val googleSignClient: GoogleSignInClient) {

    fun getSignInIntent(): Intent = googleSignClient.signInIntent

    fun getAccountFromIntent(data: Intent?): GoogleSignInAccount? {
        return GoogleSignIn.getSignedInAccountFromIntent(data).result
    }
}