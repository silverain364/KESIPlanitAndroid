package com.example.kesi.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.activity.AddScheduleActivity
import com.example.kesi.adapter.BottomSheetAdapter
import com.example.kesi.adapter.CalendarAdapter
import com.example.kesi.model.BottomSheetScheduleDto
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class HomeFragment : Fragment() {
        private lateinit var bottomSheetLayout: LinearLayout
        private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>;
        private lateinit var fabAddConstraintLayout: ConstraintLayout;

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val root = inflater.inflate(R.layout.fragment_item_month, container, false)
            val monthTv: TextView = root.findViewById(R.id.month)
            val yearTv: TextView = root.findViewById(R.id.year)
            val calendarRv: RecyclerView = root.findViewById(R.id.month_recycler)
            bottomSheetLayout = root.findViewById(R.id.persistent_bottom_sheet);
            fabAddConstraintLayout = root.findViewById(R.id.fabAddCL)

            val fabAddBtn = fabAddConstraintLayout.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd);
            fabAddBtn.setOnClickListener {
                val intent: Intent = Intent(activity, AddScheduleActivity::class.java)
                startActivity(intent)
            }

            initBottomSheetRecyclerView()
            initializePersistentBottomSheet()

            val calendarAdapter = CalendarAdapter(mutableListOf(), parentFragmentManager, bottomSheetBehavior)
            for (i in -1..1) {
                calendarAdapter.addItem(LocalDate.now().plusMonths(i.toLong()))
            }

            calendarRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            calendarRv.adapter = calendarAdapter

            val snapHelper = PagerSnapHelper()
            if (calendarRv.onFlingListener == null) {
                snapHelper.attachToRecyclerView(calendarRv)
            }

            calendarRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                        val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                        val date = calendarAdapter.getItem(position)

                        monthTv.text = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                        yearTv.text = date.year.toString()

                        if (position == 0) {
                            calendarAdapter.addFirstItem(date.minusMonths(1))
                            calendarAdapter.notifyItemInserted(0)
                        }
                        if (position == calendarAdapter.itemCount - 1) {
                            calendarAdapter.addItem(date.plusMonths(1))
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
                    if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_SETTLING
                        && p1 < 0.5 && p1 > 0.2)
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