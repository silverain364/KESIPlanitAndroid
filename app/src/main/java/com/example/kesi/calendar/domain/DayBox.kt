package com.example.kesi.calendar.domain


import com.example.kesi.domain.Schedule
import java.time.LocalDate

class DayBox(
    val date: LocalDate,
) {
    private val heightScheduleViewList: HeightScheduleViewList = HeightScheduleViewList()
    private val scheduleViewStartList: MutableSet<Schedule> = mutableSetOf() //특정 칸에서 실제로 그리는 스케줄 정보를 저장한다.


    companion object {
        const val MAX_VIEW_COUNT = 4
    }

    fun addStar(schedule: Schedule) {
        //별은 규칙에 맞게 삽입만 하면됨
        //중복 신경 쓸 필요가 항상 없음
        scheduleViewStartList.add(schedule)
        heightScheduleViewList.addStar(schedule)
    }

    fun findEmptyHeight(schedule: Schedule): Int {
        return heightScheduleViewList.findEmptyHeight(schedule)
    }

    //해당 일정을 포함해 해당일정 뒤에 있는 일정을 모두 삭제한다.
    //해당 일정과 중복되어 있는 경우도 삭제하고 재배치한다.(view 방식에 변화가 있을 수 있음으로)
    //높이 & 일정이 기준임!!
    fun getSchedulesAfter(schedule: Schedule, startHeight: Int): List<Schedule> {
        val schedules = ArrayList<Schedule>()

        //타켓 스케즐보다 일정이 뒤에 있는 모든 스케줄 정보를 가져온다.
        for(i in startHeight..<MAX_VIEW_COUNT - 1) {
            //해당 일정이 자신보다 뒤거나 같은 일정만 가져옴
            heightScheduleViewList[i].forEach { compareSchedule ->
                if(schedule.start <= compareSchedule.start)
                    schedules.add(compareSchedule)
            }
        }

        //마지막 줄에 어떤 일정이든 특정 경우에 따라서 다시 보여줘야할 수 있음으로 모두 가져온다.
        schedules.addAll(heightScheduleViewList[MAX_VIEW_COUNT - 1])
        return schedules //반환
    }

    fun getSchedulesAfter(schedule: Schedule): List<Schedule> {
        return getSchedulesAfter(schedule, heightScheduleViewList.findHeightBySchedule(schedule))
    }

    fun removeSchedule(schedule: Schedule) {
        scheduleViewStartList.remove(schedule)
        heightScheduleViewList.removeSchedule(schedule)
    }

    fun addScheduleViewStart(schedule: Schedule) {
        scheduleViewStartList.add(schedule)
    }

    fun isScheduleViewStart(schedule: Schedule): Boolean {
        return scheduleViewStartList.contains(schedule)
    }

    fun findHeightBySchedule(schedule: Schedule): Int {
        return heightScheduleViewList.findHeightBySchedule(schedule)
    }

    fun getSchedulesByHeight(height: Int)
        = heightScheduleViewList[height]

    fun getLastHeightSchedules(): List<Schedule> {
        return heightScheduleViewList.last()
    }

    fun addLastHeightSchedule(schedule: Schedule) {
        heightScheduleViewList.addLine(schedule, heightScheduleViewList.lastIndex())
    }

    fun addFixedHeightSchedule(schedule: Schedule, height: Int) {
        heightScheduleViewList.addLine(schedule, height)
    }

    fun isNotEmptyByHeight(height: Int): Boolean {
        return heightScheduleViewList.isNotEmpty(height)
    }

    fun isEmptyByHeight(height: Int): Boolean {
        return heightScheduleViewList.isEmpty(height)
    }

    fun getAllScheduleOrderByHeight() = heightScheduleViewList.findAllScheduleOrderByHeight()
}
