package com.example.kesi.application

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.example.kesi.data.local.AuthLocalDataSource
import com.example.kesi.data.repository.login.FcmRepository
import com.example.kesi.data.repository.login.FirebaseAuthRepository
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginService @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val authLocalDataSource: AuthLocalDataSource,
    private val fcmRepository: FcmRepository
) {

    private suspend fun handleFirebaseLogin(
        firebaseResult: Result<FirebaseUser>
    ): Result<FirebaseUser> {
        return firebaseResult.fold(
            onSuccess = { user ->
                val token = user.getIdToken(true).await().token!!
                authLocalDataSource.saveToken(token)
                fcmRepository.sendFcmToken()
                Result.success(user)
            },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> {
        val firebaseResult = firebaseAuthRepository.loginWithEmail(email, password);
        return this.handleFirebaseLogin(firebaseResult)
    }

    suspend fun onGoogleLogin(credential: Credential): Result<FirebaseUser> {
        if (credential !is CustomCredential || credential.type
            != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            Log.d("LoginViewModel", credential.type)
            return Result.failure(IllegalArgumentException("유효하지 않는 Credential")) //Todo. 추후 예외 변경
        }

        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

        // Firebase Auth Credential 생성
        val firebaseCredential =
            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

        val firebaseResult = firebaseAuthRepository.loginWithCredential(firebaseCredential);
        return handleFirebaseLogin(firebaseResult)
    }




}