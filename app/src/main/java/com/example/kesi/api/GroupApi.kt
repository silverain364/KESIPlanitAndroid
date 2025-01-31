package com.example.kesi.api

import com.example.kesi.model.GroupDto
import com.example.kesi.model.GroupMakeInfoRequestDto
import com.example.kesi.model.GroupSimpleDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GroupApi {
    @POST("group")
    fun creatGroup(@Body groupMakeInfoRequestDto: GroupMakeInfoRequestDto) : Call<Long>

    @GET("group")
    fun getGroup(@Query("gid") gid: Long) : Call<GroupDto>

    @GET("group/list")
    fun getAllGroups() : Call<List<GroupSimpleDto>>
}