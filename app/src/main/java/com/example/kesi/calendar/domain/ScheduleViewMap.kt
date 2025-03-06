package com.example.kesi.calendar.domain

import android.view.View
import com.example.kesi.domain.Schedule

class ScheduleViewMap {
    private val scheduleViewMap: HashMap<Schedule, ArrayList<View>> = HashMap() //schedule과 view를 연결

    operator fun get(schedule: Schedule): List<View> =
        scheduleViewMap[schedule] ?: arrayListOf()

    fun add(schedule: Schedule, view: View) {
        if(!scheduleViewMap.containsKey(schedule))
            scheduleViewMap[schedule] = ArrayList()

        scheduleViewMap[schedule]!!.add(view)
    }

    fun add(schedule: Schedule, viewList: List<View>) {
        if(!scheduleViewMap.containsKey(schedule))
            scheduleViewMap[schedule] = ArrayList()

        scheduleViewMap[schedule]!!.addAll(viewList)
    }

    fun remove(schedule: Schedule): List<View> {
        return scheduleViewMap.remove(schedule) ?: arrayListOf()
    }
}