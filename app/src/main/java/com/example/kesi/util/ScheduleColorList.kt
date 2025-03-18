package com.example.kesi.util

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import com.example.kesi.R

object ScheduleColorList {
    private var colorList: List<Color>? = null

    private fun init(context: Context): List<Color> {
        colorList = listOf(
            ContextCompat.getColor(context, R.color.schedule_red).toColor(),
            ContextCompat.getColor(context, R.color.schedule_pink).toColor(),
            ContextCompat.getColor(context, R.color.schedule_orange).toColor(),
            ContextCompat.getColor(context, R.color.schedule_yellow).toColor(),
            ContextCompat.getColor(context, R.color.schedule_green).toColor(),
            ContextCompat.getColor(context, R.color.schedule_cyan).toColor(),
            ContextCompat.getColor(context, R.color.schedule_blue).toColor(),
            ContextCompat.getColor(context, R.color.schedule_purple).toColor()
        )

        return colorList!!
    }
    fun getColorList(context: Context)
        = if(colorList == null) init(context) else colorList!!
}