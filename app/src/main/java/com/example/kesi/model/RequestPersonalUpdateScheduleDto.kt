package com.example.kesi.model

import android.graphics.Color
import com.example.kesi.data.EditScheduleDto
import com.example.kesi.domain.Schedule
import com.example.kesi.domain.SecurityLevel
import java.time.LocalDate
import java.time.LocalTime

data class RequestPersonalUpdateScheduleDto (
    val id: Long,
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
) {
    companion object {
        fun from(schedule: Schedule) = RequestPersonalUpdateScheduleDto(
            id = schedule.id,
            colorValue = schedule.color.toArgb(),
            title = schedule.title,
            link = schedule.link,
            place = schedule.place,
            description = schedule.description,
            startDate = schedule.start.toString(),
            startTime = schedule.startTime.toString(),
            endDate = schedule.end.toString(),
            endTime = schedule.endTime.toString(),
            securityLevel = schedule.securityLevel
        )
    }
}