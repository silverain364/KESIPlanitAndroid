package com.example.kesi.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Constraints
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.calendar.view.DayTextView
import com.example.kesi.holder.FullCalendarHolder
import com.example.kesi.data.MonthData
import com.example.kesi.util.view.GuideRender
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.time.LocalDate

class FullCalendarAdapter (
    private val monthData: MutableList<MonthData>,
    private val bottomSheetBehavior: BottomSheetBehavior<LinearLayout>,
    private val bottomSheetAdapter: BottomSheetAdapter
) : RecyclerView.Adapter<FullCalendarHolder>() {
    private val guideRender = GuideRender()
    private lateinit var nowHolder: FullCalendarHolder

    companion object {
        private const val VIEW_TYPE_FIVE = 1;
        private const val VIEW_TYPE_SIX = 2;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullCalendarHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false);
        val layout = view.findViewById<ConstraintLayout>(R.id.main)

        val guides =
            if (viewType == VIEW_TYPE_FIVE)
                guideRender.createGuideLine(
                    layout,
                    vertical = 7,
                    horizontal = 5
                    )
            else
                guideRender.createGuideLine(
                    layout,
                    vertical = 7,
                    horizontal = 6
                )

        return FullCalendarHolder(
            view,
            guides,
            createBackgroundView(view, guides),
            createTextView(view, guides),
            bottomSheetBehavior,
            bottomSheetAdapter
        )
    }

    override fun getItemViewType(position: Int): Int {
        val date = monthData[position].date
        val first = LocalDate.of(date.year, date.month, 1)

        //35칸에 담을 수 있는지 확인(첫 번째 요일 + 그 달에 일 수)
        return if (first.dayOfWeek.value % 7 + first.lengthOfMonth() > 7 * 5) VIEW_TYPE_SIX else VIEW_TYPE_FIVE
    }

    override fun getItemCount(): Int {
        return monthData.size
    }

    fun addItem(date: MonthData) {
        monthData.add(date)
    }

    fun addFirstItem(date: MonthData) {
        monthData.add(0, date)
    }

    fun getItem(position: Int): MonthData {
        return monthData[position]
    }

    override fun onBindViewHolder(holder: FullCalendarHolder, position: Int) {
        holder.bind(monthData[position])
        nowHolder = holder
    }

    fun getNowHolder() = nowHolder

    private fun createBackgroundView(
        view: View,
        guides: Pair<ArrayList<Guideline>, ArrayList<Guideline>>
    ): ArrayList<View> {
        val backgroundViewList = ArrayList<View>()
        val constraints = view.findViewById<ConstraintLayout>(R.id.main)

        for (i in 1..<guides.first.size) {
            for (j in 1..<guides.second.size) {
                backgroundViewList.add(View(view.context).apply {
                    id = View.generateViewId()
                    layoutParams = ConstraintLayout.LayoutParams(0, 0)
                })

                constraints.addView(backgroundViewList.last())
            }
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraints)
        var k = 0;
        for (j in 1..<guides.second.size) {
            for (i in 1..<guides.first.size) {
                val v = backgroundViewList[k++];
                constraintSet.connect(
                    v.id,
                    ConstraintSet.LEFT,
                    guides.first[i - 1].id,
                    ConstraintSet.RIGHT
                )
                constraintSet.connect(
                    v.id,
                    ConstraintSet.RIGHT,
                    guides.first[i].id,
                    ConstraintSet.LEFT
                )
                constraintSet.connect(
                    v.id,
                    ConstraintSet.TOP,
                    guides.second[j - 1].id,
                    ConstraintSet.BOTTOM
                )
                constraintSet.connect(
                    v.id,
                    ConstraintSet.BOTTOM,
                    guides.second[j].id,
                    ConstraintSet.TOP
                )
            }
        }

        constraintSet.applyTo(constraints)
        return backgroundViewList
    }

    private fun createTextView(
        view: View,
        guides: Pair<ArrayList<Guideline>, ArrayList<Guideline>>
    ): ArrayList<DayTextView> {
        val dayTvList = ArrayList<DayTextView>()
        val constraints = view.findViewById<ConstraintLayout>(R.id.main)

        for (i in 1..<guides.first.size) {
            for (j in 1..<guides.second.size) {
                dayTvList.add(DayTextView(view.context).apply {
                    id = TextView.generateViewId()
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    setTextColor(resources.getColor(android.R.color.black, null))
                    layoutParams = ConstraintLayout.LayoutParams(
                        Constraints.LayoutParams.WRAP_CONTENT,
                        Constraints.LayoutParams.WRAP_CONTENT
                    )
                })

                constraints.addView(dayTvList.last())
            }
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraints)
        var k = 0;
        for (j in 1..<guides.second.size) {
            for (i in 1..<guides.first.size) {
                val tv = dayTvList[k++];
                constraintSet.connect(
                    tv.id,
                    ConstraintSet.LEFT,
                    guides.first[i - 1].id,
                    ConstraintSet.RIGHT
                )
                constraintSet.connect(
                    tv.id,
                    ConstraintSet.RIGHT,
                    guides.first[i].id,
                    ConstraintSet.LEFT
                )
                constraintSet.connect(
                    tv.id,
                    ConstraintSet.TOP,
                    guides.second[j - 1].id,
                    ConstraintSet.BOTTOM
                )
            }
        }

        constraintSet.applyTo(constraints)
        return dayTvList
    }
}