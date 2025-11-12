package com.example.kesi.application

import com.example.kesi.data.User
import com.example.kesi.data.repository.login.BackendAuthRepository
import com.example.kesi.data.repository.login.FirebaseAuthRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginService @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
//    private val backendAuthRepository: BackendAuthRepository
) {
    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> {
        val firebaseResult = firebaseAuthRepository.loginWithEmail(email, password);
        return firebaseResult.fold(
            onSuccess = { user ->
                val token = user.getIdToken(true).await().token!!
                return Result.success(user)
//                backendAuthRepository.
            },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun loginWithCredential(credential: AuthCredential): Result<FirebaseUser> {
        val firebaseResult = firebaseAuthRepository.loginWithCredential(credential);
        return firebaseResult.fold(
            onSuccess = { user ->
                val token = user.getIdToken(true).await().token!!
                return Result.success(user)
//                backendAuthRepository.
            },
            onFailure = { Result.failure(it) }
        )
    }
}