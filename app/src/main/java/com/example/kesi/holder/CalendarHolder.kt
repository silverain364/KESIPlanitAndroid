package com.example.kesi.holder

import android.view.View
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.calendar.render.DayTextView
import com.example.kesi.calendar.view.DayBoxView
import com.example.kesi.data.MonthData
import com.example.kesi.domain.Schedule
import java.time.LocalDate

abstract class CalendarHolder(
    itemView: View,
    protected val guides: Pair<ArrayList<Guideline>, ArrayList<Guideline>>,
    protected val backgroundViewList: List<View>, //Todo. 추후 터치 인식을 위해서 forwardViewList를 만들면 괜찮을듯
    protected val dayTvList: List<DayTextView>,
    protected var dayBoxOnClickListener: (DayBoxView) -> (Unit) = { }
) : RecyclerView.ViewHolder(itemView) {
    abstract fun select(selectDate: LocalDate)
    abstract fun bind(monthData: MonthData)
    abstract suspend fun addSchedule(schedule: Schedule)
    abstract fun removeSchedule(scheduleId: Long)

//    함수면 겸칩
//    fun setDayBoxOnClickListener(dayBoxOnClickListener: (DayBoxView) -> (Unit)) {
//        this.dayBoxOnClickListener = dayBoxOnClickListener
//    }
}