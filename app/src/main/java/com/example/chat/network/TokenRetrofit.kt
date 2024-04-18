package com.example.chat.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object TokenRetrofit {

    private const val URL = "https://aip.baidubce.com/oauth/2.0/token"

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .build()

    private fun createOkHttpClient(): OkHttpClient{
        val okHttpBuilder = OkHttpClient.Builder().apply {
            addInterceptor {
                val original = it.request()
                val request = original.newBuilder()
                    .header("Content-Type","application/json")
                    .method(original.method(),original.body())
                    .build()
                it.proceed(request)
            }
        }
        return okHttpBuilder.build()
    }

    fun <T> create(serviceClass: Class<T>) : T = TokenRetrofit.retrofit.create(serviceClass)
    inline fun <reified T> create() : T = create(T::class.java)
}

