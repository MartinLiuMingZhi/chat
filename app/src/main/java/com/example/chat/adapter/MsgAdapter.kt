package com.example.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.R
import com.example.chat.data.Msg

class MsgAdapter(val msgList: List<Msg>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class LeftViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val leftMsg: TextView = view.findViewById(R.id.leftMsg)
    }
    inner class RightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rightMsg: TextView = view.findViewById(R.id.rightMsg)
    }

    inner class ImageViewHolder(view: View):RecyclerView.ViewHolder(view){
        val image:ImageView = view.findViewById(R.id.image)
    }
    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position]
        return msg.type
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType ==
        Msg.TYPE_RECEIVED) {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_left_item,
            parent, false)
        LeftViewHolder(view)
    } else if (viewType == Msg.TYPE_SENT) {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_right_item,
            parent, false)
        RightViewHolder(view)
    } else {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_img_item,parent,false)
        ImageViewHolder(view)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = msgList[position]
        when (holder) {
            is LeftViewHolder -> holder.leftMsg.text = msg.content
            is RightViewHolder -> holder.rightMsg.text = msg.content
            is ImageViewHolder -> {
                // 如果Msg对象包含了图片资源ID，则设置ImageView的资源
                msg.imageResId?.let { holder.image.setImageResource(it) }
            }
            else -> throw IllegalArgumentException()
        }
    }
    override fun getItemCount() = msgList.size
}