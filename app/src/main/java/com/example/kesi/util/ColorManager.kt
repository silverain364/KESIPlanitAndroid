package com.example.kesi.util

import android.graphics.Color

class ColorManager {
    companion object {
        fun darkenColorHSL(color: Int, factor: Float = 0.2f): Int {
            val hsl = FloatArray(3)
            android.graphics.Color.colorToHSV(color, hsl)
            hsl[2] *= (1 - factor) //밝기 조절
            return android.graphics.Color.HSVToColor(hsl)
        }

        fun lightenColorHSV(color: Int, factor: Float = 0.2f): Int{
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)

            hsv[2] = (hsv[2] + factor).coerceAtMost(1f)

            return Color.HSVToColor(hsv)
        }

        fun desaturateColor(color: Int, factor: Float = 0.2f): Int {
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)

            hsv[1] = (hsv[1] * (1 - factor)).coerceAtLeast(0f)

            return Color.HSVToColor(hsv)
        }

        fun invertColor(color: Int) : Int{
            val a = Color.alpha(color)
            val r = 255 - Color.red(color)
            val g = 255 - Color.green(color)
            val b = 255 - Color.blue(color)
            return Color.argb(a, r, g, b)
        }
    }
}