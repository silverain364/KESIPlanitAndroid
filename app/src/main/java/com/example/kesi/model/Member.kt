package com.example.kesi.model

data class Member(
    var uid: String, //or Token
    var authCode: String, //or None
    var nickName: String,
    var birth: Int,
    var gender: String,
)
