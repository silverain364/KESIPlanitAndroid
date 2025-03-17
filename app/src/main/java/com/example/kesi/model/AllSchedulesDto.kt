package com.example.kesi.model

import com.example.kesi.R
import com.example.kesi.domain.Schedule
// TODO 날짜 및 시간 수정 필요
data class AllSchedulesDto(
    val iconResId: Int,
    val title: String,
    val date: String,
    val time: String

) {
    companion object  {
        fun toDto(schedule: Schedule): AllSchedulesDto {
            return AllSchedulesDto(
                title = schedule.title,
                iconResId = R.drawable.ic_star2,
                date = schedule.start.toString(),
                time = schedule.startTime.toString()
            )
        }
    }
}
