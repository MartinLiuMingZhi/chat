package com.example.chat.network

import android.content.ContentValues.TAG
import android.util.Log
import com.example.chat.data.ChatRequest
import com.example.chat.data.ImgRequest
import com.example.chat.data.Msg
import com.example.chat.data.TokenRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NetWork {
    private val service = RetrofitManager.create<Service>()

    private val token = TokenRetrofit.create<TokenService>()
    suspend fun getToken(tokenRequest: TokenRequest) = token.getToken(tokenRequest).await()
    suspend fun chat(accessToken: String,chatRequest: ChatRequest) = service.chat(accessToken,chatRequest).await()
    suspend fun imageToText(accessToken: String,imgRequest: ImgRequest) = service.imgToText(accessToken, imgRequest).await()
    //给网络请求方法的返回值增加拓展函数
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    Log.d(TAG, "OnResponse: ${response.code()}")
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}