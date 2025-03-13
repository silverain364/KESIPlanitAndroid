package com.example.kesi.model

import com.example.kesi.data.AddScheduleDto
import com.example.kesi.domain.SecurityLevel

data class RequestPersonalScheduleDto(
    val colorValue: Int,
    val title: String,
    val link: String,
    val place: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String,
    val securityLevel: SecurityLevel
){
    companion object {
        fun toDto(addScheduleDto: AddScheduleDto) = RequestPersonalScheduleDto(
            colorValue = addScheduleDto.colorValue,
            title = addScheduleDto.title,
            link = addScheduleDto.link,
            place = addScheduleDto.place,
            description = addScheduleDto.description,
            startDate = addScheduleDto.startDate,
            endDate = addScheduleDto.endDate,
            startTime = addScheduleDto.startTime,
            endTime = addScheduleDto.endTime,
            securityLevel = addScheduleDto.securityLevel
        )
    }
}