package com.example.kesi.holder

import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.transition.TransitionManager
import com.example.kesi.R
import com.example.kesi.api.GroupScheduleApi
import com.example.kesi.api.PersonalScheduleApi
import com.example.kesi.calendar.mutiple_line.domain.DayLine
import com.example.kesi.calendar.mutiple_line.domain.ScheduleViewMap
import com.example.kesi.calendar.mutiple_line.service.CalendarRenderService
import com.example.kesi.calendar.mutiple_line.service.CalendarService
import com.example.kesi.calendar.mutiple_line.render.DayTextView
import com.example.kesi.calendar.mutiple_line.view.DayBoxView
import com.example.kesi.calendar.mutiple_line.view.DayLineView
import com.example.kesi.domain.Schedule
import com.example.kesi.data.MonthData
import com.example.kesi.model.GroupScheduleDto
import com.example.kesi.model.ScheduleDto
import com.example.kesi.setting.RetrofitSetting
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FullCalendarHolder (
    itemView: View,
    guides: Pair<ArrayList<Guideline>, ArrayList<Guideline>>,
    backgroundViewList: List<View>, //Todo. 추후 터치 인식을 위해서 forwardViewList를 만들면 괜찮을듯
    dayTvList: List<DayTextView>,
) : CalendarHolder(
    itemView,
    guides,
    backgroundViewList,
    dayTvList,
) {
    lateinit var date: LocalDate
    private val dayLines = ArrayList<DayLine>()
    private val dayViewLines = ArrayList<DayLineView>()
    private var selectedBox: DayBoxView? = null

    private val scheduleMap: HashMap<Long, Schedule> = HashMap()

    private val container: ConstraintLayout = itemView.findViewById(R.id.main)

    private val calendarRender: CalendarRenderService =
        CalendarRenderService(container, backgroundViewList, dayTvList, ScheduleViewMap())

    private val calendarService = CalendarService(calendarRender)

    private val retrofit = RetrofitSetting.getRetrofit()
    private val scheduleApi = retrofit.create(PersonalScheduleApi::class.java)
    private val groupsScheduleApi = retrofit.create(GroupScheduleApi::class.java)

    private var bindCompleted = CompletableDeferred<Unit>()

    private val scope = CoroutineScope(Dispatchers.IO)


    init {
        TransitionManager.beginDelayedTransition(container) //animation

        for(i in backgroundViewList.indices) {
            backgroundViewList[i].setOnClickListener {
                if(scheduleMap.isEmpty()) return@setOnClickListener

                val dayBox = dayLines[i / 7].dayBoxes[i % 7] //Todo. DayBox 자체를 넘겨줘도 괜찮을 것 같은데
                select(dayBox.date)
            }
        }
    }

    private fun clear() {
        calendarService.renderClear(dayLines) //기존에 view를 사제
        dayViewLines.clear()
        dayLines.clear() //기본에 동적으로 표시할 정보를 제거
    }

    private fun initDate(nowMonth: LocalDate) {
        clear()
        this.date = nowMonth

        val firstDayDate = LocalDate.of(nowMonth.year, nowMonth.month, 1) //해당 월에 첫일을 구함.
        val startEpochDay = firstDayDate.toEpochDay() - firstDayDate.dayOfWeek.value % 7 //해당 화면에 보여주는 첫 번째 화면을 구함.


        for (i in 0..<guides.second.size - 1) { //가로줄 만큼 반복
            dayLines.add(DayLine(LocalDate.ofEpochDay(startEpochDay + (guides.first.size - 1) * i)))

            dayViewLines.add(
                DayLineView(
                monthDate = date,
                dayLine = dayLines[i],
                backgroundView = backgroundViewList.subList(i * DayLine.LINE_SIZE, (i + 1) * DayLine.LINE_SIZE),
                dayTvList = dayTvList.subList(i * DayLine.LINE_SIZE, (i + 1) * DayLine.LINE_SIZE))
            )
        }
    }

    fun getSelectDate(): LocalDate {
        return selectedBox?.dayBox?.date ?: date //선택한 박스가 없으면 캘린더 date 전달
    }

    override fun select(selectDate: LocalDate) {
        selectedBox?.unSelect()

        val firstDayDate = LocalDate.of(date.year, date.month, 1) //해당 월에 첫일을 구함.
        val startEpochDay = firstDayDate.toEpochDay() - firstDayDate.dayOfWeek.value % 7 //해당 화면에 보여주는 첫 번째 화면을 구함.

        val dayBoxViewIndex = (selectDate.toEpochDay() - startEpochDay).toInt()

        selectedBox = dayViewLines[dayBoxViewIndex / DayLine.LINE_SIZE]
            .backBoxViewList[dayBoxViewIndex % DayLine.LINE_SIZE]

        dayBoxOnClickListener(selectedBox!!) //외부 시스템과 연결하기 위해서 위 설정이 좋은 것 같다.

        selectedBox!!.select()
    }

    private suspend fun geGroupScheduleInMonth(date: LocalDate): List<GroupScheduleDto> {
        return suspendCoroutine { continuation ->
            groupsScheduleApi.findInMonth(date.toString()).enqueue(object: Callback<List<GroupScheduleDto>> {
                override fun onResponse(p0: Call<List<GroupScheduleDto>>, p1: Response<List<GroupScheduleDto>>) {
                    Log.d("FullCalendarHolder", "groups schedule size : ${p1.body()?.size}")

                    continuation.resume(p1.body() ?: emptyList())
                }

                override fun onFailure(p0: Call<List<GroupScheduleDto>>, p1: Throwable) {
                    continuation.resume(emptyList())
                }
            })
        }
    }


    private suspend fun getPersonalScheduleInMonth(date: LocalDate): List<ScheduleDto> {
        return suspendCoroutine { continuation ->
            scheduleApi.findByMonth(date.toString()).enqueue(object: Callback<List<ScheduleDto>> {
                override fun onResponse(p0: Call<List<ScheduleDto>>, p1: Response<List<ScheduleDto>>) {
                    Log.d("FullCalendarHolder", "personal schedule : ${p1.body()?.size}")

                    continuation.resume(p1.body() ?: emptyList())
                }

                override fun onFailure(p0: Call<List<ScheduleDto>>, p1: Throwable) {
                    continuation.resume(emptyList())
                }
            })
        }
    }



    override fun bind(monthData: MonthData) {
        bindCompleted = CompletableDeferred()

        scheduleMap.clear()

        initDate(monthData.date)
        scope.launch {
            val personalScheduleDtoListDeferred: Deferred<List<ScheduleDto>> = async {
                getPersonalScheduleInMonth(monthData.date)
            }

            val groupScheduleDtoListDeferred: Deferred<List<GroupScheduleDto>> = async {
                geGroupScheduleInMonth(monthData.date)
            }

            val personalScheduleDtoList = personalScheduleDtoListDeferred.await()
            val groupScheduleDtoList = groupScheduleDtoListDeferred.await()

            personalScheduleDtoList.forEach {
                scheduleMap[it.id] = it.toDomain()
            }

            groupScheduleDtoList.forEach {
                scheduleMap[it.id] = it.toDomain()
            }

            withContext(Dispatchers.Main) {
                calendarService.addSchedules(scheduleMap.values.toList(), dayLines)
                calendarService.render(dayLines)
            }

            bindCompleted.complete(Unit)
        }
    }

    override suspend fun addSchedule(schedule: Schedule) { //데이터를 이 함수가 더 빨리 받아올 수도 있음
        bindCompleted.await()
        Log.d("FullCalendarHolder", "addSchedule: id : ${schedule.id} / containsCheck : ${schedule}")

        scheduleMap[schedule.id] = schedule
        calendarService.addSchedule(schedule, dayLines)
        calendarService.render(dayLines)
    }

    override fun removeSchedule(scheduleId: Long) {
        Log.d("FullCalendarHolder", "removeSchedule: id : $scheduleId / containsCheck : ${scheduleMap.containsKey(scheduleId)}")
        calendarService.removeSchedule(scheduleMap[scheduleId] ?: return, dayLines)
        calendarService.render(dayLines)
    }
}