package com.example.kesi.util.view

import android.view.View
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import com.example.kesi.R
import com.example.kesi.util.DateTimeRange
import com.example.kesi.util.DateTimeTv
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class DateRangeSelectorView(
    private val startDateTv: TextView,
    private val startTimeTv: TextView,
    private val endDateTv: TextView,
    private val endTimeTv: TextView,
    private val calendarLayout: LinearLayout,
    private val timeLayout: LinearLayout,
    startDateTime:LocalDateTime = LocalDateTime.now(),
    endDateTime:LocalDateTime = LocalDateTime.now().plusMinutes(10)
) {
    private val calendarView: CalendarView = calendarLayout.findViewById(R.id.calendarView)
    private val timePicker: TimePicker = timeLayout.findViewById(R.id.timePicker)
    private var selectedDateTv: TextView? = null
    private var selectedTimeTv: TextView? = null
    private val dateTimeRange: DateTimeRange

    init {
        val startDateTimeTv = DateTimeTv(startDateTime.toLocalDate(), endDateTime.toLocalTime(), startDateTv, startTimeTv)
        val endDateTimeTv = DateTimeTv(endDateTime.toLocalDate(), endDateTime.toLocalTime(), endDateTv, endTimeTv)
        dateTimeRange = DateTimeRange(startDateTimeTv, endDateTimeTv)

        initDateChangeEvent(dateTimeRange)
    }

    private fun initDateChangeEvent(dateTimeRange: DateTimeRange) {
        startDateTv.setOnClickListener {
            timeLayout.visibility = View.GONE

            //end target으로 펼치고 있을 때는 toggle를 실행하지 않음
            if(!(calendarLayout.visibility == View.VISIBLE && selectedDateTv == endDateTv))
                toggleVisibility(calendarLayout)

            selectedDateTv = startDateTv
            //calendarView에 date는 UTC기준 milli second를 기준으로 설정되어 있어 변환이 필요
            //LocalDate > Instant > Millis 로 변환
            calendarView.date = dateTimeRange.getStartDate()
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        endDateTv.setOnClickListener {
            timeLayout.visibility = View.GONE
            if(calendarLayout.visibility == View.GONE || selectedDateTv == endDateTv)
                toggleVisibility(calendarLayout)

            selectedDateTv = endDateTv
            calendarView.date = dateTimeRange.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }


        startTimeTv.setOnClickListener {
            calendarLayout.visibility = View.GONE
            if(timeLayout.visibility == View.GONE || selectedTimeTv == startTimeTv)
                toggleVisibility(timeLayout)

            selectedTimeTv = startTimeTv
            timePicker.hour = dateTimeRange.getStartTime().hour
            timePicker.minute = dateTimeRange.getStartTime().minute
        }


        endTimeTv.setOnClickListener {
            calendarLayout.visibility = View.GONE
            if(timeLayout.visibility == View.GONE || selectedTimeTv == endTimeTv)
                toggleVisibility(timeLayout)

            selectedTimeTv = endTimeTv
            timePicker.hour = dateTimeRange.getEndTime().hour
            timePicker.minute = dateTimeRange.getEndTime().minute
        }

        calendarView.setOnDateChangeListener { calendarView, y, m, d ->
            if(selectedDateTv == startDateTv)
                dateTimeRange.setStartDate(LocalDate.of(y, m + 1, d))

            if(selectedDateTv == endDateTv)
                dateTimeRange.setEndDate(LocalDate.of(y, m + 1, d))
        }

        timePicker.setOnTimeChangedListener { timePicker, h, m ->
            if(selectedTimeTv == startTimeTv)
                dateTimeRange.setStartTime(LocalTime.of(h, m))

            if(selectedTimeTv == endTimeTv)
                dateTimeRange.setEndTime(LocalTime.of(h, m))
        }
    }

    private fun toggleVisibility(view: View) {
        if(view.visibility == View.VISIBLE)
            view.visibility = View.GONE
        else
            view.visibility = View.VISIBLE
    }


    fun getStartDate() = dateTimeRange.getStartDate()
    fun getEndDate() = dateTimeRange.getEndDate()
    fun getStartTime() = dateTimeRange.getStartTime()
    fun getEndTime() = dateTimeRange.getEndTime()
}