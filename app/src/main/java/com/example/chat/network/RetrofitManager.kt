package com.example.chat.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {

    private const val BASE_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom"


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(createOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun createOkHttpClient() : OkHttpClient {
        val okBuilder = OkHttpClient.Builder().apply {
            addInterceptor {
                val original = it.request()
                val request = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("connection", "Keep-Alive")
                    .method(original.method(),original.body())
                    .build()
                it.proceed(request)
            }
        }
//            .connectTimeout(5L, TimeUnit.SECONDS)
        return  okBuilder.build()
    }

    fun <T> create(serviceClass: Class<T>) : T = retrofit.create(serviceClass)
    inline fun <reified T> create() : T = create(T::class.java)
}