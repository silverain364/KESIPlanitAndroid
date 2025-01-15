package com.example.kesi.model

data class GroupDto(
    val gid:Long,
    val groupName:String,
    val maker:GroupMemberDto,
    val calendarId:Long,
    val members:List<GroupMemberDto>
)
