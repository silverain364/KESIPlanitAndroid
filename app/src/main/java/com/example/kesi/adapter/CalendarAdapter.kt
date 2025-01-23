package com.example.kesi.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.model.CalendarBoxDto
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.time.LocalDate

class CalendarAdapter(
    private val dates: MutableList<LocalDate>,
    private val fragmentManager: FragmentManager,
    private val bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_month, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dates[position])
    }

    override fun getItemCount(): Int = dates.size

    fun getItem(position: Int): LocalDate = dates[position]

    fun addItem(localDate: LocalDate) {
        dates.add(localDate)
    }

    fun addFirstItem(localDate: LocalDate) {
        dates.add(0, localDate)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val calendarContentRv: RecyclerView = itemView.findViewById(R.id.calendarContentView)

        init {
            calendarContentRv.layoutManager = GridLayoutManager(itemView.context, 7)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(date: LocalDate) {
            val calendarAdapter = CalendarContentAdapter(mutableListOf(), fragmentManager, bottomSheetBehavior)
            val firstDayDate = LocalDate.of(date.year, date.month, 1)
            val lastMonthDate = date.minusMonths(1)

            val firstDay = firstDayDate.dayOfWeek.value % 7
            val lastMonthLastDay = lastMonthDate.lengthOfMonth()

            for (i in 1..firstDay) {
                calendarAdapter.addItem(CalendarBoxDto(lastMonthLastDay - firstDay + i, CalendarBoxDto.LAST_MONTH))
            }

            for (i in 1..date.lengthOfMonth()) {
                calendarAdapter.addItem(CalendarBoxDto(i, CalendarBoxDto.NOW_MONTH))
            }

            for (i in 1..(35 - (date.lengthOfMonth() + firstDay))) {
                calendarAdapter.addItem(CalendarBoxDto(i, CalendarBoxDto.NEXT_MONTH))
            }

            calendarContentRv.adapter = calendarAdapter
        }
    }
}