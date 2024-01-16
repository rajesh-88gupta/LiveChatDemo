package com.agro.livechatdemo.data

data class UserData(
    var userId: String? ="",
    var name: String? ="",
    var mobileNum: String? ="",
    var imageUrl: String? ="",
){
    fun  toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "mobileNum" to mobileNum,
        "imageUrl" to imageUrl
    )
}

data class ChatUser(
    val userId: String?="",
    val name: String?="",
    val imageUrl: String?="",
    val number: String?=""
)

data class ChatData(
    val chatId:String?="",
    val user1:ChatUser= ChatUser(),
    val user2:ChatUser= ChatUser()
)