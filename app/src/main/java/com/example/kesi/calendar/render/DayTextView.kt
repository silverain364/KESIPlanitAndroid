package com.example.kesi.calendar.render

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.kesi.R
import java.time.DayOfWeek
import java.time.LocalDate

class DayTextView(context: Context): AppCompatTextView(context) {
    private lateinit var date: LocalDate
    private var unSelectColor = 0
    private var selectColor = 0

    @SuppressLint("SetTextI18n")
    fun setDate(date: LocalDate) {
        this.date = date

        text = date.dayOfMonth.toString()
        unSelectColor = when(date.dayOfWeek) {
            DayOfWeek.SUNDAY -> Color.RED
            DayOfWeek.SATURDAY -> Color.BLUE
            else -> Color.WHITE
        }

        //Todo. 공유일에 경우 다른 색으로 변경할 수도 있음
        selectColor = ContextCompat.getColor(context, R.color.black)

        setTextColor(unSelectColor)
    }

    fun select() {
        setTextColor(selectColor)
    }

    fun unSelect() {
        setTextColor(unSelectColor)
    }
    fun getDate() = date

}