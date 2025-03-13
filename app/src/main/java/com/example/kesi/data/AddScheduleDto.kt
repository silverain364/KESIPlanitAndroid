package com.example.kesi.data

import android.graphics.Color
import android.os.Parcelable
import com.example.kesi.domain.Schedule
import com.example.kesi.domain.SecurityLevel
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalTime

@Parcelize
data class AddScheduleDto(
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
): Parcelable {
    fun toDomain(id: Long) = Schedule(
        id = id,
        color = Color.valueOf(colorValue),
        title = title,
        description = description,
        place = place,
        link = link,
        start = LocalDate.parse(startDate),
        end = LocalDate.parse(endDate),
        startTime =  LocalTime.parse(startTime),
        endTime = LocalTime.parse(endTime)
    )
}
