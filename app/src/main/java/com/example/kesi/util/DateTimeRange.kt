package com.example.kesi.util

import android.view.View.OnClickListener
import java.time.LocalDate
import java.time.LocalTime

class DateTimeRange(private val start: DateTimeTv,
                    private val end: DateTimeTv) {
    fun setStartDate(startDate: LocalDate) {
        start.setDate(startDate)

        if(startDate > end.getDate())
            end.setDate(startDate)
    }

    fun setEndDate(endDate: LocalDate) {
        end.setDate(endDate)

        if(endDate < start.getDate())
            start.setDate(endDate)
    }

    fun setStartTime(startTime: LocalTime) {
        start.setTime(startTime)

        if(start.getDateTime() > end.getDateTime())
            end.setTime(startTime)
    }

    fun setEndTime(endTime: LocalTime) {
        end.setTime(endTime)

        if(end.getDateTime() < start.getDateTime())
            start.setTime(endTime)
    }

    fun getStartDate() = start.getDate()
    fun getEndDate() = end.getDate()
    fun getStartTime() = start.getTime()
    fun getEndTime() = end.getTime()

    fun setStartDateTvSetOnClickListener(l: OnClickListener) {
        start.setDateTvSetOnClickListener(l)
    }

    fun setStartTimeTvSetOnClickListener(l: OnClickListener) {
        start.setTimeTvSetOnClickListener(l)
    }
    fun setEndDateTvSetOnClickListener(l: OnClickListener) {
        end.setDateTvSetOnClickListener(l)
    }

    fun setEndTimeTvSetOnClickListener(l: OnClickListener) {
        end.setTimeTvSetOnClickListener(l)
    }
}
