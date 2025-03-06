package com.example.kesi.calendar.domain

import android.view.View
import com.example.kesi.domain.Schedule

class DayLineScheduleMap { //일급 컬렉션
    private val scheduleMap: HashMap<Schedule, ArrayList<View>> = HashMap()

    fun put(schedule: Schedule, scheduleViewList: ArrayList<View>) {
        scheduleMap[schedule] = scheduleViewList
    }

    fun add(schedule: Schedule, scheduleView: View) {
        if(!scheduleMap.containsKey(schedule))
            scheduleMap[schedule] = ArrayList()

        scheduleMap[schedule]!!.add(scheduleView)
    }

    fun get(schedule: Schedule): ArrayList<View> {
        if(!scheduleMap.containsKey(schedule)) return arrayListOf()
        return scheduleMap[schedule]!!
    }

    fun remove(schedule: Schedule): ArrayList<View> {
        if(!scheduleMap.containsKey(schedule)) return arrayListOf()
        return scheduleMap.remove(schedule)!!
    }
}