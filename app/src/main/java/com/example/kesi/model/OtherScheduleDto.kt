package com.example.kesi.model

import android.graphics.Color
import com.example.kesi.domain.OtherSchedule
import com.example.kesi.domain.SecurityLevel
import java.time.LocalDate
import java.time.LocalTime

data class OtherScheduleDto(
    val id: Long,
    val sourceUser: GroupMemberDto,
    val colorValue: Int,
    val title: String,
    val link: String,
    val place: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String,
) {
    fun toDomain() = OtherSchedule(
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
        securityLevel = SecurityLevel.LOW //Todo. 추후 변경
    )
}
