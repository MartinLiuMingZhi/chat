package com.example.chat

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.chat.databinding.ActivityImgSelectorBinding
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.tencent.yolov5ncnn.MyCropEngine
import java.io.File
import java.util.ArrayList

class ImgSelectorActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private lateinit var binding: ActivityImgSelectorBinding
    private var imgFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImgSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.send.setOnClickListener {
            if (imgFile != null) sendImage(imgFile!!)
            else {
                Toast.makeText(this@ImgSelectorActivity,"图片不存在",Toast.LENGTH_SHORT).show()
            }
        }
        binding.takePhoto.setOnClickListener {
            PictureSelector.create(this)
                .openCamera(SelectMimeType.ofImage())
                .setCropEngine(MyCropEngine.createGlideEngine())
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>) {
                        Glide.with(this@ImgSelectorActivity).load(File(result[0].realPath)).into(binding.img)
//                            imageUrl(File(result[0].realPath))      //图片转url
                        imgFile = File(result[0].realPath)
                    }

                    override fun onCancel() {}
                })
        }
        binding.importImage.setOnClickListener {
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setCropEngine(MyCropEngine.createGlideEngine())
                .setMaxSelectNum(1)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>) {
                        val file = File(result[0].realPath)
                        if (file.exists()) {
                            imgFile = file
                            Log.e(TAG, "onResult: ${file.name}")
                            Glide.with(this@ImgSelectorActivity).load(file).into(binding.img)
                        } else {
                            Toast.makeText(this@ImgSelectorActivity,"图片不存在",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancel() {}
                })
        }

    }
    fun sendImage(file: File)  {
        // 创建意图，将照片路径作为额外数据传递给 MainActivity
        val intent = Intent(this@ImgSelectorActivity, MainActivity::class.java)
        val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "com.example.cameraalbumtest.fileprovider", file)
        } else {
            Uri.fromFile(file.absoluteFile)
        }
        intent.putExtra("image_path", path)
        setResult(RESULT_OK, intent)
        finish()
    }


}