package com.example.kesi.data.remote

import com.example.kesi.data.model.AlarmDataDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AlarmApi {
    @GET("alarm")
    fun getAlarm(@Query("alarmId") alarmId: String): Call<com.example.kesi.data.model.AlarmDataDto>
    @GET("alarm/all")
    fun getAlarmAll(): Call<List<com.example.kesi.data.model.AlarmDataDto>>
}