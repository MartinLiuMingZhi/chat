package com.example.chat.network

object NetWork {
    private val service = RetrofitManager.create<Service>()

    private val token = TokenRetrofit.create<>()
}