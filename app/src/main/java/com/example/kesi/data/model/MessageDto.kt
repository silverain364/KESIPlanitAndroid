package com.example.kesi.data.model

data class MessageDto(
    val notificationDto : com.example.kesi.data.model.NotificationDto,
    val data : HashMap<String,String>
)
