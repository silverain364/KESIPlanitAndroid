package com.example.kesi.model

data class GroupMemberDto(
    val email:String,
    val nickname:String,
    val imgPath:String,
    val gender:String

){
    fun toFriendsDto(): FriendsDto{
        return FriendsDto(
            email = email,
            nickname = nickname,
            imgPath =  imgPath,
            gender = gender,
            alias = nickname
        );
    }

}
