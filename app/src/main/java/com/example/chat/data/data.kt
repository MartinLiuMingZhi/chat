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

data class ImgRequest(
    val image:String,
    val prompt:String,
   // val output_CHN:Boolean
)


data class ImgResponse(
    val id:String,
    val `object`: String,
    val created:Int,
    val result: String,
    val is_safe:Int,
    val usage:Usage
)

data class Usage(
    val prompt_tokens:Int,
    val completion_tokens:Int,
    val total_tokens:Int
)

data class TransRequest(
    val from:String,
    val to:String,
    val q:String
)

data class TransResponse(
    val result:Result,
    val log_id:String,
)
data class Result(
    val from: String,
    val trans_result:List<Translation>,
    val to:String
)
data class Translation(
    val dst: String,
    val src:String
)
