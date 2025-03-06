package com.example.kesi.calendar.view

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kesi.R
import java.time.LocalDate

class DayBoxViewRender (
    private val container: ConstraintLayout,
){
    fun setupDayTextView(date: LocalDate) {
//        dayTv.apply {
//            text = date.dayOfMonth.toString()
//            setTextColor(getDayColor(date))
//        }
    }

    private fun getDayColor(date: LocalDate) =
        when (date.dayOfWeek.value) {
            6 -> Color.BLUE // 토요일
            7 -> Color.RED  // 일요일
            else -> Color.BLACK
        }

    fun createSingleStar(topReferenceViewId: Int, backgroundViewId: Int) = ImageView(container.context).apply {
        id = ImageView.generateViewId()
        setImageResource(R.drawable.star)
        layoutParams = ConstraintLayout.LayoutParams(30, 30).apply {
            topMargin = 10

            topToBottom = topReferenceViewId
            leftToLeft = backgroundViewId
            rightToRight = backgroundViewId
        }
    }

    fun createDoubleStar(topReferenceViewId: Int, backgroundViewId: Int): List<ImageView> {
        val leftStar = ImageView(container.context).apply {
            id = ImageView.generateViewId()
            setImageResource(R.drawable.star)
        }
        val rightStar = ImageView(container.context).apply {
            id = ImageView.generateViewId()
            setImageResource(R.drawable.star)
        }

        leftStar.layoutParams = ConstraintLayout.LayoutParams(30, 30).apply {
            topToBottom = topReferenceViewId
            leftToLeft = backgroundViewId
            rightToLeft = rightStar.id
        }
        
        rightStar.layoutParams = ConstraintLayout.LayoutParams(30, 30).apply {
            topToBottom = topReferenceViewId
            leftToRight = leftStar.id
            rightToRight = backgroundViewId
        }

        return listOf(leftStar, rightStar)
    }

    fun createOverFlowView(topReferenceViewId: Int, backgroundViewId: Int): TextView = TextView(container.context).apply {
        id = ImageView.generateViewId()
        layoutParams = ConstraintLayout.LayoutParams(
            0,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 10
            leftMargin = 10
            rightMargin = 10
            setBackgroundColor(Color.GRAY)

            topToBottom = topReferenceViewId
            leftToLeft = backgroundViewId
            rightToRight = backgroundViewId
        }
    }

//    fun hideTextView() {
//        dayTv.setTextColor(dayTv.textColors.withAlpha(100))
//    }
}