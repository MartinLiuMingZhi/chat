package com.example.chat

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.adapter.MsgAdapter
import com.example.chat.data.ChatRequest
import com.example.chat.data.ImgRequest
import com.example.chat.data.Message
import com.example.chat.data.Msg
import com.example.chat.databinding.ActivityMainBinding
import com.example.chat.network.NetWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private  val REQUEST_IMAGE = 1
    // 声明 TextToSpeech 对象
    private lateinit var textToSpeech: TextToSpeech

    private lateinit var binding: ActivityMainBinding
    private lateinit var loadingProgressBar: ProgressBar
    private val msgList = ArrayList<Msg>()
    private var adapter: MsgAdapter? = null
    private val access_token = "24.69224740f53277f7a0cf0aa68f86adf9.2592000.1716207595.282335-58308844"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        loadingProgressBar = binding.progressBar
//        loadingProgressBar.

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


//        initMsg()

        // 初始化 TextToSpeech 对象
        textToSpeech = TextToSpeech(this, this)

        val layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.layoutManager = layoutManager
        adapter = MsgAdapter(msgList)
        binding.chatRecyclerView.adapter = adapter

        binding.sayingBtn.setOnClickListener {
            checkAndRequestPermission()
            startVoiceInput()
        }

        binding.imgBtn.setOnClickListener {
            val intent = Intent(this,ImgSelectorActivity::class.java)
            startActivityForResult(intent,REQUEST_IMAGE)


        }


    }



    //    private fun initMsg() {
//        val msg1 = Msg("hello guy.", Msg.TYPE_RECEIVED)
//        msgList.add(msg1)
//        val msg2 = Msg("hello.who is that?", Msg.TYPE_SENT)
//        msgList.add(msg2)
//        val msg3 = Msg("This is Tom.Nice to meet you.", Msg.TYPE_RECEIVED)
//        msgList.add(msg3)
//    }
    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_PERMISSION_CODE)
        }

    }


    private fun startVoiceInput() {
        // 在这里启动语音输入界面
        // 可以使用 Intent 启动系统自带的语音输入界面，也可以使用自定义的界面
        // 这里只是一个示例，具体实现可以根据需求进行修改
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Speech input not supported on your device", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = result?.get(0)

            // 在这里处理语音输入结果，例如将其显示在界面上
            // 这里只是一个示例，具体实现可以根据需求进行修改
            Toast.makeText(this, "You said: $spokenText", Toast.LENGTH_SHORT).show()
            if (spokenText.toString().isNotEmpty()){
                sendMsg(spokenText.toString())
                val msg = Msg(spokenText.toString(),Msg.TYPE_SENT)
                msgList.add(msg)
                adapter?.notifyItemInserted(msgList.size - 1)
                binding.chatRecyclerView.scrollToPosition(msgList.size - 1)
            }

        }

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            val imageUri = data?.getParcelableExtra<Uri>("image_path") // 从 Intent 中获取图像的 Uri
            Log.d("image_path", imageUri.toString())

            // 将 URI 转换为 Base64 编码的字符串
            val imageBase64 = encodeImageToBase64(imageUri)
            Log.d("Base64编码",imageBase64)
            if (imageBase64.isNotEmpty() && imageBase64 != "error") {
                // 继续进行其他处理，例如将图像发送到服务器
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val response = NetWork.imageToText(access_token,
                            ImgRequest(imageBase64, "introduce this picture")
                        )
                        Log.d("response", response.toString())
                        if (response.result != null) {
                            val msg = Msg(response.result, Msg.TYPE_RECEIVED)
                            msgList.add(msg)
                            adapter?.notifyItemInserted(msgList.size - 1)
                            binding.chatRecyclerView.scrollToPosition(msgList.size - 1)
                            speakOut(response.result)
                        } else {
                            Log.e("TAG", "Response result is null")
                            // 处理结果为空的情况，例如向用户显示错误消息
                        }
                    } catch (e: Exception) {
                        Log.e("TAG", "Exception: ${e.message}", e)
                    }
                }
            } else {
                // 如果处理失败，显示错误消息
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show()
            }
        }



    }

    private fun sendMsg(spokenText: String) {
        val message = Message("user", spokenText)
        val list = ArrayList<Message>()
        list.add(message)
        val request = ChatRequest(list)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = NetWork.chat(access_token, request)
                withContext(Dispatchers.Main) {
                    if (response.result != null) {
                        val msg = Msg(response.result, Msg.TYPE_RECEIVED)
                        msgList.add(msg)
                        adapter?.notifyItemInserted(msgList.size - 1)
                        binding.chatRecyclerView.scrollToPosition(msgList.size - 1)
                        speakOut(response.result)
                    } else {
                        Log.e("TAG", "Response result is null")
                        // 处理结果为空的情况，例如向用户显示错误消息
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "Exception: ${e.message}", e)
            }
        }
    }

    // 将文字转换为语音并播放
    private fun speakOut(text: String) {
        // 检查 TextToSpeech 对象是否已初始化
        if (textToSpeech != null) {
            // 检查 TextToSpeech 引擎是否已成功初始化
            if (textToSpeech!!.isLanguageAvailable(Locale.CHINESE) == TextToSpeech.LANG_AVAILABLE) {
                // 使用 TextToSpeech 播放文本
                textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            } else {
                Log.e("TTS", "Language not supported")
            }
        } else {
            Log.e("TTS", "TextToSpeech is not initialized")
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, perform the required operation
                    startVoiceInput()
                } else {
                    // Permission denied, handle the scenario accordingly
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            // Add more cases if you have multiple permissions to handle
        }
    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 100
        private const val RECORD_AUDIO_PERMISSION_CODE = 101
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 设置语言为中文（你可以根据需要设置其他语言）
            val result = textToSpeech.setLanguage(Locale.CHINESE)

            // 如果语音合成引擎不支持所选的语言
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }


    override fun onDestroy() {
        // 释放 TextToSpeech 对象
        if (textToSpeech != null) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }


    private fun encodeImageToBase64(uri: Uri?): String {
        try {
            // 通过 URI 获取图像的输入流
            val inputStream = contentResolver.openInputStream(uri!!)
            // 将输入流解码为 Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // 压缩图像
            val compressedBitmap = compressBitmap(bitmap)
            // 将压缩后的图像转换为 Base64 编码的字符串
            val byteArrayOutputStream = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            return "error"
        }
    }





    // 压缩图片以确保大小在要求范围内
    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val maxSize = 4 * 1024 * 1024 // 4MB
        val minWidth = 15
        val maxWidth = 4096

        // 计算图片的宽度和高度
        val width = bitmap.width
        val height = bitmap.height

        // 如果图片大小在要求范围内，则返回原始图片
        if (bitmap.byteCount <= maxSize && width >= minWidth && height >= minWidth && width <= maxWidth && height <= maxWidth) {
            return bitmap
        }

        // 计算压缩比例
        val ratio = Math.sqrt(bitmap.byteCount.toDouble() / maxSize.toDouble())

        // 新的宽度和高度
        val newWidth = (width / ratio).toInt()
        val newHeight = (height / ratio).toInt()

        // 返回压缩后的图片
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}