package com.example.kesi.api

import com.example.kesi.model.ScheduleDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ScheduleApi {
    @GET("schedule")
    fun findByMonth(@Query("monthDate") date: String): retrofit2.Call<List<ScheduleDto>>

}