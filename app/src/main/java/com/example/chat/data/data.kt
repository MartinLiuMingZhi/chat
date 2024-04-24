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
    val access_token:String
)

data class ChatResponse(
    val msg:String
)