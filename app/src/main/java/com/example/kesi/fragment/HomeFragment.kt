package com.example.kesi.fragment

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.activity.AddScheduleActivity
import com.example.kesi.adapter.BottomSheetAdapter
import com.example.kesi.adapter.FullCalendarAdapter
import com.example.kesi.data.AddScheduleDto
import com.example.kesi.model.BottomSheetScheduleDto
import com.example.kesi.data.MonthData
import com.example.kesi.domain.Schedule
import com.example.kesi.util.ActivityResultKeys
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private lateinit var bottomSheetLayout: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>;
    private lateinit var fabAddConstraintLayout: ConstraintLayout;
    private lateinit var bottomSheetAdapter: BottomSheetAdapter
    private lateinit var addScheduleLauncher: ActivityResultLauncher<Intent>
    private lateinit var editScheduleLauncher: ActivityResultLauncher<Intent>

    private lateinit var calendarAdapter: FullCalendarAdapter
    private lateinit var calendarRv: RecyclerView

    private lateinit var scheduleBottomSheet: ScheduleBottomSheet

    ///Todo. 추후 Coroutine 학습필요
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_item_month, container, false)
        val monthTv: TextView = root.findViewById(R.id.month)
        val yearTv: TextView = root.findViewById(R.id.year)
        calendarRv = root.findViewById(R.id.month_recycler)
        bottomSheetLayout = root.findViewById(R.id.persistent_bottom_sheet);
        fabAddConstraintLayout = root.findViewById(R.id.fabAddCL)

        monthTv.text = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        yearTv.text = LocalDate.now().year.toString()


        setAddScheduleLauncher()
        setEditScheduleLauncher()
        bottomSheetAdapter = BottomSheetAdapter(arrayListOf(), editScheduleLauncher)

        scheduleBottomSheet = ScheduleBottomSheet(bottomSheetAdapter, bottomSheetLayout)


        val fabAddBtn = fabAddConstraintLayout.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd);
        fabAddBtn.setOnClickListener {
            val intent = Intent(activity, AddScheduleActivity::class.java)
            addScheduleLauncher.launch(intent)
        }

        calendarAdapter = FullCalendarAdapter(ArrayList(), scheduleBottomSheet)

        for (i in -1..1) {
            calendarAdapter.addItem(MonthData(LocalDate.now().plusMonths(i.toLong()), arrayListOf()))
        }

        calendarRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        calendarRv.adapter = calendarAdapter

        val snapHelper = PagerSnapHelper()
        if (calendarRv.onFlingListener == null) {
            snapHelper.attachToRecyclerView(calendarRv)
        }

        calendarRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (position < 0) return

                    val data = calendarAdapter.getItem(position)

                    monthTv.text = data.date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    yearTv.text = data.date.year.toString()

                    if (position == 0) {
                        calendarAdapter.addFirstItem(MonthData(data.date.minusMonths(1), arrayListOf()))
                        calendarAdapter.notifyItemInserted(0)
                    }

                    if (position == calendarAdapter.itemCount - 1) {
                        calendarAdapter.addItem(MonthData(data.date.plusMonths(1), arrayListOf()))
                        calendarAdapter.notifyItemInserted(calendarAdapter.itemCount - 1)
                    }
                }
            }
        })

        calendarRv.scrollToPosition(calendarAdapter.itemCount / 2)

        return root
    }

    private fun setAddScheduleLauncher() {
        addScheduleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            Log.d("HomeFragment", "setAddScheduleLauncher: rst : ${it.resultCode}")
            if(it.resultCode != Activity.RESULT_OK) return@registerForActivityResult

            val addScheduleDto:AddScheduleDto =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.data?.getParcelableExtra("schedule", AddScheduleDto::class.java) ?: return@registerForActivityResult
                }else {
                    it.data?.getParcelableExtra("schedule") ?: return@registerForActivityResult
                }

            addSchedule(addScheduleDto)
        }
    }

    private fun editSchedule(){

    }

    private fun removeSchedule() {

    }

    private fun setEditScheduleLauncher() {
        editScheduleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode != Activity.RESULT_OK) return@registerForActivityResult

            val actionType = it.data?.getStringExtra(ActivityResultKeys.ACTION_TYPE) ?: return@registerForActivityResult

            when(actionType) {
                ActivityResultKeys.EDIT -> editSchedule()
                ActivityResultKeys.DELETE -> removeSchedule()
                else -> throw RuntimeException("unknown action type")
            }

        }
    }

    private fun addSchedule(addScheduleDto: AddScheduleDto) {
        //다른 화면에 있다면
        if(ChronoUnit.MONTHS.between(calendarAdapter.getNowHolder().date.withDayOfMonth(1)
            ,LocalDate.parse(addScheduleDto.startDate).withDayOfMonth(1)) != 0L) {
            var diffMonthValue = //Todo. diffMonthValue가 만약 많이 크다면 그냥 배열을 다 삭제하고 이동하는 방법도 나쁘지 않을 것 같다.
                ChronoUnit.MONTHS.between(calendarAdapter.getItem(0).date.withDayOfMonth(1), LocalDate.parse(addScheduleDto.startDate).withDayOfMonth(1)).toInt()


            while (diffMonthValue < 0) { //음수라면
                calendarAdapter.addFirstItem(MonthData(calendarAdapter.getItem(0).date.minusMonths(1), arrayListOf()))
                calendarAdapter.notifyItemInserted(0)
                diffMonthValue++
            }

            while (diffMonthValue > calendarAdapter.itemCount) { //현재 갱신된 달력보다 크다면
                calendarAdapter.addItem(MonthData(calendarAdapter.getItem(calendarAdapter.itemCount - 1).date.plusMonths(1), arrayListOf()))
                calendarAdapter.notifyItemInserted(calendarAdapter.itemCount - 1)
            }

            calendarRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) { //모든 이동이 완료된 경우
                        coroutineScope.launch {
                            calendarAdapter.getNowHolder().addSchedule(addScheduleDto)
                        }
                        calendarRv.removeOnScrollListener(this)
                    }
                }
            })

            calendarRv.smoothScrollToPosition(diffMonthValue)
            return
        }

        //현재 화면에 있다면
        coroutineScope.launch {
            calendarAdapter.getNowHolder().addSchedule(addScheduleDto)
        }
    }


    private fun testBottomSchedule(): ArrayList<BottomSheetScheduleDto> = arrayListOf(
        BottomSheetScheduleDto(R.drawable.ic_star2, "오후 미팅", "15:00 - 17:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "아침 회의", "09:00 - 10:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "팀 점심", "12:00 - 13:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "팀 점심", "12:00 - 13:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "팀 점심", "12:00 - 13:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "팀 점심", "12:00 - 13:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "팀 점심", "12:00 - 13:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "아침 회의", "09:00 - 10:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "팀 점심", "12:00 - 13:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "팀 점심", "12:00 - 13:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "팀 점심", "12:00 - 13:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "팀 점심", "12:00 - 13:00"),
        BottomSheetScheduleDto(R.drawable.ic_star2, "팀 점심", "12:00 - 13:00")
    )

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}

class ScheduleBottomSheet(
    private val bottomSheetAdapter: BottomSheetAdapter,
    private val bottomSheetLayout: LinearLayout,
) {
    private val dateTv: TextView = bottomSheetLayout.findViewById(R.id.dayTv)
    private val recyclerView = bottomSheetLayout.findViewById<RecyclerView>(R.id.ScheduleRecyclerView)
    private val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> = BottomSheetBehavior.from(bottomSheetLayout);
    private val dateFormat = DateTimeFormatter.ofPattern("d일 EEE", Locale.US)

    init {
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(p0: View, state: Int) {
            }

            override fun onSlide(p0: View, p1: Float) {
                Log.d("bottom", " boottom : " + bottomSheetBehavior.state);
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_SETTLING
                    && p1 < 0.5 && p1 > 0.2
                )
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(bottomSheetLayout.context)
        recyclerView.adapter = bottomSheetAdapter

        //화면에 보이기 전에 bottom sheet state 설정이 되어있어야 함.
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun showSchedules(date: LocalDate, schedules: List<Schedule>) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        bottomSheetAdapter.reset()

        schedules.forEach {
            bottomSheetAdapter.addItem(it)
        }

        dateTv.text = dateFormat.format(date)
    }
}