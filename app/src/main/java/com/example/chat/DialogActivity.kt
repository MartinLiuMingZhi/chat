package com.example.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.adapter.MsgAdapter
import com.example.chat.data.Msg
import com.example.chat.databinding.ActivityDialogBinding
import okhttp3.*
import java.io.IOException
import com.google.gson.Gson




class DialogActivity : AppCompatActivity() , View.OnClickListener {

    private lateinit var binding: ActivityDialogBinding

    private val msgList = ArrayList<Msg>()

    private var adapter: MsgAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initMsg()
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        adapter = MsgAdapter(msgList)
        binding.recyclerView.adapter = adapter
        binding.send.setOnClickListener(this)

    }

    private fun initMsg() {
        val msg1 = Msg("hello guy.", Msg.TYPE_RECEIVED)
        msgList.add(msg1)
        val msg2 = Msg("hello.who is that?", Msg.TYPE_SENT)
        msgList.add(msg2)
        val msg3 = Msg("This is Tom.Nice to meet you.", Msg.TYPE_RECEIVED)
        msgList.add(msg3)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.send -> {
                val content = binding.inputText.text.toString()
                if(content.isNotEmpty()) {
                    sendMsg(content)
                    val msg = Msg(content, Msg.TYPE_SENT)
                    msgList.add(msg)
                    adapter?.notifyItemInserted(msgList.size - 1)
                    binding.recyclerView.scrollToPosition(msgList.size - 1)
                    binding.inputText.setText("")

                }
            }
        }
    }

    private fun sendMsg(content: String) {
        val url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro?access_token=24.69224740f53277f7a0cf0aa68f86adf9.2592000.1716207595.282335-58308844"
        val requestBody = """
        {
            "messages": [
                {
                    "role": "user",
                    "content": "$content"
                }
            ]
        }
    """.trimIndent()

        // 创建 OkHttp 客户端
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
            .build()

        // 发送请求并处理响应
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()?.string()
                runOnUiThread {
                    // 在主线程更新界面
                    responseData?.let {
                        val msg = Msg(it, Msg.TYPE_RECEIVED)
                        msgList.add(msg)
                        adapter?.notifyItemInserted(msgList.size - 1)
                        binding.recyclerView.scrollToPosition(msgList.size - 1)
                    }
                }
            }

        })
    }

//    private fun getAccessToken(): String {
//        val clientId = "XUh65QnZ6suKc5l2nHc5ebQG"
//        val clientSecret = "IGZOvuSJD1YrWMOOSrOT3Fq3LytDQNkK"
//        val url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=$clientId&client_secret=$clientSecret"
//        val request = Request.Builder()
//            .url(url)
//            .post(RequestBody.create(MediaType.parse("application/json"), ""))
//            .build()
//
//        val client = OkHttpClient()
//        val response = client.newCall(request).execute()
//
//        if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//        val responseData = response.body()?.string()
//        val gson = Gson()
//        val accessTokenMap = gson.fromJson(responseData, Map::class.java)
//
//        // 检查 accessTokenMap 是否为 null
//        if (accessTokenMap != null) {
//            // 尝试获取 access_token
//            val accessToken = accessTokenMap["access_token"]
//            if (accessToken != null) {
//                return accessToken.toString()
//            }
//        }
//
//        // 如果无法获取 access_token，则抛出异常或返回空字符串
//        throw NullPointerException("Failed to get access_token from response data")
//    }
}