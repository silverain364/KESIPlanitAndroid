package com.example.kesi.data.model

import com.example.kesi.R
import com.example.kesi.domain.Schedule

data class BottomSheetScheduleDto (
    val iconResId: Int,
    val title: String,
    val time: String

) {
    companion object  {
        fun toDto(schedule: Schedule): com.example.kesi.data.model.BottomSheetScheduleDto {
            return com.example.kesi.data.model.BottomSheetScheduleDto(
                title = schedule.title,
                iconResId = R.drawable.ic_star2,
                time = schedule.start.toString()
            )
        }
    }
}