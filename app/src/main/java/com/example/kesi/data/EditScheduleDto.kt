package com.example.kesi.data

import android.os.Parcelable
import androidx.core.graphics.toColor
import com.example.kesi.domain.Schedule
import com.example.kesi.domain.SecurityLevel
import com.example.kesi.model.GroupMemberDto
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalTime

@Parcelize

data class EditScheduleDto(
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
) : Parcelable {
    companion object {
        fun from(schedule: Schedule) = EditScheduleDto(
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

    fun toDomain() = Schedule(
        id = id,
        color = colorValue.toColor(),
        title = title,
        link = link,
        place = place,
        description = description,
        start = LocalDate.parse(startDate),
        startTime = LocalTime.parse(startTime),
        end = LocalDate.parse(endDate),
        endTime = LocalTime.parse(endTime),
        securityLevel = securityLevel
    )
}
