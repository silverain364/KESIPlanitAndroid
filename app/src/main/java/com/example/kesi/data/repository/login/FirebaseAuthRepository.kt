package com.example.kesi.data.repository.login

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> =
        suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { continuation.resume(Result.success(it.user!!)) }
                .addOnFailureListener { continuation.resume(Result.failure(it))}
        }

    suspend fun loginWithCredential(credential: AuthCredential): Result<FirebaseUser> =
        suspendCoroutine { continuation ->
            auth.signInWithCredential(credential)
                .addOnSuccessListener { continuation.resume(Result.success(it.user!!)) }
                .addOnFailureListener { continuation.resume(Result.failure(it))}
        }
}