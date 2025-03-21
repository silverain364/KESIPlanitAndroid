package com.example.kesi.holder

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import com.example.kesi.R
import com.example.kesi.calendar.domain.DayLine
import com.example.kesi.calendar.render.DayTextView
import com.example.kesi.calendar.view.DayBoxView
import com.example.kesi.calendar.view.DayLineView
import com.example.kesi.data.MonthData
import com.example.kesi.domain.Schedule
import java.time.LocalDate

class MiniCalendarHolder (
    itemView: View,
    guides: Pair<ArrayList<Guideline>, ArrayList<Guideline>>,
    backgroundViewList: List<View>, //Todo. 추후 터치 인식을 위해서 forwardViewList를 만들면 괜찮을듯
    dayTvList: List<DayTextView>,
    dayBoxOnClickListener: (DayBoxView) -> (Unit) = { }
): CalendarHolder(
    itemView,
    guides,
    backgroundViewList,
    dayTvList,
    dayBoxOnClickListener
) {
    private val container = itemView.findViewById<ConstraintLayout>(R.id.main)
    lateinit var date: LocalDate

    override fun select(selectDate: LocalDate) {
    }

    override fun bind(monthData: MonthData) {
        date = monthData.date
        val firstDayDate = LocalDate.of(date.year, date.month, 1) //해당 월에 첫일을 구함.
        val startEpochDay = firstDayDate.toEpochDay() - firstDayDate.dayOfWeek.value % 7 //해당 화면에 보여주는 첫 번째 화면을 구함.

        for(i in 0..dayTvList.lastIndex) {
            dayTvList[i].setDate(LocalDate.ofEpochDay(startEpochDay + i))
            if(date.month != dayTvList[i].getDate().month) {
                dayTvList[i].alpha = 0.6f
            }else {
                dayTvList[i].alpha = 1f
            }
        }
    }

    override suspend fun addSchedule(schedule: Schedule) {
    }

    override fun removeSchedule(scheduleId: Long) {

    }

    private fun initDayTv() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(container)
        for(i in 0..dayTvList.lastIndex) {
            constraintSet.connect(dayTvList[i].id, ConstraintSet.LEFT, backgroundViewList[i].id, ConstraintSet.LEFT)
            constraintSet.connect(dayTvList[i].id, ConstraintSet.TOP, backgroundViewList[i].id, ConstraintSet.TOP)
            constraintSet.connect(dayTvList[i].id, ConstraintSet.BOTTOM, backgroundViewList[i].id, ConstraintSet.BOTTOM)
            constraintSet.connect(dayTvList[i].id, ConstraintSet.RIGHT, backgroundViewList[i].id, ConstraintSet.RIGHT)
        }
    }
}