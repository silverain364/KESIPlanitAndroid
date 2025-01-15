package com.example.kesi.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.adapter.CalendarAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var bottomSheetLayout: LinearLayout
    private lateinit var bottomSheetBehavior:BottomSheetBehavior<LinearLayout>;

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_item_month, container, false)
        val monthTv: TextView = root.findViewById(R.id.month)
        val yearTv: TextView = root.findViewById(R.id.year)
        val calendarRv: RecyclerView = root.findViewById(R.id.month_recycler)
        bottomSheetLayout = root.findViewById(R.id.persistent_bottom_sheet);



        val calendarAdapter = CalendarAdapter(mutableListOf(), parentFragmentManager)
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
        initializePersistentBottomSheet()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        return root
    }

    private fun initializePersistentBottomSheet(){
        bottomSheetBehavior=BottomSheetBehavior.from(bottomSheetLayout);

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(p0: View, p1: Int) {
                TODO("Not yet implemented")
            }
            override fun onSlide(p0: View, p1: Float) {
                TODO("Not yet implemented")
            }

        }

        )
    }
}