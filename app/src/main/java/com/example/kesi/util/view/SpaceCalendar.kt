package com.example.kesi.util.view

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.adapter.CalendarHolderFactory
import com.example.kesi.adapter.FullCalendarAdapter
import com.example.kesi.api.ScheduleApi
import com.example.kesi.calendar.domain.DayBox
import com.example.kesi.calendar.render.DayTextView
import com.example.kesi.calendar.view.DayBoxView
import com.example.kesi.data.AddScheduleDto
import com.example.kesi.data.EditScheduleDto
import com.example.kesi.data.MonthData
import com.example.kesi.domain.Schedule
import com.example.kesi.fragment.ScheduleBottomSheet
import com.example.kesi.holder.CalendarHolder
import com.example.kesi.holder.FullCalendarHolder
import com.example.kesi.model.RequestPersonalScheduleDto
import com.example.kesi.model.RequestPersonalUpdateScheduleDto
import com.example.kesi.model.ScheduleDto
import com.example.kesi.setting.RetrofitSetting
import com.example.kesi.util.ActivityResultKeys
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class SpaceCalendar(
    private val monthTv: TextView,
    private val yearTv: TextView,
    private val calendarRv: RecyclerView,
    private val scheduleBottomSheet: ScheduleBottomSheet
) {
    private val retrofit = RetrofitSetting.getRetrofit()
    private val scheduleApi = retrofit.create(ScheduleApi::class.java)
    private val holderFactory = object: CalendarHolderFactory {
        override fun create(
            view: View, guides: Pair<ArrayList<Guideline>, ArrayList<Guideline>>,
            backgroundViewList: List<View>, dayTvList: List<DayTextView>, dayBoxOnClickListener: (DayBoxView) -> Unit
        ): CalendarHolder {
            return FullCalendarHolder(view, guides, backgroundViewList, dayTvList, dayBoxOnClickListener)
        }
    }

    private val calendarAdapter: FullCalendarAdapter = FullCalendarAdapter(
        ArrayList(), holderFactory) { //dayBoxView를 클릭했을 때 이벤트
        scheduleBottomSheet.showSchedules(
            it.dayBox.date, //클릭한 스케줄 정보 보여주기
            it.dayBox.getAllScheduleOrderByHeight().toList()
        )
    }


    ///Todo. 추후 Coroutine 학습필요
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

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

                        val holder = calendarRv.findViewHolderForAdapterPosition(position) as FullCalendarHolder
                        holder.select(holder.date)


                        //부족한 페이지 추가
                        if (position <= 1) { //한 칸 여유를 둠
                            calendarAdapter.addFirstItem(MonthData(data.date.withDayOfMonth(1).minusMonths(position + 1L), arrayListOf()))
                            calendarAdapter.notifyItemInserted(0)
                        }

                        if (position >= calendarAdapter.itemCount - 2) {
                            val diff = calendarAdapter.itemCount - position //이미 하나 앞써 있음
                            calendarAdapter.addItem(MonthData(data.date.withDayOfMonth(1).plusMonths(diff.toLong()), arrayListOf()))
                            calendarAdapter.notifyItemInserted(calendarAdapter.itemCount - 1)
                        }
                    }
                }
            })

            scrollToPosition(calendarAdapter.itemCount / 2)
        }
    }


    fun editSchedule(editScheduleDto: EditScheduleDto) {
        val schedule = editScheduleDto.toDomain()
        val holderPosition = (calendarRv.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val nowHolder = calendarRv.findViewHolderForAdapterPosition(holderPosition) as FullCalendarHolder

        scheduleApi.updateSchedule(RequestPersonalUpdateScheduleDto.from(schedule)).enqueue(object :
            Callback<ScheduleDto> {
            override fun onResponse(p0: Call<ScheduleDto>, p1: Response<ScheduleDto>) {
                if(p1.body() == null) return
                val updateSchedule = p1.body()!!.toDomain()

                val date = updateSchedule.start.withDayOfMonth(1)

                //다른 화면에 있다면
                if (ChronoUnit.MONTHS.between(
                        nowHolder.date.withDayOfMonth(1), date
                    ) != 0L
                ) {
                    calendarRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) { //모든 이동이 완료된 경우
                                coroutineScope.launch {
                                    calendarAdapter.getHolders().forEach {
                                        it.removeSchedule(schedule.id)
                                        it.addSchedule(updateSchedule)
                                    }
                                }

                                calendarRv.removeOnScrollListener(this)
                            }
                        }
                    })

                    calendarMove(date
                        , calendarRv
                        , calendarAdapter)
                    return
                }

                //현재 화면에 있다면
                coroutineScope.launch {
                    Log.d("HomeFragment", "await: ${schedule}")

                    calendarAdapter.getHolders().forEach {
                        it.removeSchedule(schedule.id)
                        it.addSchedule(schedule)
                    }
                }
            }

            override fun onFailure(p0: Call<ScheduleDto>, p1: Throwable) {
                Log.d("HomeFragment", "updateApi-onFailure: ${p1.message}")
            }
        })
    }

     fun removeSchedule(scheduleId: Long) {

        scheduleApi.deleteSchedule(scheduleId).enqueue(object: Callback<Void> {
            override fun onResponse(p0: Call<Void>, p1: Response<Void>) {
                Log.d("HomeFragment", "removeApi-code : ${p1.code()}")

                if(p1.isSuccessful) {
                    calendarAdapter.getHolders().forEach {
                        it.removeSchedule(scheduleId)
                    }
                }
            }

            override fun onFailure(p0: Call<Void>, p1: Throwable) {
                Log.d("HomeFragment", "removeApi-onFailure: ${p1.message}")
            }
        })
    }

     fun calendarMove(date: LocalDate, recyclerView: RecyclerView, adapter: FullCalendarAdapter){
        var diffMonthValue = //Todo. diffMonthValue가 만약 많이 크다면 그냥 배열을 다 삭제하고 이동하는 방법도 나쁘지 않을 것 같다.
            ChronoUnit.MONTHS.between(
                adapter.getItem(0).date.withDayOfMonth(1),
                date.withDayOfMonth(1)
            ).toInt()


        while (diffMonthValue < 0) { //음수라면
            adapter.addFirstItem(MonthData(adapter.getItem(0).date.minusMonths(1), arrayListOf()))
            adapter.notifyItemInserted(0)
            diffMonthValue++
        }

        while (diffMonthValue > adapter.itemCount) { //현재 갱신된 달력보다 크다면
            adapter.addItem(
                MonthData(
                    adapter.getItem(adapter.itemCount - 1).date.plusMonths(
                        1
                    ), arrayListOf()
                )
            )
            adapter.notifyItemInserted(adapter.itemCount - 1)
        }

        recyclerView.smoothScrollToPosition(diffMonthValue)
    }

     fun addSchedule(addScheduleDto: AddScheduleDto) {
        val holderPosition = (calendarRv.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val nowHolder = calendarRv.findViewHolderForAdapterPosition(holderPosition) as FullCalendarHolder
        val bindCompleted = CompletableDeferred<Schedule?>()

        scheduleApi.addSchedule(RequestPersonalScheduleDto.toDto(addScheduleDto)).enqueue(object: Callback<Long> {
            override fun onResponse(p0: Call<Long>, p1: Response<Long>) {
                Log.d("HomeFragment", "body: ${p1.body()}")

                if(p1.body() == null) {
                    bindCompleted.complete(null)
                    return
                }

                val id = p1.body()!!
                val schedule = addScheduleDto.toDomain(id)
                bindCompleted.complete(schedule)
            }

            override fun onFailure(p0: Call<Long>, p1: Throwable) {
                Log.d("HomeFragment", "onFailure: ${p1.message}")
                bindCompleted.complete(null)
            }
        })

        val date = LocalDate.parse(addScheduleDto.startDate).withDayOfMonth(1)
        //다른 화면에 있다면
        if (ChronoUnit.MONTHS.between(
                nowHolder.date.withDayOfMonth(1), date
            ) != 0L
        ) {
            calendarRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) { //모든 이동이 완료된 경우
                        coroutineScope.launch {
                            val schedule = bindCompleted.await()

                            calendarAdapter.getHolders().forEach {
                                it.addSchedule(schedule ?: return@launch)
                            }
                        }

                        calendarRv.removeOnScrollListener(this)
                    }
                }
            })

            calendarMove(date
                , calendarRv
                , calendarAdapter)
            return
        }

        //현재 화면에 있다면
        coroutineScope.launch {
            val schedule = bindCompleted.await()

            Log.d("HomeFragment", "await: ${schedule}")

            calendarAdapter.getHolders().forEach {
                it.addSchedule(schedule ?: return@launch)
            }
        }
    }

    fun cancel() {
        job.cancel()
    }
}