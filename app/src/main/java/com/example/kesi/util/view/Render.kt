package com.example.kesi.util.view

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

class Render {
    companion object {
        fun dpToPx(context: Context, dp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
            )
        }
    }
}