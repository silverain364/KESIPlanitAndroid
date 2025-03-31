package com.example.kesi.model

import android.graphics.Color
import com.example.kesi.domain.GroupSchedule
import com.example.kesi.domain.PersonalSchedule
import com.example.kesi.domain.SecurityLevel
import java.time.LocalDate
import java.time.LocalTime

data class GroupScheduleDto(
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
    val securityLevel: SecurityLevel,
    val sourceCalendarId: Long
) {
    fun toDomain() = GroupSchedule(
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
        securityLevel = securityLevel,
        sourceCalendarId = sourceCalendarId
    )

    fun toPersonalSchedule() = PersonalSchedule(
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