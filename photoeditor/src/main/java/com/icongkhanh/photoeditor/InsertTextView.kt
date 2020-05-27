package com.icongkhanh.photoeditor

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM
import androidx.annotation.ColorInt
import androidx.core.view.setMargins

class InsertTextView : InsertView {

    private var tv: AutoTextView

    init {
        tv = AutoTextView(context)
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        val layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(20)
        addView(tv, layoutParams)
    }

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {

    }

    override fun getType(): InsertViewType {
        return InsertViewType.Text
    }

    fun setText(text: String) {
        tv.text = text
    }

    fun setColor(@ColorInt color: Int) {
        tv.setTextColor(color)
    }

    fun setFont(typeface: Typeface?) {
        tv.typeface = typeface
    }

    fun setFontSize(size: Float) {
        tv.textSize = size
    }

    fun setFontSize(size: Float, unit: Int) {
        tv.setTextSize(unit, size)
    }

    override fun onFocus() {
        super.onFocus()

        tv.setBackgroundResource(R.drawable.border)
    }

    override fun onUnfocus() {
        super.onUnfocus()

        tv.background = null
    }

    fun getText(): CharSequence? {
        return tv.text
    }

    fun getTypeface(): Typeface? {
        return tv.typeface
    }

    fun getTextColor(): Int {
        return tv.currentTextColor
    }
}