package com.example.kesi.calendar.domain
import android.util.Log
import com.example.kesi.domain.Schedule
import java.time.LocalDate
import kotlin.math.log

class DayLine(
    val startDate: LocalDate,
) {
    val endDate: LocalDate = startDate.plusDays(LINE_SIZE.toLong() - 1) //Todo. 검즘필요
    val dayBoxes: Array<DayBox> = Array(LINE_SIZE) { DayBox(startDate.plusDays(it.toLong())) }

    companion object {
        const val LINE_SIZE = 7
    }

    private fun isWithinRangeSchedule(schedule: Schedule): Boolean {
        return !(endDate.toEpochDay() < schedule.start.toEpochDay() ||
                schedule.end.toEpochDay() < startDate.toEpochDay()) //삽입 못하는 스케줄 이라면
    }

    //todo. edit
    fun addSchedule(schedule: Schedule) {
        if (!isWithinRangeSchedule(schedule)) return

        if (schedule.isStar()) addStarSchedule(schedule)
        if (schedule.isLine()) addLineSchedule(schedule)
    }

    private fun calScheduleValidRange(schedule: Schedule): Pair<Int, Int> {
        //달력 화면에서 유요한 범위를 구한다.
        val start = (schedule.start.toEpochDay() - startDate.toEpochDay()).toInt()
        val end = (schedule.end.toEpochDay() - startDate.toEpochDay()).toInt()

        //현재 라인에서 유요한 범위를 구한다.
        var validStart = if (start < 0) 0 else start
        val validEnd = if (end < dayBoxes.size) end else dayBoxes.lastIndex

        return Pair(validStart, validEnd)
    }

    private fun calScheduleRenderStartIndex(schedule: Schedule): Int {
        val validRange = calScheduleValidRange(schedule)
        var renderStartIndex = validRange.first

        while(renderStartIndex <= validRange.second
            && dayBoxes[renderStartIndex].findHeightBySchedule(schedule) >= DayBox.MAX_VIEW_COUNT - 1) {

            if (dayBoxes[renderStartIndex].findHeightBySchedule(schedule) == DayBox.MAX_VIEW_COUNT - 1)
                if (dayBoxes[renderStartIndex].isEmptyByHeight(DayBox.MAX_VIEW_COUNT - 1)) return renderStartIndex

            renderStartIndex++
        }

        return renderStartIndex
    }

    private fun addLineSchedule(schedule: Schedule) { //2일 이상 일정인 경우
        val validRange = calScheduleValidRange(schedule) //현재 라인에서 유효한 범위를 구함

        //박스에서 일정이 많아서 그리지 못 하는 경우(+n 과 같이 표현해야 되는 경우)
        val renderStart = calScheduleRenderStartIndex(schedule) //실제로 랜더해야 하는 인덱스를 구한다.

        for(i in validRange.first..<renderStart) {
            if (crashSingleLine(dayBoxes[i], schedule)) continue //원래 마지막 줄에 한 개의 일정만 존재 했던 경우 충돌처리
            dayBoxes[i].addLastHeightSchedule(schedule) //마지막 줄에 일정 추가
        }

        if (renderStart > validRange.second) return

        val height = dayBoxes[renderStart].findHeightBySchedule(schedule)
        dayBoxes[renderStart].addScheduleViewStart(schedule) //실제로 그려야하는 위치를 표시!!

        //높이가 결정된 상태에서 뒤에 일정이 height와 충돌한다면
        for (i in renderStart..validRange.second) {
            if (dayBoxes[i].isNotEmptyByHeight(height)) { //이미 공간을 점유하고 있는 일정이 있다면
                insertSchedule(dayBoxes[i], schedule, height)
                continue
            }

            dayBoxes[i].addFixedHeightSchedule(schedule, height)
        }
    }

    //특정 박스 특정 높이에 스케줄을 삽입
    private fun insertSchedule(dayBox: DayBox, schedule: Schedule, height: Int) {
        val replaceSchedules = dayBox.getSchedulesAfter(schedule, height) //재정렬이 필요한 스케줄을 가져온다.

        replaceSchedules.forEach { removeSchedule(it) } //해당 일정을 잠시 삭제한다.

        dayBox.addFixedHeightSchedule(schedule, height)

        replaceSchedules.forEach { addSchedule(it) } //다시 추가해준다.
    }

    private fun addStarSchedule(schedule: Schedule) {
        val boxIndex = (schedule.start.toEpochDay() - dayBoxes.first().date.toEpochDay()).toInt()
        val dayBox = dayBoxes[boxIndex]

        //만약 추가한 일정이 충돌나는 경우
        if (crashSingleLine(dayBox, schedule)) return

        //빈 공간을 찾는다.
        //해당 일정을 추가한다.
        dayBox.addStar(schedule) //일정을 추가한다.
    }

    private fun calValidStartBoxIndex(schedule: Schedule): Int {
        val start = (schedule.start.toEpochDay() - dayBoxes.first().date.toEpochDay()).toInt()
        return if (start < 0) 0 else start
    }

    private fun calValidEndBoxIndex(schedule: Schedule): Int {
        val end = (schedule.end.toEpochDay() - dayBoxes.first().date.toEpochDay()).toInt()
        return if (end < dayBoxes.size) end else dayBoxes.lastIndex
    }

    private fun removeSchedule(schedule: Schedule) {
        val validStart = calValidStartBoxIndex(schedule)
        val validEnd = calValidEndBoxIndex(schedule)

        for (i in validStart..validEnd)
            dayBoxes[i].removeSchedule(schedule) //해당 일정을 삭제한다.
    }

    fun removeScheduleAndReplace(schedule: Schedule) {
        val validStart = calValidStartBoxIndex(schedule)
        val validEnd = calValidEndBoxIndex(schedule)

        removeSchedule(schedule)

        //재비치
        for (i in validStart..validEnd) {
            val removeSchedules = dayBoxes[i].getSchedulesAfter(schedule) //삭제한 일정보다 같거나 늦은 일정들을 가져온다.

            removeSchedules.forEach { //재배치
                removeSchedule(it) //또 뒤에 일정이 삭제 되면서 영향을 미치는 값들에 대해서 다시 계산을 해준다.
                addSchedule(it)
            }
        }
    }

    private fun isCrashSingleLine(dayBox: DayBox, schedule: Schedule): Boolean {
        val height = dayBox.findEmptyHeight(schedule)

        if (height != DayBox.MAX_VIEW_COUNT) return false;
        if (dayBox.getLastHeightSchedules().size != 1) return false
        if (dayBox.getLastHeightSchedules().first().isStar()) return false

        //그리기를 시작하는 부분에 schedule와 충돌나는 경우에만 변경하겠다.
        if (!dayBox.isScheduleViewStart(dayBox.getLastHeightSchedules().first())) return false

        return true
    }


    //만약 마자믹 줄에 일정이 하나만 있고 그것이 연속된 일정이라면
    //+n 으로 바뀌면서 연속된 일정을 갱신할 필요가 있음 그래서 충돌하는지 판단함.
    private fun crashSingleLine(dayBox: DayBox, schedule: Schedule): Boolean {
        if (!isCrashSingleLine(dayBox, schedule)) return false

        val crashLineSchedule = dayBox.getLastHeightSchedules().first()

        removeSchedule(crashLineSchedule) //충돌된 스케줄 정보를 가져와 잠시 삭제
        dayBox.addLastHeightSchedule(schedule) //일정을 추가
        addSchedule(crashLineSchedule) //삭제했던 일정을 새롭게 추가한다.

        Log.d("DayLine", "crash! orignal : ${dayBox.date}  target : $schedule star : ${schedule.isStar()}")
        return true
    }
}