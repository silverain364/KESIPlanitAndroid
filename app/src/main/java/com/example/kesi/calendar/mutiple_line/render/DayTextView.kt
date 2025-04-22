package com.example.kesi.calendar.mutiple_line.render

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.kesi.R
import com.example.kesi.util.ColorManager
import java.time.DayOfWeek
import java.time.LocalDate

class DayTextView(context: Context): AppCompatTextView(context) {
    private lateinit var date: LocalDate
    private var primaryColor = 0
    private var secondaryColor = 0
    private var inverse = false

    @SuppressLint("SetTextI18n")
    fun setDate(date: LocalDate) {
        this.date = date
        inverse = false

        text = date.dayOfMonth.toString()
        primaryColor = when(date.dayOfWeek) {
            DayOfWeek.SUNDAY -> Color.RED
            DayOfWeek.SATURDAY -> Color.BLUE
            else -> Color.WHITE
        }

        //Todo. 공유일에 경우 다른 색으로 변경할 수도 있음
        secondaryColor = ContextCompat.getColor(context, R.color.black)
        setTextColor(primaryColor)
    }

    private fun setPrimaryColor() {
        if(primaryColor != Color.WHITE) return

        setTextColor(if(!inverse) primaryColor else ColorManager.invertColor(primaryColor))
    }

    private fun setSecondaryColor() {
        setTextColor(secondaryColor)
    }

    fun select() {
        setSecondaryColor()
    }

    fun unSelect() {
        setPrimaryColor()
    }

    //Todo. 따로 빼야되나...
    fun inverse() {
        inverse = true
        setPrimaryColor()
    }

    fun uncheckInverse() {
        inverse = false
        setPrimaryColor()
    }

    fun getDate() = date

}