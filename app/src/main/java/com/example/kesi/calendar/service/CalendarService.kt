package com.example.kesi.calendar.service

import com.example.kesi.calendar.domain.DayLine
import com.example.kesi.domain.Schedule


class CalendarService(
    private val calendarRender: CalendarRenderService,
) {
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

    fun addSchedule(schedule: Schedule, dayLines: List<DayLine>){
        val validLineIndex = getValidLineIndex(schedule, dayLines)

        for(i in validLineIndex.first..validLineIndex.second) {
            dayLines[i].addSchedule(schedule)
            updateLineList.add(dayLines[i]) //업데이트 된 항목을 담아둔다.
        }
    }

    fun addSchedules(schedules: List<Schedule>, dayLines: List<DayLine>) {
        schedules.forEach { addSchedule(it, dayLines) }
    }

    fun removeSchedule(schedule: Schedule, dayLines: List<DayLine>) {
        val validLineIndex = getValidLineIndex(schedule, dayLines)

        for(i in validLineIndex.first..validLineIndex.second) {
            dayLines[i].removeScheduleAndReplace(schedule)
            updateLineList.add(dayLines[i])
        }
    }

    fun updateSchedule(originalSchedule: Schedule, updateSchedule: Schedule, dayLines: List<DayLine>) {
        removeSchedule(originalSchedule, dayLines)
        addSchedule(updateSchedule, dayLines)
    }


    fun render(dayLines: List<DayLine>) {
        val startDate = dayLines.first().startDate

        //render가 필요한 라인만 잰행한다.
        dayLines.forEach {
            if(updateLineList.contains(it)) { //update해야 되는 line이 존재한다면
                calendarRender.renderLine(it, startDate)
            }

            updateLineList.remove(it)
        }
    }


    fun renderClear(dayLines: List<DayLine>) {
        dayLines.forEach {
            calendarRender.viewClear(it)
        }
    }
}