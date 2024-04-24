package com.example.chat.network

import com.example.chat.data.ChatRequest
import com.example.chat.data.ChatResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface Service {

    @POST("v1/wenxinworkshop/chat/completions_pro")
    fun chat(@Query("access_token") accessToken: String, @Body chatRequest: ChatRequest):Call<ChatResponse>

}