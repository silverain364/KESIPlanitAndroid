package com.example.kesi.calendar.mutiple_line.render

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import com.example.kesi.R
import com.example.kesi.domain.*
import java.time.LocalDate

class DayBoxViewRender (
    private val container: ConstraintLayout,
){
    companion object {
        const val STAR_SIZE = 35
    }

    private fun getInitSettingImageView(schedule: Schedule) = ImageView(container.context).apply {
        id = ImageView.generateViewId()

        if(schedule is PersonalSchedule)setImageResource(R.drawable.star)
        if(schedule is GroupSchedule) setImageResource(R.drawable.ic_group_schedule_tmp)

        adjustViewBounds = true //비율 유지
        scaleType = ImageView.ScaleType.FIT_CENTER
        imageTintList = ColorStateList.valueOf(schedule.color.toArgb())
    }


    fun createSingleStar(topReferenceViewId: Int, backgroundViewId: Int, schedule: Schedule)
    = getInitSettingImageView(schedule).apply {
        layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, STAR_SIZE).apply {
            topMargin = 10

            topToBottom = topReferenceViewId
            leftToLeft = backgroundViewId
            rightToRight = backgroundViewId
        }

    }

    fun createDoubleStar(topReferenceViewId: Int, backgroundViewId: Int, schedules: List<Schedule>): List<ImageView> {
        val leftStar = getInitSettingImageView(schedules.first())
        val rightStar = getInitSettingImageView(schedules.last())

        leftStar.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, STAR_SIZE).apply {
            topToBottom = topReferenceViewId
            leftToLeft = backgroundViewId
            rightToLeft = rightStar.id
        }
        
        rightStar.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, STAR_SIZE).apply {
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
            setTextColor(Color.WHITE)
            setTextColor(textColors.withAlpha(200))

            topToBottom = topReferenceViewId
            leftToLeft = backgroundViewId
            rightToRight = backgroundViewId
        }
    }

//    fun hideTextView() {
//        dayTv.setTextColor(dayTv.textColors.withAlpha(100))
//    }
}