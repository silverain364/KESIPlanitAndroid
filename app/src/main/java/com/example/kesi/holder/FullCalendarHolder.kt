package com.example.kesi.holder

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.adapter.BottomSheetAdapter
import com.example.kesi.api.ScheduleApi
import com.example.kesi.calendar.domain.DayLine
import com.example.kesi.calendar.domain.ScheduleViewMap
import com.example.kesi.calendar.service.CalendarRenderService
import com.example.kesi.calendar.service.CalendarService
import com.example.kesi.calendar.view.DayTextView
import com.example.kesi.data.AddScheduleDto
import com.example.kesi.domain.Schedule
import com.example.kesi.model.BottomSheetScheduleDto
import com.example.kesi.data.MonthData
import com.example.kesi.fragment.ScheduleBottomSheet
import com.example.kesi.model.RequestPersonalScheduleDto
import com.example.kesi.model.ScheduleDto
import com.example.kesi.setting.RetrofitSetting
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CompletableDeferred
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate


class FullCalendarHolder(
    itemView: View,
    private val guides: Pair<ArrayList<Guideline>, ArrayList<Guideline>>,
    private val backgroundViewList: ArrayList<View>, //Todo. 추후 터치 인식을 위해서 forwardViewList를 만들면 괜찮을듯
    private val dayTvList: ArrayList<DayTextView>,
    private val scheduleBottomSheet: ScheduleBottomSheet
) : RecyclerView.ViewHolder(itemView) {
    lateinit var date: LocalDate
    private val dayLines = ArrayList<DayLine>()
    private var startEpochDay = LocalDate.now().toEpochDay()
    private lateinit var schedules: ArrayList<Schedule>

    private val container: ConstraintLayout = itemView.findViewById(R.id.main)
    private val calendarRender: CalendarRenderService =
        CalendarRenderService(container, backgroundViewList, dayTvList, ScheduleViewMap())

    private val calendarService = CalendarService(calendarRender)

    private val retrofit = RetrofitSetting.getRetrofit()
    private val scheduleApi = retrofit.create(ScheduleApi::class.java)

    private var bindCompleted = CompletableDeferred<Unit>()


    init {
        for(i in backgroundViewList.indices) {
            backgroundViewList[i].setOnClickListener {
                if(schedules.isEmpty()) return@setOnClickListener

                val dayBox = dayLines[i / 7].dayBoxes[i % 7] //Todo. DayBox 자체를 넘겨줘도 괜찮을 것 같은데
                scheduleBottomSheet.showSchedules(
                    dayBox.date, //클릭한 스케줄 정보 보여주기
                    dayBox.getAllScheduleOrderByHeight().map {
                        BottomSheetScheduleDto.toDto(it) }.toList()
                )
            }
        }
    }

    private fun clear() {
        calendarService.renderClear(dayLines) //기존에 view를 사제
        dayLines.clear() //기본에 동적으로 표시할 정보를 제거
    }

    private fun initDate(nowMonth: LocalDate) {
        clear()
        this.date = nowMonth
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
        bindCompleted = CompletableDeferred()

        initDate(monthData.date)
        scheduleApi.findByMonth(monthData.date.toString()).enqueue(object: Callback<List<ScheduleDto>> {
            override fun onResponse(p0: Call<List<ScheduleDto>>, p1: Response<List<ScheduleDto>>) {
                Log.d("FullCalendarHolder", "size : ${p1.body()?.size}")

                if(p1.body() == null) return
                schedules = ArrayList(p1.body()!!.map { it.toDomain() })
                calendarService.addSchedules(schedules, dayLines)
                calendarService.render(dayLines)
                bindCompleted.complete(Unit)
            }

            override fun onFailure(p0: Call<List<ScheduleDto>>, p1: Throwable) {
                bindCompleted.complete(Unit)
            }
        })
    }

    suspend fun addSchedule(addScheduleDto: AddScheduleDto) { //데이터를 이 함수가 더 빨리 받아올 수도 있음
        bindCompleted.await()

        scheduleApi.addSchedule(RequestPersonalScheduleDto.toDto(addScheduleDto)).enqueue(object : Callback<Long> {
            override fun onResponse(p0: Call<Long>, p1: Response<Long>) {
                if(p1.body() == null) return

                //Todo. 추후 처리
                val id = p1.body()!!
                val schedule = addScheduleDto.toDomain(id)

                schedules.add(schedule)
                calendarService.addSchedule(schedule, dayLines)
                calendarService.render(dayLines)
            }

            override fun onFailure(p0: Call<Long>, p1: Throwable) {
                Log.d("FullCalendarHolder", "onFailure: ${p1.message}")
            }
        })
    }
}