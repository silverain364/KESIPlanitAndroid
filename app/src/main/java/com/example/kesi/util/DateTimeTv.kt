package com.example.kesi.util

import android.view.View.OnClickListener
import android.widget.TextView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateTimeTv(
    private var date: LocalDate,
    private var time: LocalTime,
    private val dateTv: TextView,
    private val timeTv: TextView
){
    val dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREAN)
    val timeFormat = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN)

    init {
        setDate(date)
        setTime(time)
    }

    fun setDateTime(dateTime: LocalDateTime) {
        setDate(dateTime.toLocalDate())
        setTime(dateTime.toLocalTime())
    }

    fun setDate(date: LocalDate) {
        this.date = date
        dateTv.text = dateFormat.format(date)
    }

    fun setTime(time: LocalTime) {
        this.time = time
        timeTv.text = timeFormat.format(time)
    }

    fun getDate() = date
    fun getTime() = time

    fun getDateTime() = LocalDateTime.of(date, time)

    fun setDateTvSetOnClickListener(l: OnClickListener) {
        dateTv.setOnClickListener(l)
    }

    fun setTimeTvSetOnClickListener(l: OnClickListener) {
        timeTv.setOnClickListener(l)
    }
}