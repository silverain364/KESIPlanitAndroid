package com.example.kesi.domain

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.kesi.R
import java.time.LocalDate
import java.time.LocalTime


sealed class Schedule (
    val id: Long,
    val start: LocalDate,
    val end: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val color: Color,
    val title: String,
    val description: String,
    val link: String,
    val place: String,
    val securityLevel: SecurityLevel
) {
    companion object {
        val SCHEDULE_TYPES = Schedule::class.sealedSubclasses
    }
    fun isStar() = start == end
    fun isLine() = start != end

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Schedule

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    abstract fun getSingleLineColor(context: Context): Int
}