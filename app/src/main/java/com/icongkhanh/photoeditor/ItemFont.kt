package com.icongkhanh.photoeditor

import android.graphics.Typeface

data class ItemFont(
    val name: String,
    val text: String,
    val typeface: Typeface,
    val onClick: () -> Unit
)