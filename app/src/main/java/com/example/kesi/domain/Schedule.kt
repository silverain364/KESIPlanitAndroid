package com.example.kesi.domain

import android.content.Context
import android.graphics.Color
import java.time.LocalDate
import java.time.LocalTime

abstract class Schedule (
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

    abstract fun getType(): ScheduleType

    abstract fun getSingleLineColor(context: Context): Int
}