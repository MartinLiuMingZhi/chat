package com.tencent.yolov5ncnn

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.luck.picture.lib.engine.CropFileEngine
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine

class MyCropEngine : CropFileEngine {
    companion object{
        private object InstanceHolder {
            val instance = MyCropEngine()
        }

        fun createGlideEngine(): MyCropEngine {
            return InstanceHolder.instance
        }
    }
    override fun onStartCrop(
        fragment: Fragment,
        srcUri: Uri,
        destinationUri: Uri,
        dataSource: ArrayList<String>,
        requestCode: Int
    ) {

        val uCrop = UCrop.of(srcUri, destinationUri, dataSource);
        uCrop.setImageEngine(object : UCropImageEngine {
            override fun loadImage(context: Context, url: String, imageView: ImageView) {

                Glide.with(context).load(url).into(imageView)
            }

            override fun loadImage(
                context: Context,
                url: Uri,
                maxWidth: Int,
                maxHeight: Int,
                call: UCropImageEngine.OnCallbackListener<Bitmap>
            ) {
                Glide.with(context).asBitmap().override(R.attr.minHeight, R.attr.maxHeight)
                    .load(url).into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                        ) {
                            call.onCall(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            call.onCall(null);
                        }
                    })
            }

        })
        uCrop.withOptions(buildOptions())
        uCrop.start(fragment.requireActivity(), fragment, requestCode)
    }

    private fun buildOptions(): UCrop.Options {
        val options = UCrop.Options()
        options.setShowCropFrame(true)
        options.setShowCropGrid(true)
        options.isDragCropImages(true)
        options.setToolbarTitle("裁剪")
        options.setFreeStyleCropEnabled(true)
        return options
    }
}