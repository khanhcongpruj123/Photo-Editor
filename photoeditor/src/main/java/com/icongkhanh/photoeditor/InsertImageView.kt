package com.icongkhanh.photoeditor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.core.view.setMargins
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

@RemoteViews.RemoteView
class InsertImageView: InsertView {

    private lateinit var imageView: ImageView

    init {
        setupImageView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {

    }

    constructor(context: Context) : super(context) {

    }

    private fun setupImageView() {
        imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.adjustViewBounds = true
        imageView.elevation = 0F

        val layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(20)
        this.addView(imageView, layoutParams)
    }

    fun setImageBitmap(bm: Bitmap) {

        Glide.with(context)
            .load(bm)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    fun setImageResource(@DrawableRes resId: Int) {
        Glide.with(context)
            .load(resId)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    override fun onFocus() {
        super.onFocus()
        imageView.setBackgroundResource(R.drawable.border)
    }

    override fun onUnfocus() {
        super.onUnfocus()
        imageView.background = null
    }

    override fun getType(): InsertViewType {
        return InsertViewType.Image
    }

    fun setImageURI(uri: Uri) {
        Glide.with(context)
            .load(uri)
            .override(720, 720)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
}