package com.example.kesi.calendar.single_line.domain

import com.example.kesi.domain.PersonalSchedule
import com.example.kesi.domain.Schedule
import java.time.LocalDate

class DayLine(
    val startDate: LocalDate
) {
    companion object {
        const val LINE_SIZE = 7
    }

    val endDate = startDate.plusDays(LINE_SIZE.toLong() - 1)
    val dayBoxes = List(LINE_SIZE) {DayBox(startDate.plusDays(it.toLong()))}

    //Todo. 나중에 코드를 묶을 필요 있음
    private fun isWithInRangeSchedule(schedule: Schedule): Boolean {
        return !(endDate.toEpochDay() < schedule.start.toEpochDay() ||
                schedule.end.toEpochDay() < startDate.toEpochDay()) //삽입 못하는 스케줄 이라면
    }

    private fun calScheduleValidRange(schedule: Schedule): Pair<Int, Int> {
        //달력 화면에서 유요한 범위를 구한다.
        val start = (schedule.start.toEpochDay() - startDate.toEpochDay()).toInt()
        val end = (schedule.end.toEpochDay() - startDate.toEpochDay()).toInt()

        //현재 라인에서 유요한 범위를 구한다.
        val validStart = if (start < 0) 0 else start
        val validEnd = if (end < dayBoxes.size) end else dayBoxes.lastIndex

        return Pair(validStart, validEnd)
    }

    fun addSchedule(schedule: Schedule){
        if(!isWithInRangeSchedule(schedule)) return

        val (start, end) = calScheduleValidRange(schedule)

        for(i in start..end) {
            dayBoxes[i].addSchedule(schedule)
        }
    }
}














