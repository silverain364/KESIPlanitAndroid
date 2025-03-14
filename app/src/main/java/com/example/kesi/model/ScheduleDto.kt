package com.example.kesi.model

import android.graphics.Color
import androidx.core.graphics.toColor
import com.example.kesi.domain.Schedule
import com.example.kesi.domain.SecurityLevel
import java.time.LocalDate
import java.time.LocalTime

data class ScheduleDto(
    val id: Long,
    val makerName: GroupMemberDto,
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
    fun toDomain() = Schedule(
        id = id,
        start = LocalDate.parse(startDate),
        end = LocalDate.parse(endDate),
        startTime = LocalTime.parse(startTime),
        endTime = LocalTime.parse(endTime),
        color = Color.valueOf(colorValue),
        title = title,
        description = description,
        link = link ?: " ",
        place = place ?: " ",
        securityLevel = securityLevel
    )
}