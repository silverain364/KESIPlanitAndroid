package com.example.kesi.data.remote

import com.example.kesi.data.model.FriendsDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendsApi {
    @POST("friends")
    fun addFriends(@Query("email") email: String): Call<String>

    @GET("friends")
    fun getFriends(): Call<List<com.example.kesi.data.model.FriendsDto>>
}