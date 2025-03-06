package com.example.kesi.calendar.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.widget.AppCompatTextView
import java.time.DayOfWeek
import java.time.LocalDate

class DayTextView(context: Context): AppCompatTextView(context) {
    private lateinit var date: LocalDate

    @SuppressLint("SetTextI18n")
    fun setDate(date: LocalDate) {
        this.date = date

        text = date.dayOfMonth.toString()

        setTextColor(when(date.dayOfWeek) {
            DayOfWeek.SUNDAY -> Color.RED
            DayOfWeek.SATURDAY -> Color.BLUE
            else -> Color.WHITE
        })
    }

    fun getDate() = date

}