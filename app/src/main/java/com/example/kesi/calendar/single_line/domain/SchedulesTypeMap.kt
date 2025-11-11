package com.example.kesi.calendar.single_line.domain

import com.example.kesi.domain.Schedule
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KClass

class SchedulesTypeMap {
    private val scheduleTypes = Schedule::class.sealedSubclasses
    private val schedulesTypePriorityMap = HashMap<KClass<out Schedule>, PriorityQueue<Schedule>>()
    private val scheduleMap = HashMap<Long, Schedule>()

    init {
        scheduleTypes.forEach {
            schedulesTypePriorityMap[it] = PriorityQueue<Schedule> { a, b ->
                (a.start.toEpochDay() - b.start.toEpochDay()).toInt()
            }
        }
    }

    fun addSchedule(schedule: Schedule) {
        scheduleMap[schedule.id] = schedule
        schedulesTypePriorityMap[schedule::class]!!.add(schedule)
    }

    fun removeSchedule(schedule: Schedule) {
        scheduleMap.remove(schedule.id)
    }

    fun getFirstPrioryScheduleByType(scheduleType: KClass<out Schedule>): Schedule? {
        val targetTypeSchedules = schedulesTypePriorityMap[scheduleType]!!
        while(targetTypeSchedules.isNotEmpty()) {
            val schedule = targetTypeSchedules.peek()!!
            if(scheduleMap.containsKey(schedule.id)) return schedule

            targetTypeSchedules.poll()
        }

        return null
    }

    fun getSchedulesByType(scheduleType: KClass<out Schedule>): List<Schedule> {
        return emptyList()  //Todo. 추후 구현
    }

    fun getSchedules(): List<Schedule> {
        return scheduleMap.values.toList()
    }
}

