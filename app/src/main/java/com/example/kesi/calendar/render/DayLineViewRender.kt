package com.example.custormcalendardeom.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kesi.R
import com.example.kesi.domain.Schedule

class DayLineViewRender(private val container: ConstraintLayout) {
    fun createLine(topReferenceViewId: Int, rightReferenceViewId: Int,
                   leftReferenceViewId: Int, schedule: Schedule ,
                   isStart: Boolean = true, isEnd: Boolean = false): View{

        return View(container.context).apply {
            id = View.generateViewId()

            layoutParams = ConstraintLayout.LayoutParams(0, 30).apply {
                leftMargin = if (isStart) 15 else 20
                rightMargin = if (isEnd) 15 else 20 //Todo. 테스트후 20 -> 0
                topMargin = 10

                topToBottom = topReferenceViewId
                leftToLeft = leftReferenceViewId
                rightToRight = rightReferenceViewId
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
            backgroundTintList = ColorStateList.valueOf(schedule.color.toArgb())


        }
    }
}
