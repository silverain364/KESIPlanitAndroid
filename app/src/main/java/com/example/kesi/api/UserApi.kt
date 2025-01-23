package com.example.kesi.api

import com.example.kesi.model.JoinRequestDto
import com.example.kesi.model.UserInfoDto
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApi {
    @GET("user")
    fun getByUid(): Call<UserInfoDto>

    @POST("user")
    fun join(@Body joinRequestDto: JoinRequestDto): Call<String>

    @Multipart
    @POST("user/profile")
    fun uploadProfile(@Part image:MultipartBody.Part): Call<String>
}