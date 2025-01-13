package com.example.kesi.api

import com.example.kesi.model.JoinRequestDto
import com.example.kesi.model.UserInfoDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @GET("user")
    fun getByUid(): Call<UserInfoDto>

    @POST("user")
    fun join(@Body joinRequestDto: JoinRequestDto): Call<String>
}