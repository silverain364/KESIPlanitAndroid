package com.example.kesi.data.model

data class AlarmDataDto(
    val id: Long,
    val title: String,
    val content: String,
    val alarmType: com.example.kesi.data.model.AlarmType,
    val createTime: String,
    val data: HashMap<String, String>
)