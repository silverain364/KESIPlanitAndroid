package com.example.kesi.api

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface FCMApi {
    @POST("fcm-token")
    fun addFCMToken(@Query("token") token: String): Call<String>


}