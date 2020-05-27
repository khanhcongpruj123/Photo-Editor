package com.icongkhanh.photoeditor

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ListColorAdapter(val context: Context): ListAdapter<ItemColor, ListColorAdapter.ColorHolder>(FontDiff) {

    inner class ColorHolder(view: View): RecyclerView.ViewHolder(view) {


        fun bind(item: ItemColor) {
            itemView.setBackgroundColor(item.color)
            itemView.setOnClickListener {
                item.onClick()
            }
        }
    }

    object FontDiff: DiffUtil.ItemCallback<ItemColor>() {
        override fun areItemsTheSame(oldItem: ItemColor, newItem: ItemColor): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ItemColor, newItem: ItemColor): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: ColorHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
        return ColorHolder(LayoutInflater.from(context).inflate(R.layout.item_font, parent, false))
    }
}