package com.example.custormcalendardeom.view

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kesi.R
import com.example.kesi.domain.Schedule
import com.example.kesi.domain.ScheduleType
import com.example.kesi.util.ColorManager

class DayLineViewRender(private val container: ConstraintLayout) {
    private fun createConstraintLayout(topReferenceViewId: Int, rightReferenceViewId: Int,
                                      leftReferenceViewId: Int): ConstraintLayout {

        return ConstraintLayout(container.context).apply {
            id = ConstraintLayout.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(0, 35).apply {
                topMargin = 10

                topToBottom = topReferenceViewId
                leftToLeft = leftReferenceViewId
                rightToRight = rightReferenceViewId
            }
        }
    }

    fun createLine(topReferenceViewId: Int, rightReferenceViewId: Int,
                   leftReferenceViewId: Int, schedule: Schedule ,
                   isStart: Boolean = true, isEnd: Boolean = false): View{

        val layout = createConstraintLayout(topReferenceViewId, rightReferenceViewId, leftReferenceViewId)

        val lineView = View(container.context).apply {
            id = View.generateViewId()

            layoutParams = ConstraintLayout.LayoutParams(0, 0).apply {
                leftMargin = 20
                rightMargin = 20

                leftToLeft = layout.id
                rightToRight = layout.id
                topToTop = layout.id
                bottomToBottom = layout.id
            }

            if(isStart && isEnd) {
                setBackgroundResource(R.drawable.view_line_round)
            }else if(isStart) {
                setBackgroundResource(R.drawable.view_line_round_left)
            }else if(isEnd) {
                setBackgroundResource(R.drawable.view_line_round_right)
            }else{
                setBackgroundResource(R.drawable.view_line)
            }
            backgroundTintList = ColorStateList.valueOf(ColorManager.desaturateColor(schedule.color.toArgb(), 0.5f))

        }
        layout.addView(lineView)


        if(isEnd) {
            val starView = ImageView(container.context).apply {
                id = ImageView.generateViewId()

                if(schedule.getType() == ScheduleType.PERSONAL)setImageResource(R.drawable.star)
                if(schedule.getType() == ScheduleType.GROUP) setImageResource(R.drawable.ic_group_schedule_tmp)

                adjustViewBounds = true //비율 유지
                scaleType = ImageView.ScaleType.FIT_CENTER
                imageTintList = ColorStateList.valueOf(schedule.color.toArgb())


                layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 35).apply {
                    rightMargin = 20 //Todo. 테스트후 20 -> 0

                    rightToRight = layout.id
                    topToTop = layout.id
                    bottomToBottom = layout.id
                }
            }
            layout.addView(starView)
        }

        return layout
    }
}
