package com.example.kesi.model

import com.example.kesi.domain.Schedule
import java.time.LocalDate

data class MonthData(val date: LocalDate, val scheduleList: List<Schedule>)