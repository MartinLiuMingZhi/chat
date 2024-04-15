package com.example.chat.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {

    private const val URL = "https://aip.baidubce.com/oauth/2.0/token"

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <T> create(serviceClass: Class<T>) : T = retrofit.create(serviceClass)
    inline fun <reified T> create() : T = create(T::class.java)
}