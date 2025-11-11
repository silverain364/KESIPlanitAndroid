package com.example.kesi.data.model

data class GroupMemberDto(
    val email:String,
    val nickname:String,
    val imgPath:String,
    val gender:String

){
    fun toFriendsDto(): com.example.kesi.data.model.FriendsDto {
        return com.example.kesi.data.model.FriendsDto(
            email = email,
            nickname = nickname,
            imgPath = imgPath,
            gender = gender,
            alias = nickname
        );
    }

}
