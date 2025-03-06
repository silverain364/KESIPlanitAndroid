package com.example.kesi.model

import android.graphics.Color
import androidx.core.graphics.toColor
import com.example.kesi.domain.Schedule
import java.time.LocalDate
import java.time.LocalTime

data class ScheduleDto(
    val id: Long,
    val colorId: String,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String
) {

    fun toDomain() = Schedule(
        id = id,
        start = LocalDate.parse(startDate),
        end = LocalDate.parse(endDate),
        startTime = LocalTime.parse(startTime),
        endTime = LocalTime.parse(endTime),
        color = Color.parseColor(colorId).toColor(),
        title = title,
        description = description
    )
}