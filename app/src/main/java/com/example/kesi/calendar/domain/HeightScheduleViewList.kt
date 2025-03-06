package com.example.kesi.calendar.domain

import com.example.kesi.domain.Schedule
import kotlin.math.min

class HeightScheduleViewList {
    private val heightScheduleViewList: Array<ArrayList<Schedule>> = Array(MAX_VIEW_COUNT) { ArrayList() } //Todo. 추후 수정 예정
    private val heightMap: HashMap<Schedule, Int> = HashMap() //Todo. 나중에 이것보다 효율적인 방법 있으면 수정할 예정

    companion object {
        const val MAX_VIEW_COUNT = 5
    }

    fun lastIndex() = heightScheduleViewList.lastIndex

    fun addStar(schedule: Schedule) {
        val height = findEmptyHeightForStar()

        if(height == MAX_VIEW_COUNT) {
            heightScheduleViewList.last().add(schedule)
            heightMap[schedule] = heightScheduleViewList.lastIndex
            return
        }

        heightScheduleViewList[height].add(schedule) //Todo. 캡슐화 한번 더 할 수 있을 듯
        heightMap[schedule] = height
    }

    fun addLine(schedule: Schedule, height: Int) {
        if(height < 0 || height >= MAX_VIEW_COUNT) return
        heightScheduleViewList[height].add(schedule)
        heightMap[schedule] = height
    }

    fun isNotEmpty(height: Int): Boolean {
        return heightScheduleViewList[height].isNotEmpty()
    }

    fun isEmpty(height: Int): Boolean {
        return heightScheduleViewList[height].isEmpty()
    }

    operator fun get(height: Int): List<Schedule> {
        return heightScheduleViewList[height].toList()
    }


    fun last(): List<Schedule> { //schedule add하는 경우 height 정보도 저쟝해야됨
        return heightScheduleViewList.last()
    }

    //높이에 따라서 들어갈 수 있는 위치가 다름
    fun findEmptyHeight(schedule: Schedule): Int {
        return if(schedule.isStar()) findEmptyHeightForStar()
        else findEmptyHeightForLine()
    }

    private fun findEmptyHeightForStar(): Int {
        var i = 0;
        while (i < MAX_VIEW_COUNT) {
            if(heightScheduleViewList[i].isEmpty()) return i
            if(heightScheduleViewList[i].size == 1 && heightScheduleViewList[i][0].isStar()) return i;

            i++
        }

        return i;
    }

    private fun findEmptyHeightForLine(): Int{
        var i = 0
        while (i < MAX_VIEW_COUNT && heightScheduleViewList[i].size != 0) i++

        return i;
    }


    fun removeSchedule(schedule: Schedule) {
        val height = heightMap.remove(schedule) ?: return
        heightScheduleViewList[height].remove(schedule)
    }

    fun findAllScheduleOrderByHeight(): List<Schedule> {
        val rst = ArrayList<Schedule>()
        for(list in heightScheduleViewList)
            rst.addAll(list)

        return rst
    }

    fun getHeightBySchedule(schedule: Schedule): Int {
        return heightMap[schedule] ?: -1 //해당 스케줄 정보가 없으면 최대값을 반환.
        //Todo. 원래는 일어나면 안되는 일임으로 추후 Exception 처리로 변경할수도 있음
    }

    //특정 스케줄에 height를 구한다.
    fun findHeightBySchedule(schedule: Schedule): Int {
        val schedules = findAllScheduleOrderByHeight()
        val emptyHeight = findEmptyHeight(schedule)

        schedules.forEach { compareSchedule ->
            if(compareSchedule.isStar() && schedule.isLine()) //별보다 라인이 우선순위가 높음
                return min(getHeightBySchedule(compareSchedule), emptyHeight)

            if(compareSchedule.start > schedule.start) //앞선 일정이라면
                return min(getHeightBySchedule(compareSchedule), emptyHeight)
        }

        //위 조건에 달성하지 않는 경우
        return emptyHeight //가장 뒤에 있는 일정이라면 빈 공간을 찾는다.
    }

}