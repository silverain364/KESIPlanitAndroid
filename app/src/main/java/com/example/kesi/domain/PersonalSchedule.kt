package com.example.kesi.domain

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.kesi.R
import java.time.LocalDate
import java.time.LocalTime

class PersonalSchedule(
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
    securityLevel: SecurityLevel
): Schedule(id, start, end, startTime, endTime, color, title, description, link, place, securityLevel) {
    override fun getType(): ScheduleType {
        return ScheduleType.PERSONAL
    }

    override fun getSingleLineColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.white)
    }
}