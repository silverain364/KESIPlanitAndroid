package com.example.kesi.api

import com.example.kesi.model.GroupMakeInfoRequestDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GroupApi {
    @POST("group")
    fun creatGroup(@Body groupMakeInfoRequestDto: GroupMakeInfoRequestDto) : Call<Long>
}