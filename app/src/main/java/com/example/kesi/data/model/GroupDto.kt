package com.example.kesi.data.model

data class GroupDto(
    val gid:Long,
    val groupName:String,
    val maker: com.example.kesi.data.model.GroupMemberDto,
    val calendarId:Long,
    val members:List<com.example.kesi.data.model.GroupMemberDto>
)
