package com.example.kesi.data.remote

import com.example.kesi.data.model.JoinRequestDto
import com.example.kesi.data.model.UserInfoDto
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApi {
    @GET("user")
    fun getByUid(): Call<com.example.kesi.data.model.UserInfoDto>

    @POST("user")
    fun join(@Body joinRequestDto: com.example.kesi.data.model.JoinRequestDto): Call<String>

    @Multipart
    @POST("user/profile")
    fun uploadProfile(@Part image:MultipartBody.Part): Call<String>
}