package com.example.aether

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DreamAdapter(private var dreamList: List<Dream>):
    RecyclerView.Adapter<DreamAdapter.DreamMyViewHolder>() {
    inner  class DreamMyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val un_TV: TextView = itemView.findViewById(R.id.un_TV)
        val desc_TV: TextView = itemView.findViewById(R.id.desc_TV)
        val type_TV: TextView = itemView.findViewById(R.id.type_TV)
        val time_TV: TextView = itemView.findViewById(R.id.time_TV)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamMyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dream_recyclerview_item, parent, false)
        return DreamMyViewHolder(view)
    }
    override fun onBindViewHolder(holder: DreamMyViewHolder, position: Int) {
        val dream = dreamList[position]
        holder.un_TV.setText(dream.username)
        holder.desc_TV.setText(dream.desc)
        holder.type_TV.setText(dream.type)
        holder.time_TV.setText(dream.time)
        Glide.with(holder.itemView.context).load(dream.url).into(holder.imageView)


    }
    override fun getItemCount(): Int {
        return dreamList.size
    }
    fun setData(list: List<Dream>) {
        dreamList = list
        notifyDataSetChanged()
    }

}