package com.example.kesi.model

data class AlarmDataDto(
    val id: Long,
    val title: String,
    val content: String,
    val alarmType: AlarmType,
    val createTime: String,
    val data: HashMap<String, String>
)