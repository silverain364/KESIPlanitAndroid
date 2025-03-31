package com.example.kesi.domain

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.scaleMatrix
import com.example.kesi.R
import java.time.LocalDate
import java.time.LocalTime

class GroupSchedule(
    id: Long,
    start: LocalDate,
    end: LocalDate,
    startTime: LocalTime,
    endTime: LocalTime,
    color: Color,
    title: String,
    description: String,
    link: String,
    place: String,
    securityLevel: SecurityLevel,
    private val sourceCalendarId: Long
    ) : Schedule(id, start, end, startTime, endTime, color, title, description, link, place, securityLevel) {
    override fun getType(): ScheduleType {
        return ScheduleType.GROUP
    }

    override fun getSingleLineColor(context: Context): Int {
        return this.color.toArgb()
    }

}