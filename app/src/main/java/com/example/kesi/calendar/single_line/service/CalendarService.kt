package com.example.kesi.calendar.single_line.service

import com.example.kesi.calendar.single_line.domain.DayLine
import com.example.kesi.domain.Schedule

class CalendarService (
    private val calendarRender: CalendarRenderService
){
    private val updateLineList: MutableSet<DayLine> = mutableSetOf()
    private fun getValidLineIndex(schedule: Schedule, dayLines: List<DayLine>): Pair<Int, Int> {
        val calendarLastIndex = dayLines.size * DayLine.LINE_SIZE

        val firstDayBoxDate = dayLines.first().dayBoxes.first().date.toEpochDay()

        val startIndex = (schedule.start.toEpochDay() - firstDayBoxDate).toInt() //시작 일정을 구한다.
        val endIndex = (schedule.end.toEpochDay() - firstDayBoxDate).toInt() //끝나는 일정을 구한다.

        val validStartIndex = if(startIndex > 0) startIndex else 0
        val validEndIndex = if(calendarLastIndex > endIndex) endIndex else calendarLastIndex - 1

        val validStartLineIndex = validStartIndex / DayLine.LINE_SIZE
        val validEndLIneIndex = validEndIndex / DayLine.LINE_SIZE

        return Pair(validStartLineIndex, validEndLIneIndex)
    }
    fun addSchedule(schedule: Schedule, dayLines: List<DayLine>) {
        val (validStart, validEnd) =  getValidLineIndex(schedule, dayLines)

        for(i in validStart..validEnd) {
            dayLines[i].addSchedule(schedule)
            updateLineList.add(dayLines[i])
        }
    }

    fun addSchedules(schedules:List<Schedule>, dayLines: List<DayLine>) {
        schedules.forEach { addSchedule(it, dayLines) }
    }

    fun render(dayLines: List<DayLine>) {
        val startDate = dayLines.first().startDate

        dayLines.forEach {
            if(updateLineList.contains(it)) {
                calendarRender.renderLine(it, startDate)
                updateLineList.remove(it)
            }
        }
    }

}