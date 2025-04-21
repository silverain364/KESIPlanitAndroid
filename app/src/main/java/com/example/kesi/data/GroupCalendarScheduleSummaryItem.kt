package com.example.kesi.data

import com.example.kesi.domain.Schedule
import com.example.kesi.domain.ScheduleType
import java.time.LocalDate

sealed class GroupCalendarScheduleSummaryItem {
    data class Day(val date: LocalDate): GroupCalendarScheduleSummaryItem()
    data class Kind(val type: ScheduleType): GroupCalendarScheduleSummaryItem()
    data class Item(val schedule: Schedule): GroupCalendarScheduleSummaryItem()
}