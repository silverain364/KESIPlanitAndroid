package com.example.kesi.domain

import android.graphics.Color
import java.time.LocalDate
import java.time.LocalTime

class Schedule (
    val id: Long,
    val start: LocalDate,
    val end: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val color: Color,
    val title: String,
    val description: String
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
}