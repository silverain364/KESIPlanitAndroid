package com.example.kesi.holder

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.api.ScheduleApi
import com.example.kesi.calendar.domain.DayBox
import com.example.kesi.calendar.domain.DayLine
import com.example.kesi.calendar.domain.ScheduleViewMap
import com.example.kesi.calendar.service.CalendarRenderService
import com.example.kesi.calendar.service.CalendarService
import com.example.kesi.calendar.view.DayTextView
import com.example.kesi.domain.Schedule
import com.example.kesi.model.MonthData
import com.example.kesi.model.ScheduleDto
import com.example.kesi.setting.RetrofitSetting
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate


class FullCalendarHolder(
    itemView: View,
    private val guides: Pair<ArrayList<Guideline>, ArrayList<Guideline>>,
    private val backgroundViewList: ArrayList<View>,
    private val dayTvList: ArrayList<DayTextView>
) : RecyclerView.ViewHolder(itemView) {
    private val dayLines = ArrayList<DayLine>()
    private var startEpochDay = LocalDate.now().toEpochDay()
    private lateinit var schedules: ArrayList<Schedule>

    private val container: ConstraintLayout = itemView.findViewById(R.id.main)
    private val calendarRender: CalendarRenderService =
        CalendarRenderService(container, backgroundViewList, dayTvList, ScheduleViewMap())

    private val calendarService = CalendarService(calendarRender)

    private val retrofit = RetrofitSetting.getRetrofit()
    private val scheduleApi = retrofit.create(ScheduleApi::class.java)

    private fun initDate(nowMonth: LocalDate) {
        calendarService.renderClear(dayLines)
        dayLines.clear() //기본에 동적으로 표시할 정보를 제거

        var cnt = 0;

        val firstDayDate = LocalDate.of(nowMonth.year, nowMonth.month, 1) //해당 월에 첫일을 구함.
        startEpochDay = firstDayDate.toEpochDay() - firstDayDate.dayOfWeek.value % 7 //해당 화면에 보여주는 첫 번째 화면을 구함.


        for (i in 0..<guides.second.size - 1) { //가로줄 만큼 반복
            dayLines.add(DayLine(LocalDate.ofEpochDay(startEpochDay + (guides.first.size - 1) * i)))
            for (j in 0..<guides.first.size - 1) {
                val date = LocalDate.ofEpochDay(startEpochDay + cnt)
                dayTvList[cnt].setDate(date)

                if (date.month != nowMonth.month)
                    dayTvList[cnt].setTextColor(dayTvList[cnt].textColors.withAlpha(100))

                cnt++;
            }
        }
    }


    fun bind(monthData: MonthData) {
        initDate(monthData.date)
        scheduleApi.findByMonth(monthData.date.toString()).enqueue(object: Callback<List<ScheduleDto>> {
            override fun onResponse(p0: Call<List<ScheduleDto>>, p1: Response<List<ScheduleDto>>) {
                Log.d("FullCalendarHolder", "size : ${p1.body()?.size}")

                if(p1.body() == null) return
                schedules = ArrayList(p1.body()!!.map { it.toDomain() })
                calendarService.addSchedules(schedules, dayLines)
                calendarService.render(dayLines)
            }

            override fun onFailure(p0: Call<List<ScheduleDto>>, p1: Throwable) {

            }
        })
    }


}