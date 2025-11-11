package com.example.kesi.data.remote

import com.example.kesi.data.model.GroupDto
import com.example.kesi.data.model.GroupMakeInfoRequestDto
import com.example.kesi.data.model.GroupSimpleDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GroupApi {
    @POST("group")
    fun creatGroup(@Body groupMakeInfoRequestDto: com.example.kesi.data.model.GroupMakeInfoRequestDto) : Call<Long>

    @GET("group")
    fun getGroup(@Query("gid") gid: Long) : Call<com.example.kesi.data.model.GroupDto>

    @GET("group/list")
    fun getAllGroups() : Call<List<com.example.kesi.data.model.GroupSimpleDto>>


}