package com.example.kesi.data

import com.example.kesi.domain.Schedule
import java.time.LocalDate
import kotlin.reflect.KClass

sealed class GroupCalendarScheduleSummaryItem {
    data class Day(val date: LocalDate): GroupCalendarScheduleSummaryItem()
    data class Kind(val type: KClass<out Schedule>): GroupCalendarScheduleSummaryItem()
    data class Item(val schedule: Schedule): GroupCalendarScheduleSummaryItem()
}