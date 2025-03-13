package com.example.kesi.calendar.service

import android.content.Context
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kesi.calendar.domain.DayBox
import com.example.kesi.calendar.domain.ScheduleViewMap
import com.example.kesi.calendar.view.DayBoxViewRender
import com.example.custormcalendardeom.view.DayLineViewRender
import com.example.kesi.calendar.view.DayTextView
import com.example.kesi.calendar.domain.DayLine
import java.time.LocalDate

//view + dayBox를 묶는 일은 필요하겠지
//일단 view를 생성하는 책임만 구현해 보자
class CalendarRenderService(
    private val container: ConstraintLayout,
    private val backgroundViewList: List<View>,
    private val dayTvList: List<DayTextView>,
    private val scheduleViewMap: ScheduleViewMap//schedule과 view를 연결
) {
    private val boxViewRender = DayBoxViewRender(container)
    private val lineViewRender = DayLineViewRender(container)
    private val dayLineViews: HashMap<DayLine, List<View>> = HashMap()


    companion object {
        fun Context.dpToPx(dp: Int): Int {
            return (dp * resources.displayMetrics.density).toInt()
        }
    }
    fun viewClear(dayLine: DayLine){
        dayLineViews[dayLine]?.forEach { //기존에 다른게 랜더 되어 있다면 삭제한다.
            container.removeView(it)
        }
    }

    //schedule과 연결된 view를 반환해줘야 겠지?
    fun renderLine(dayLine: DayLine, startDate: LocalDate): List<View> { //line 단위로 랜더한다.
        viewClear(dayLine)

        val views = ArrayList<View>()
        val validMaxEnd = (dayLine.dayBoxes.last().date.toEpochDay() - startDate.toEpochDay()).toInt() // 0 ~ 31
        val lastOccupyList = BooleanArray(DayLine.LINE_SIZE)


        for(i in 0..dayLine.dayBoxes.lastIndex)
            views.addAll(renderBox(dayLine.dayBoxes[i], i, validMaxEnd, startDate, lastOccupyList))

        dayLineViews[dayLine] = views
        return views
    }

    fun renderBox(dayBox: DayBox, index: Int, validMaxEnd: Int, startDate: LocalDate, lastOccupyList: BooleanArray): List<View> {
        //해당 박스에 위치한 backborundView를 가져옴
        val views = ArrayList<View>()
        val backgroundView =
            backgroundViewList[(dayBox.date.toEpochDay() - startDate.toEpochDay()).toInt()]
        //최상단에 위차한 view를 가져옴
        var topView = dayTvList[(dayBox.date.toEpochDay() - startDate.toEpochDay()).toInt()] as View


        (0..<DayBox.MAX_VIEW_COUNT).forEach { i ->
            val schedules = dayBox.getSchedulesByHeight(i)
            if (schedules.isEmpty()) return@forEach //해당 높이에 값이 비어 있음 실행 X

            if (schedules.size == 1 && schedules.first().isLine()) { //가장 첫 번째 요소가 line인지
                val lineSchedule = schedules.first()

                if (!dayBox.isScheduleViewStart(lineSchedule)) { //이미 그려졌다면 혹은 그릴 파트가 아니라면
                    if(scheduleViewMap[lineSchedule].isNotEmpty())
                        topView = scheduleViewMap[lineSchedule].last()

                    return@forEach
                }

                val endBackgroundViewIndex = lineSchedule.end.toEpochDay() - startDate.toEpochDay()
                val validEndBackgroundViewIndex =
                    if (endBackgroundViewIndex < validMaxEnd) endBackgroundViewIndex.toInt() else validMaxEnd
                val endBackgroundView = backgroundViewList[validEndBackgroundViewIndex]

                Log.d("LineRender", "create! height $i schedule: ${lineSchedule.title}")
                val view =
                    lineViewRender.createLine(topView.id, endBackgroundView.id, backgroundView.id,
                        isStart =  lineSchedule.start == dayBox.date,
                        isEnd =  lineSchedule.end.toEpochDay() - startDate.toEpochDay() <= validMaxEnd,
                        schedule = lineSchedule)
                container.addView(view)
                views.add(view)

                scheduleViewMap.add(lineSchedule, view)

                topView = view //다음 상단에 있는 view가 됨

                if(i == DayBox.MAX_VIEW_COUNT - 1) {
                    val endIndex = lineSchedule.end.toEpochDay() - dayBox.date.toEpochDay()
                    val validEndIndex = if (endIndex >= DayLine.LINE_SIZE) DayLine.LINE_SIZE - 1 else endIndex.toInt()

                    for (j in index..validEndIndex)
                        lastOccupyList[j] = true
                }
                return@forEach
            }

            if (schedules.size <= 2 && schedules.stream()
                    .allMatch { it.isStar() }
            ) { //star로 출력할 수 잇는지
                if (schedules.size == 1) { //star가 1개인 경우 경우
                    val starView = boxViewRender.createSingleStar(topView.id, backgroundView.id)
                    views.add(starView)
                    container.addView(starView)
                    scheduleViewMap.add(schedules.first(), starView)
                    topView = starView
                }

                if (schedules.size == 2) { //star가 2개인 경우
                    val starViewList = boxViewRender.createDoubleStar(topView.id, backgroundView.id)
                    for (j in starViewList.indices) {
                        container.addView(starViewList[j])
                        scheduleViewMap.add(schedules[j], starViewList[j])
                    }
                    views.addAll(starViewList)

                    topView = starViewList.first()
                }

                return@forEach
            }


            if(!lastOccupyList[i]) {
                //overflow인 경우
                val overFlowView = boxViewRender.createOverFlowView(topView.id, backgroundView.id)
                overFlowView.text = "+${schedules.size}"
                container.addView(overFlowView)
                schedules.forEach {
                    scheduleViewMap.add(it, overFlowView)
                }
                views.add(overFlowView)

                //상단에 있는 view를 탐색
                topView = scheduleViewMap[dayBox.getSchedulesByHeight(i).first()]?.last() ?: topView
                lastOccupyList[i] = true
            }
        }

        return views
    }

}