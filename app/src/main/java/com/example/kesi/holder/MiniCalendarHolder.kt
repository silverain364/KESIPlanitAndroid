package com.example.kesi.holder

import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import com.example.kesi.R
import com.example.kesi.api.GroupScheduleApi
import com.example.kesi.api.PersonalScheduleApi
import com.example.kesi.calendar.mutiple_line.render.DayTextView
import com.example.kesi.calendar.mutiple_line.view.DayBoxView
import com.example.kesi.calendar.single_line.domain.DayLine
import com.example.kesi.calendar.single_line.service.CalendarRenderService
import com.example.kesi.calendar.single_line.service.CalendarService
import com.example.kesi.data.MonthData
import com.example.kesi.domain.OtherSchedule
import com.example.kesi.domain.Schedule
import com.example.kesi.model.GroupDto
import com.example.kesi.model.GroupScheduleDto
import com.example.kesi.model.OtherScheduleDto
import com.example.kesi.model.ScheduleDto
import com.example.kesi.setting.RetrofitSetting
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MiniCalendarHolder (
    itemView: View,
    guides: Pair<ArrayList<Guideline>, ArrayList<Guideline>>,
    backgroundViewList: List<View>, //Todo. 추후 터치 인식을 위해서 forwardViewList를 만들면 괜찮을듯
    dayTvList: List<DayTextView>,
    dayBoxOnClickListener: (DayBoxView) -> (Unit) = { },
    private val groupDto: GroupDto
): CalendarHolder(
    itemView,
    guides,
    backgroundViewList,
    dayTvList,
    dayBoxOnClickListener
) {
    private val container = itemView.findViewById<ConstraintLayout>(R.id.main)
    lateinit var date: LocalDate
    private val scheduleMap: HashMap<Long, Schedule> = HashMap()

    private val retrofit = RetrofitSetting.getRetrofit()
    private val scheduleApi = retrofit.create(PersonalScheduleApi::class.java)
    private val groupsScheduleApi = retrofit.create(GroupScheduleApi::class.java)

    private val calendarRenderService = CalendarRenderService(container, backgroundViewList, dayTvList)
    private val calendarService = CalendarService(calendarRenderService)

    private var bindCompleted = CompletableDeferred<Unit>()

    private val scope = CoroutineScope(Dispatchers.IO)

    private val dayLines = ArrayList<DayLine>()

    init {
        var cnt = 0
        dayTvList.forEach {
            it.elevation = 99f
            (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                bottomToBottom = backgroundViewList[cnt++].id
            }
        }
    }

    private suspend fun getOthersScheduleInMonth(data: LocalDate): List<OtherScheduleDto> {
        return suspendCoroutine { continuation ->
            groupsScheduleApi.findOthersInMonth(date.toString(), groupDto.gid).enqueue(object: Callback<List<OtherScheduleDto>> {
                override fun onResponse(p0: Call<List<OtherScheduleDto>>, p1: Response<List<OtherScheduleDto>>) {
                    Log.d("FullCalendarHolder", "others schedule size : ${p1.body()?.size}")

                    continuation.resume(p1.body() ?: emptyList())
                }

                override fun onFailure(p0: Call<List<OtherScheduleDto>>, p1: Throwable) {
                    continuation.resume(emptyList())
                }
            })
        }
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


    override fun select(selectDate: LocalDate) {

    }

    private fun initDate(monthDate: LocalDate) {
        dayLines.clear()
        this.date = monthDate

        val firstDayDate = LocalDate.of(date.year, date.month, 1) //해당 월에 첫일을 구함.
        val startEpochDay = firstDayDate.toEpochDay() - firstDayDate.dayOfWeek.value % 7 //해당 화면에 보여주는 첫 번째 화면을 구함.

        for(i in 0..<guides.second.size-1) {
            dayLines.add(DayLine(
                LocalDate.ofEpochDay(startEpochDay + (DayLine.LINE_SIZE) * i)))
        }
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

        initDate(monthData.date)
        scope.launch {
            val personalScheduleDtoListDeferred: Deferred<List<ScheduleDto>> = async {
                getPersonalScheduleInMonth(monthData.date)
            }

            val groupScheduleDtoListDeferred: Deferred<List<GroupScheduleDto>> = async {
                geGroupScheduleInMonth(monthData.date)
            }

            val othersScheduleDtoListDeferred: Deferred<List<OtherScheduleDto>> = async {
                getOthersScheduleInMonth(monthData.date)
            }

            val personalScheduleDtoList = personalScheduleDtoListDeferred.await()
            val groupScheduleDtoList = groupScheduleDtoListDeferred.await()
            val othersScheduleDto = othersScheduleDtoListDeferred.await()


            //Todo. 추후 securityId로 접근하도록 수정이 필요할 수 있음
            othersScheduleDto.forEach {
                scheduleMap[it.id] = it.toDomain()
            }

            personalScheduleDtoList.forEach {
                scheduleMap[it.id] = it.toDomain()
            }

            groupScheduleDtoList.forEach {
                Log.d("MiniCalendar", "group calendar id $it")
                if(it.sourceCalendarId == groupDto.calendarId) {
                    scheduleMap[it.id] = it.toDomain()
                }else {
                    scheduleMap[it.id] = it.toPersonalSchedule()
                }
            }


            withContext(Dispatchers.Main) {
                calendarService.addSchedules(scheduleMap.values.toList(), dayLines)
                calendarService.render(dayLines)
            }

            bindCompleted.complete(Unit)
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