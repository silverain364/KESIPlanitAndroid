package com.example.kesi.api

import com.example.kesi.model.FriendsDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendsApi {
    @POST("friends")
    fun addFriends(@Query("email") email: String): Call<String>

    @GET("friends")
    fun getFriends(): Call<List<FriendsDto>>
}