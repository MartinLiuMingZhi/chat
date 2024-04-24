package com.example.chat.network

import com.example.chat.data.TokenRequest
import com.example.chat.data.TokenResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenService {

    @POST
    fun getToken(@Body tokenRequest: TokenRequest):Call<TokenResponse>
}