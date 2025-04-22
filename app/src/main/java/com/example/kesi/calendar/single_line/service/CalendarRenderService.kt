package com.example.kesi.calendar.single_line.service

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kesi.calendar.mutiple_line.render.DayTextView
import com.example.kesi.calendar.single_line.domain.DayBox
import com.example.kesi.calendar.single_line.domain.DayLine
import com.example.kesi.calendar.single_line.render.DayBoxViewRender
import com.example.kesi.calendar.single_line.render.DayLineViewRender
import com.example.kesi.domain.Schedule
import com.example.kesi.domain.ScheduleType
import java.time.LocalDate

class CalendarRenderService(
    private val container: ConstraintLayout,
    private val backgroundViewList: List<View>,
    private val dayTvList: List<DayTextView>,
) {
    private val dayBoxViewRender: DayBoxViewRender = DayBoxViewRender(container)
    private val dayLineViewRender: DayLineViewRender = DayLineViewRender(container)
    private val dayLineViews: HashMap<DayLine, List<View>> = HashMap()

    fun viewClear(dayLines: List<DayLine>, startDate: LocalDate) {
        dayLines.forEach { viewClear(it, startDate) }
    }

    fun viewClear(dayLine: DayLine, startDate: LocalDate) {
        val componentStartIndex = (dayLine.startDate.toEpochDay() - startDate.toEpochDay()).toInt()
        for(i in componentStartIndex..<componentStartIndex + DayLine.LINE_SIZE) {
            dayTvList[i].uncheckInverse() //원래대로
        }

        dayLineViews[dayLine]?.forEach {
            container.removeView(it)
        }
    }

    fun renderCalendar(dayLines: List<DayLine>): List<View> {
        val views = ArrayList<View>()

        val startDate = dayLines.first().startDate
        dayLines.forEach {
            views.addAll(renderLine(it, startDate))
        }

        return views
    }

    fun renderLine(dayLine: DayLine, startDate: LocalDate): List<View> {
        viewClear(dayLine, startDate)

        val views = ArrayList<View>()
        val validMaxEnd = (dayLine.endDate.toEpochDay() - startDate.toEpochDay()).toInt() // 0 ~ 31

        for(i in dayLine.dayBoxes.indices)
            views.addAll(renderBox(dayLine.dayBoxes[i], i, validMaxEnd, startDate))

        dayLineViews[dayLine] = views
        return views
    }

    fun renderBox(dayBox: DayBox, boxIndex: Int, validMaxEnd: Int, startDate: LocalDate): List<View> {
        val views = ArrayList<View>()

        val componentIndex = (dayBox.date.toEpochDay() - startDate.toEpochDay()).toInt()
        val backgroundView = backgroundViewList[componentIndex]
        val dayTv = dayTvList[componentIndex]


        val types = ScheduleType.entries.toTypedArray()
        val priorityScheduleByType = ArrayList<Schedule>()

        //타입별 가장 일정이 빠른 일정을 가져온다.
        types.forEach {
            val schedule = dayBox.getFirstPriorityScheduleByType(it)
            if (schedule != null) priorityScheduleByType.add(schedule)
        }

        if(priorityScheduleByType.isEmpty()) return emptyList()

        dayTv.inverse() //color(inverse)

        priorityScheduleByType.forEach { schedule ->
            //해당 라인에 시작하는 위치에 있는 경우만 출력
            if (!(boxIndex == 0 || schedule.start == dayBox.date)) return@forEach


            //끝나는 background를 구한다.
            val endBackgroundViewIndex = schedule.end.toEpochDay() - startDate.toEpochDay()
            val validEndBackgroundViewIndex =
                if (endBackgroundViewIndex < validMaxEnd) endBackgroundViewIndex.toInt() else validMaxEnd
            val endBackgroundView = backgroundViewList[validEndBackgroundViewIndex]

            val view =
                if(schedule.isStar()) dayBoxViewRender.crateCircleView(backgroundView.id, schedule)
                else dayLineViewRender.createLine(backgroundView.id, endBackgroundView.id, schedule)

            container.addView(view)
            views.add(view)
        }

        return views
    }
}






























