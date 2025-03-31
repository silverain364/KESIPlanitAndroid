package com.example.kesi.calendar.single_line.domain

import com.example.kesi.domain.Schedule
import com.example.kesi.domain.ScheduleType
import java.time.LocalDate
import java.util.PriorityQueue

class DayBox (
    val date: LocalDate
){
    private val schedulesTypeMap = SchedulesTypeMap()

    fun addSchedule(schedule: Schedule) {
        schedulesTypeMap.addSchedule(schedule)
    }

    fun getScheduleByType(scheduleType: ScheduleType): List<Schedule> {
        return schedulesTypeMap.getSchedulesByType(scheduleType)
    }

    fun getFirstPriorityScheduleByType(scheduleType: ScheduleType): Schedule? {
        return schedulesTypeMap.getFirstPrioryScheduleByType(scheduleType)
    }

    fun getSchedules(): List<Schedule> {
        return schedulesTypeMap.getSchedules()
    }
}