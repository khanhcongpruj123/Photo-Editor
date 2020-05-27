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

class ListFontAdapter(val context: Context): ListAdapter<ItemFont, ListFontAdapter.FontHolder>(FontDiff) {

    inner class FontHolder(view: View): RecyclerView.ViewHolder(view) {

        val text: TextView

        init {
            text = view.findViewById<TextView>(R.id.text)
        }

        fun bind(item: ItemFont) {
            text.typeface = item.typeface
            text.text = item.text
            text.textSize = 20f
            text.setTextColor(Color.WHITE)
            itemView.setOnClickListener {
                item.onClick()
            }
        }
    }

    object FontDiff: DiffUtil.ItemCallback<ItemFont>() {
        override fun areItemsTheSame(oldItem: ItemFont, newItem: ItemFont): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ItemFont, newItem: ItemFont): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: FontHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontHolder {
        return FontHolder(LayoutInflater.from(context).inflate(R.layout.item_font, parent, false))
    }
}