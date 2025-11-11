package com.example.kesi.ui.notification

import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.databinding.ItemAlarmListBinding
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period

class AlarmListViewHolder(val binding: ItemAlarmListBinding): RecyclerView.ViewHolder(binding.root) {
    private val iconIv = binding.ivIcon
    private val contentTv = binding.contentTv
    private val timeTv = binding.timeTv
    fun bind(alarmDataDto: com.example.kesi.data.model.AlarmDataDto){
        contentTv.text = alarmDataDto.content
        classificationAlarm(alarmDataDto)
    }

    private fun classificationAlarm(alarmDataDto: com.example.kesi.data.model.AlarmDataDto){
        when(alarmDataDto.alarmType) {
            com.example.kesi.data.model.AlarmType.GROUP -> settingGroup(alarmDataDto)
            com.example.kesi.data.model.AlarmType.SCHEDULE -> settingBasic(alarmDataDto)
            com.example.kesi.data.model.AlarmType.BASIC -> settingSchedule(alarmDataDto)
        }
    }

    private fun settingGroup(alarmDataDto: com.example.kesi.data.model.AlarmDataDto){
        iconIv.setImageResource(R.drawable.ic_notification)
        timeTv.text = pastTimeCals(LocalDateTime.parse(alarmDataDto.createTime))
    }

    private fun settingBasic(alarmDataDto: com.example.kesi.data.model.AlarmDataDto){
        iconIv.setImageResource(R.drawable.ic_notification_group)
        timeTv.text = pastTimeCals(LocalDateTime.parse(alarmDataDto.createTime))
    }

    private fun settingSchedule(alarmDataDto: com.example.kesi.data.model.AlarmDataDto){
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