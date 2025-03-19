package com.example.kesi.calendar.view

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.example.kesi.R
import com.example.kesi.calendar.domain.DayBox
import com.example.kesi.calendar.render.DayTextView
import java.time.LocalDate

data class DayBoxView(
    val monthDate: LocalDate,
    val dayBox: DayBox,
    private val backgroundView: View,
    private val dayTv: DayTextView
) {
    init {
        dayTv.setDate(dayBox.date)
        if(dayBox.date.month != monthDate.month)
            dayTv.alpha = 0.6f
    }

    fun select() {
        backgroundView.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(dayTv.context, R.color.white)
        )

        dayTv.select()
    }

    fun unSelect() {
        backgroundView.backgroundTintList = ColorStateList.valueOf(
            backgroundView.context.getColor(R.color.black)
        )

        dayTv.unSelect()
    }
}
