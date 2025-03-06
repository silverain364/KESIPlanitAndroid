package com.example.kesi.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.activity.AddScheduleActivity
import com.example.kesi.adapter.BottomSheetAdapter
import com.example.kesi.adapter.FullCalendarAdapter
import com.example.kesi.api.ScheduleApi
import com.example.kesi.model.BottomSheetScheduleDto
import com.example.kesi.model.MonthData
import com.example.kesi.setting.RetrofitSetting
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private lateinit var bottomSheetLayout: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>;
    private lateinit var fabAddConstraintLayout: ConstraintLayout;


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_item_month, container, false)
        val monthTv: TextView = root.findViewById(R.id.month)
        val yearTv: TextView = root.findViewById(R.id.year)
        val calendarRv: RecyclerView = root.findViewById(R.id.month_recycler)
        bottomSheetLayout = root.findViewById(R.id.persistent_bottom_sheet);
        fabAddConstraintLayout = root.findViewById(R.id.fabAddCL)

        monthTv.text = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        yearTv.text = LocalDate.now().year.toString()

        val fabAddBtn = fabAddConstraintLayout.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd);
        fabAddBtn.setOnClickListener {
            val intent: Intent = Intent(activity, AddScheduleActivity::class.java)
            startActivity(intent)
        }

        initBottomSheetRecyclerView()
        initializePersistentBottomSheet()

        //val calendarAdapter = CalendarAdapter(mutableListOf(), parentFragmentManager, bottomSheetBehavior)
        val calendarAdapter = FullCalendarAdapter(ArrayList())

        for (i in -1..1) {
//                calendarAdapter.addItem(LocalDate.now().plusMonths(i.toLong()))
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

        //화면에 보이기 전에 bottom sheet state 설정이 되어있어야 함.
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED


        return root
    }

    private fun initializePersistentBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

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
    }

    private fun initBottomSheetRecyclerView() {
        val recyclerView = bottomSheetLayout.findViewById<RecyclerView>(R.id.ScheduleRecyclerView);


        val items = listOf(
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

        recyclerView.layoutManager = LinearLayoutManager(this.activity)
        recyclerView.adapter = BottomSheetAdapter(items)
    }
}