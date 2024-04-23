package com.example.chat.data

//content: 消息内容
//type: 消息类型 TYPE_RECEIVED表示是一条收到的消息 TYPE_SENT表示是一条发送出去的信息
class Msg(val content: String, val type: Int) {
    companion object {
        const val TYPE_RECEIVED = 0
        const val TYPE_SENT = 1
    }
}