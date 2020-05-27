package com.icongkhanh.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.core.widget.TextViewCompat

class AutoTextView : androidx.appcompat.widget.AppCompatTextView {

    init {
        setup()
    }

    constructor(context: Context): super(context) {

    }

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {

    }

    @SuppressLint("RestrictedApi")
    private fun setup() {
        setTextSize(TypedValue.TYPE_DIMENSION, 10f)
        setAutoSizeTextTypeUniformWithConfiguration(2, 100, 2, TypedValue.TYPE_DIMENSION)
    }
}