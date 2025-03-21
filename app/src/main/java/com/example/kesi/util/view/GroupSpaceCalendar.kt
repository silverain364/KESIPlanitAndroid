package com.example.kesi.util.view

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.adapter.CalendarHolderFactory
import com.example.kesi.adapter.FullCalendarAdapter
import com.example.kesi.calendar.render.DayTextView
import com.example.kesi.calendar.view.DayBoxView
import com.example.kesi.data.MonthData
import com.example.kesi.holder.CalendarHolder
import com.example.kesi.holder.MiniCalendarHolder
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

class GroupSpaceCalendar(
    private val monthTv: TextView,
    private val yearTv: TextView,
    private val calendarRv: RecyclerView,
) {
    private val holderFactory = object: CalendarHolderFactory {
        override fun create(
            view: View, guides: Pair<ArrayList<Guideline>, ArrayList<Guideline>>,
            backgroundViewList: List<View>, dayTvList: List<DayTextView>, dayBoxOnClickListener: (DayBoxView) -> Unit
        ): CalendarHolder {
            return MiniCalendarHolder(view, guides, backgroundViewList, dayTvList, dayBoxOnClickListener)
        }
    }

    private val calendarAdapter =  FullCalendarAdapter(
        ArrayList(), holderFactory
    ) //Todo. 추후 선택시 발생시킬 이벤트 있음 설정해야 됨

    init {
        monthTv.text = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        yearTv.text = LocalDate.now().year.toString()


        for (i in -2..2) {
            calendarAdapter.addItem(MonthData(LocalDate.now().withDayOfMonth(1).plusMonths(i.toLong()), arrayListOf()))
        }

        calendarRv.apply {
            layoutManager = LinearLayoutManager(calendarRv.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = calendarAdapter

            if(onFlingListener == null)
                PagerSnapHelper().attachToRecyclerView(calendarRv)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)


                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                        val position = layoutManager.findFirstVisibleItemPosition()
                        if (position < 0) return

                        val data = calendarAdapter.getItem(position)

                        monthTv.text = data.date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                        yearTv.text = data.date.year.toString()

//                        val holder = calendarRv.findViewHolderForAdapterPosition(position) as MiniCalendarHolder
//                        holder.select(holder.date)


                        //부족한 페이지 추가
                        if (position <= 1) {
                            calendarAdapter.addFirstItem(MonthData(data.date.withDayOfMonth(1).minusMonths(position + 1L), arrayListOf()))
                            calendarAdapter.notifyItemInserted(0)
                        }

                        if (position >= calendarAdapter.itemCount - 2) {
                            val diff = calendarAdapter.itemCount - position
                            calendarAdapter.addItem(MonthData(data.date.withDayOfMonth(1).plusMonths(diff.toLong()), arrayListOf()))
                            calendarAdapter.notifyItemInserted(calendarAdapter.itemCount - 1)
                        }
                    }
                }
            })

            scrollToPosition(calendarAdapter.itemCount / 2)
        }
    }

    fun nextMonth() {
        val layoutManager = calendarRv.layoutManager as? LinearLayoutManager ?: return
        val position = layoutManager.findFirstVisibleItemPosition()
        calendarRv.smoothScrollToPosition(position + 1)
    }

    fun previousMonth() {
        val layoutManager = calendarRv.layoutManager as? LinearLayoutManager ?: return
        val position = layoutManager.findFirstVisibleItemPosition()
        calendarRv.smoothScrollToPosition(position - 1)
    }
}
