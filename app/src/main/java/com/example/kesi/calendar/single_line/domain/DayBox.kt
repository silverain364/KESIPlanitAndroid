package com.example.kesi.calendar.single_line.domain

import com.example.kesi.domain.Schedule
import java.time.LocalDate
import kotlin.reflect.KClass

class DayBox (
    val date: LocalDate
){
    private val schedulesTypeMap = SchedulesTypeMap()

    fun addSchedule(schedule: Schedule) {
        schedulesTypeMap.addSchedule(schedule)
    }

    fun getScheduleByType(scheduleType: KClass<out Schedule>): List<Schedule> {
        return schedulesTypeMap.getSchedulesByType(scheduleType)
    }

    fun getFirstPriorityScheduleByType(scheduleType: KClass<out Schedule>): Schedule? {
        return schedulesTypeMap.getFirstPrioryScheduleByType(scheduleType)
    }

    fun getSchedules(): List<Schedule> {
        return schedulesTypeMap.getSchedules()
    }
}