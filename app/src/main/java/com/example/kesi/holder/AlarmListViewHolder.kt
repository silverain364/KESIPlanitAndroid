package com.example.kesi.holder

import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.databinding.ItemAlarmListBinding
import com.example.kesi.model.AlarmDataDto
import com.example.kesi.model.AlarmType
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

class AlarmListViewHolder(val binding: ItemAlarmListBinding): RecyclerView.ViewHolder(binding.root) {
    private val iconIv = binding.ivIcon
    private val contentTv = binding.contentTv
    private val timeTv = binding.timeTv
    fun bind(alarmDataDto: AlarmDataDto){
        contentTv.text = alarmDataDto.content
        classificationAlarm(alarmDataDto)
    }

    private fun classificationAlarm(alarmDataDto: AlarmDataDto){
        when(alarmDataDto.alarmType) {
            AlarmType.GROUP -> settingGroup(alarmDataDto)
            AlarmType.SCHEDULE -> settingBasic(alarmDataDto)
            AlarmType.BASIC -> settingSchedule(alarmDataDto)
        }
    }

    private fun settingGroup(alarmDataDto: AlarmDataDto){
        iconIv.setImageResource(R.drawable.ic_notification)
        timeTv.text = pastTimeCals(LocalDateTime.parse(alarmDataDto.createTime))
    }

    private fun settingBasic(alarmDataDto: AlarmDataDto){
        iconIv.setImageResource(R.drawable.ic_notification_group)
        timeTv.text = pastTimeCals(LocalDateTime.parse(alarmDataDto.createTime))
    }

    private fun settingSchedule(alarmDataDto: AlarmDataDto){
        iconIv.setImageResource(R.drawable.ic_notification_schedule)
        timeTv.text = "group name" + " · " + pastTimeCals(LocalDateTime.parse(alarmDataDto.createTime))
    }

    private fun pastTimeCals(date: LocalDateTime): String {
        val now = LocalDateTime.now()
        val period = Period.between(date.toLocalDate(), now.toLocalDate())
        val duration = Duration.between(date, now)

        if(period.years > 0)
            return "${period.years}년 전"

        if(period.months > 0)
            return "${period.months}월 전"

        if(period.days > 0)
            return "${period.days}일 전"

        if(duration.seconds > 3600)
            return "${duration.seconds / 3600}시간 전"

        if(duration.seconds > 60)
            return "${duration.seconds / 60}분 전"

        return "${duration.seconds}초 전"
    }
}