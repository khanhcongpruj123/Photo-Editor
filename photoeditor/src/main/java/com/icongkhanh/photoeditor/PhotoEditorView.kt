package com.icongkhanh.photoeditor

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

@RemoteViews.RemoteView
class PhotoEditorView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {

    lateinit var imageView: ImageView

    init {
        setupImageView()
        setOnTouchListener { v, event ->
            for (i in 0 until childCount) {
                val child = this.getChildAt(i)
                if (child is InsertView) child.unFocus()
            }
            true
        }
    }

    private fun setupImageView() {
        imageView = ImageView(context)
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

        val layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.addView(imageView)
    }

    fun addInsertView(view: InsertView) {
        val layoutParams = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.marginStart = this.width / 2 - view.width / 2
        layoutParams.topMargin = this.height / 2 - view.height / 2
        this.addView(view, layoutParams)
        view.focus()
    }

    fun setImageResource(@DrawableRes resId: Int) {
        Glide.with(context)
            .load(resId)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    fun setImageURI(uri: Uri) {
        Glide.with(context)
            .load(uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
}