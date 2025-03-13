package com.example.kesi.model

import com.example.kesi.R
import com.example.kesi.domain.Schedule

data class BottomSheetScheduleDto (
    val iconResId: Int,
    val title: String,
    val time: String

) {
    companion object  {
        fun toDto(schedule: Schedule): BottomSheetScheduleDto {
            return BottomSheetScheduleDto(
                title = schedule.title,
                iconResId = R.drawable.ic_star2,
                time = schedule.start.toString()
            )
        }
    }
}