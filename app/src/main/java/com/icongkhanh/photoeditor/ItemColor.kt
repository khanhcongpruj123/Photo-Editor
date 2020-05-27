package com.icongkhanh.photoeditor

import android.graphics.Color

data class ItemColor(
    val name: String,
    val color: Int,
    val onClick: () -> Unit
) {
}