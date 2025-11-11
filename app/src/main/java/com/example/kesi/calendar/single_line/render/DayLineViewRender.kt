package com.example.kesi.calendar.single_line.render

import android.app.Person
import android.content.res.ColorStateList
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kesi.R
import com.example.kesi.domain.*

class DayLineViewRender(
    private val container: ConstraintLayout
) {
    fun createLine(leftReferenceViewId: Int, rightReferenceViewId: Int, schedule:Schedule)
        = View(container.context).apply {
        id = View.generateViewId()

        layoutParams = ConstraintLayout.LayoutParams(0, 0).apply {
            leftMargin = 10
            rightMargin = 10
            topMargin = 10
            bottomMargin = 10

            leftToLeft = leftReferenceViewId
            rightToRight = rightReferenceViewId
            topToTop = leftReferenceViewId
            bottomToBottom = leftReferenceViewId

            elevation = when(schedule) {
                is GroupSchedule -> 3f
                is PersonalSchedule -> 2f
                is OtherSchedule -> 1f
            }
        }

        setBackgroundResource(R.drawable.view_line_round)
        backgroundTintList = ColorStateList.valueOf(
            schedule.getSingleLineColor(context)
        )
    }
}