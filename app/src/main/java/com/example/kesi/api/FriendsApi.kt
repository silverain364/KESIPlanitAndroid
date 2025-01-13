package com.example.kesi.api

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendsApi {
    @POST("friends")
    fun addFriends(@Query("email") email: String): Call<String>
}