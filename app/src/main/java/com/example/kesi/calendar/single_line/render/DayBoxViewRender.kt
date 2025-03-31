package com.example.kesi.calendar.single_line.render

import android.content.res.ColorStateList
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import com.example.kesi.R
import com.example.kesi.domain.Schedule
import com.example.kesi.domain.ScheduleType

class DayBoxViewRender(
    private val container: ConstraintLayout
) {
    companion object {
        const val CIRCLE_SIZE = 35
    }

    fun crateCircleView(backgroundViewId: Int, schedule: Schedule) = View(container.context).apply {
        layoutParams = ConstraintLayout.LayoutParams(0, 0).apply {
            setMargins(10)
            leftToLeft = backgroundViewId
            rightToRight = backgroundViewId
            topToTop = backgroundViewId
            bottomToBottom = backgroundViewId

            dimensionRatio = "1:1"

            elevation = when(schedule.getType()) {
                ScheduleType.GROUP -> 3f
                ScheduleType.PERSONAL -> 2f
                else -> 1f
            }
        }

        setBackgroundResource(R.drawable.view_line_round)
        backgroundTintList = ColorStateList.valueOf(
            schedule.getSingleLineColor(context)
        )
    }
}