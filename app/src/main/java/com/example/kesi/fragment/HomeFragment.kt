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
import com.example.kesi.activity.EditScheduleActivity
import com.example.kesi.adapter.BottomSheetAdapter
import com.example.kesi.adapter.FullCalendarAdapter
import com.example.kesi.api.ScheduleApi
import com.example.kesi.data.AddScheduleDto
import com.example.kesi.data.EditScheduleDto
import com.example.kesi.model.BottomSheetScheduleDto
import com.example.kesi.data.MonthData
import com.example.kesi.domain.Schedule
import com.example.kesi.holder.FullCalendarHolder
import com.example.kesi.model.RequestPersonalScheduleDto
import com.example.kesi.model.RequestPersonalUpdateScheduleDto
import com.example.kesi.model.ScheduleDto
import com.example.kesi.setting.RetrofitSetting
import com.example.kesi.util.ActivityResultKeys
import com.example.kesi.util.view.SpaceCalendar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private lateinit var bottomSheetLayout: LinearLayout
    private lateinit var fabAddConstraintLayout: ConstraintLayout;
    private lateinit var bottomSheetAdapter: BottomSheetAdapter
    private lateinit var addScheduleLauncher: ActivityResultLauncher<Intent>
    private lateinit var editScheduleLauncher: ActivityResultLauncher<Intent>

    private lateinit var calendarRv: RecyclerView
    private lateinit var scheduleBottomSheet: ScheduleBottomSheet

    private lateinit var spaceCalendar: SpaceCalendar


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_item_month, container, false)
        val monthTv: TextView = root.findViewById(R.id.month)
        val yearTv: TextView = root.findViewById(R.id.year)
        calendarRv = root.findViewById(R.id.month_recycler)
        bottomSheetLayout = root.findViewById(R.id.persistent_bottom_sheet);
        fabAddConstraintLayout = root.findViewById(R.id.fabAddCL)


        setAddScheduleLauncher()
        setEditScheduleLauncher()
        bottomSheetAdapter = BottomSheetAdapter(arrayListOf(), editScheduleLauncher)

        scheduleBottomSheet = ScheduleBottomSheet(bottomSheetAdapter, bottomSheetLayout)

        spaceCalendar = SpaceCalendar(monthTv, yearTv, calendarRv, scheduleBottomSheet)

        val fabAddBtn = fabAddConstraintLayout.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd);
        fabAddBtn.setOnClickListener {
            val intent = Intent(activity, AddScheduleActivity::class.java)
            addScheduleLauncher.launch(intent)
        }

        return root
    }

    private fun setAddScheduleLauncher() {
        addScheduleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d("HomeFragment", "setAddScheduleLauncher: rst : ${it.resultCode}")
            if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult

            val addScheduleDto: AddScheduleDto =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.data?.getParcelableExtra("schedule", AddScheduleDto::class.java)
                        ?: return@registerForActivityResult
                } else {
                    it.data?.getParcelableExtra("schedule") ?: return@registerForActivityResult
                }

            spaceCalendar.addSchedule(addScheduleDto)
        }
    }

    private fun setEditScheduleLauncher() {
        editScheduleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult

            val actionType = it.data?.getStringExtra(ActivityResultKeys.ACTION_TYPE) ?: return@registerForActivityResult

            when (actionType) {
                ActivityResultKeys.EDIT -> {
                    val editedSchedule =
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            it.data!!.getParcelableExtra("schedule", EditScheduleDto::class.java)
                        else
                            it.data!!.getParcelableExtra("schedule")

                    editedSchedule ?: throw RuntimeException("empty edited schedule!")
                    spaceCalendar.editSchedule(editedSchedule)
                }

                ActivityResultKeys.DELETE -> spaceCalendar.removeSchedule(it.data!!.getLongExtra("scheduleId", -1L))
                else -> throw RuntimeException("unknown action type")
            }
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
        spaceCalendar.cancel()
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