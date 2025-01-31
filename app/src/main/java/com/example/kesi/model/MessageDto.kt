package com.example.kesi.model

data class MessageDto(
    val notificationDto : NotificationDto,
    val data : HashMap<String,String>
)
