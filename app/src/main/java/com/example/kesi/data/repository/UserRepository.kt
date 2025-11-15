package com.example.kesi.data.repository

import com.example.kesi.data.model.UserInfoDto
import com.example.kesi.data.remote.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepository @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun existsUser(): Result<Boolean> =
        suspendCoroutine { continuation ->
            userApi.get().enqueue(object: Callback<UserInfoDto> {
                override fun onResponse(call: Call<UserInfoDto>, response: Response<UserInfoDto>) {
                    continuation.resume(Result.success(response.isSuccessful))
                }

                override fun onFailure(call: Call<UserInfoDto>, t: Throwable) {
                    continuation.resume(Result.failure(t))
                }
            })
        }
}