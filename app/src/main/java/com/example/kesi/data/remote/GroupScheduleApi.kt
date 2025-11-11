package com.example.kesi.data.remote

import com.example.kesi.data.model.GroupScheduleDto
import com.example.kesi.data.model.OtherScheduleDto
import retrofit2.Call
import retrofit2.http.*

interface GroupScheduleApi {
    @GET("schedule/group")
    fun findInMonth(@Query("monthDate") date: String): Call<List<com.example.kesi.data.model.GroupScheduleDto>>

    @GET("schedule/other")
    fun findOthersInMonth(@Query("monthDate") date:String, @Query("gid") gid: Long): Call<List<com.example.kesi.data.model.OtherScheduleDto>>

//    @POST("schedule/group")
//    fun addSchedule(@Body personalScheduleDto: RequestPersonalScheduleDto): retrofit2.Call<Long>
//
//    @DELETE("schedule/group")
//    fun deleteSchedule(@Query("scheduleId") scheduleId: Long): retrofit2.Call<Void>
//
//    @PATCH("schedule/group")
//    fun updateSchedule(@Body updateScheduleDto: RequestPersonalUpdateScheduleDto): retrofit2.Call<ScheduleDto>

}