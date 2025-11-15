package com.example.kesi.data.repository.login

import com.example.kesi.data.remote.FCMApi
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import retrofit2.Callback
import retrofit2.Response

class FcmRepository @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging,
    private val fcmApi: FCMApi
) {
    suspend fun sendFcmToken(): Result<Unit> =
        suspendCoroutine { continuation ->
            firebaseMessaging.token.addOnCompleteListener() {
                if(!it.isSuccessful) {
                    continuation.resume(Result.failure(it.exception ?: IllegalArgumentException("FCM 토큰 가져오기 실패")))
                    return@addOnCompleteListener
                }

                fcmApi.addFCMToken(it.result).enqueue(object: Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        continuation.resume(Result.success(Unit))
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        continuation.resume(Result.failure(t))
                    }

                })
            }

            firebaseMessaging.token.addOnFailureListener {
                continuation.resume(Result.failure(it))
            }
        }
}