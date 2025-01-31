package com.example.kesi.api

import com.example.kesi.model.AlarmDataDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AlarmApi {
    @GET("alarm")
    fun getAlarm(@Query("alarmId") alarmId: String): Call<AlarmDataDto>
    @GET("alarm/all")
    fun getAlarmAll(): Call<List<AlarmDataDto>>

}