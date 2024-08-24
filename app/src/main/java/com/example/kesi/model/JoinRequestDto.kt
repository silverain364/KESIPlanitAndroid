package com.example.kesi.model

data class JoinRequestDto(
    var uid: String,
    var authCode: String?,
    var nickName: String,
    var birth: String,
    var gender: String
)
