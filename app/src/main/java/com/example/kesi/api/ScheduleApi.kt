package com.example.kesi.api

import com.example.kesi.model.RequestPersonalScheduleDto
import com.example.kesi.model.ScheduleDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ScheduleApi {
    @GET("schedule")
    fun findByMonth(@Query("monthDate") date: String): retrofit2.Call<List<ScheduleDto>>

    @POST("schedule")
    fun addSchedule(@Body personalScheduleDto: RequestPersonalScheduleDto): retrofit2.Call<Long>


}