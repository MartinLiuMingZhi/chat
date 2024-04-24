package com.example.chat.data

data class TokenRequest(
    val grant_type:String,
    val client_id:String,
    val client_secret:String
)

data class TokenResponse(
    val access_token:String
)

data class ChatRequest(
    val messages: List<Message> // 添加消息列表字段
)

data class Message(
    val role: String,
    val content: String
)


data class ChatResponse(
    val id:String,
    val `object`:String,
    val created:Int,
    val result:String
)