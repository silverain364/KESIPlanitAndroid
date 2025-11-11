package com.example.kesi.data.remote

import com.example.kesi.data.model.RequestPersonalScheduleDto
import com.example.kesi.data.model.RequestPersonalUpdateScheduleDto
import com.example.kesi.data.model.ScheduleDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface PersonalScheduleApi {
    @GET("schedule")
    fun findByMonth(@Query("monthDate") date: String): retrofit2.Call<List<com.example.kesi.data.model.ScheduleDto>>

    @POST("schedule")
    fun addSchedule(@Body personalScheduleDto: com.example.kesi.data.model.RequestPersonalScheduleDto): retrofit2.Call<Long>

    @DELETE("schedule")
    fun deleteSchedule(@Query("scheduleId") scheduleId: Long): retrofit2.Call<Void>

    @PATCH("schedule")
    fun updateSchedule(@Body updateScheduleDto: com.example.kesi.data.model.RequestPersonalUpdateScheduleDto): retrofit2.Call<com.example.kesi.data.model.ScheduleDto>
}