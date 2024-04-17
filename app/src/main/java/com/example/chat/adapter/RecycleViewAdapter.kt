package com.example.chat.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.R

class RecycleViewAdapter(private val data:Array<String>): RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>() {

    class ViewHolder(view:View):RecyclerView.ViewHolder(view) {
        private val textView : TextView

        init {
           textView = view.findViewById(R.id.textMessage)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecycleViewAdapter.ViewHolder {
        TODO("Not yet implemented")
    }



    override fun onBindViewHolder(holder: RecycleViewAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {

        return data.size
    }


}