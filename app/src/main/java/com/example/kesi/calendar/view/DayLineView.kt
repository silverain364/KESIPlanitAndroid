package com.example.kesi.calendar.view

import android.view.View
import com.example.kesi.calendar.domain.DayLine
import com.example.kesi.calendar.render.DayTextView
import java.time.LocalDate

data class DayLineView(
    val monthDate: LocalDate,
    val dayLine: DayLine,
    private val backgroundView: List<View>,
    private val dayTvList: List<DayTextView>
) {
    val backBoxViewList: List<DayBoxView> = List(DayLine.LINE_SIZE) { i ->
        DayBoxView(monthDate, dayLine.dayBoxes[i], backgroundView[i], dayTvList[i])
    }
}