package com.example.kesi.util.view

import android.content.Context
import android.util.DisplayMetrics

class Render {
    companion object {
        fun pxToDp(context: Context, px: Float): Float{
            val metrics = context.resources.displayMetrics
            val dp = px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
            return dp
        }
    }
}