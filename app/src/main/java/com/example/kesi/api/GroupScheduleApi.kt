package com.example.kesi.api

import com.example.kesi.domain.OtherSchedule
import com.example.kesi.model.GroupScheduleDto
import com.example.kesi.model.OtherScheduleDto
import retrofit2.Call
import retrofit2.http.*

interface GroupScheduleApi {
    @GET("schedule/group")
    fun findInMonth(@Query("monthDate") date: String): Call<List<GroupScheduleDto>>

    @GET("schedule/other")
    fun findOthersInMonth(@Query("monthDate") date:String, @Query("gid") gid: Long): Call<List<OtherScheduleDto>>

//    @POST("schedule/group")
//    fun addSchedule(@Body personalScheduleDto: RequestPersonalScheduleDto): retrofit2.Call<Long>
//
//    @DELETE("schedule/group")
//    fun deleteSchedule(@Query("scheduleId") scheduleId: Long): retrofit2.Call<Void>
//
//    @PATCH("schedule/group")
//    fun updateSchedule(@Body updateScheduleDto: RequestPersonalUpdateScheduleDto): retrofit2.Call<ScheduleDto>

}