package com.icongkhanh.photoeditor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlin.math.max
import kotlin.math.min

@RemoteViews.RemoteView
class PhotoEditorView(context: Context, attributeSet: AttributeSet) : RelativeLayout(context, attributeSet) {

    lateinit var imageView: ImageView
    private var scaleGestureDetector: ScaleGestureDetector
    private var zoomValue = 1f

    init {
        setupImageView()


        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.OnScaleGestureListener {
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {

            }

            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                zoomValue *= detector?.scaleFactor?: 1f

                zoomValue = max(0.1f, min(zoomValue, 10f))

                imageView.scaleY = zoomValue
                imageView.scaleX = zoomValue

                return true
            }

        })

        setOnTouchListener { v, event ->
            performClick()
            for (i in 0 until childCount) {
                val child = this.getChildAt(i)
                if (child is InsertView) child.unFocus()
            }

            scaleGestureDetector.onTouchEvent(event)

            true
        }
    }

    private fun setupImageView() {
        imageView = ImageView(context)
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        val layoutParams = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_IN_PARENT)

        this.addView(imageView, layoutParams)
    }

    fun addInsertView(view: InsertView) {
        val layoutParams = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        this.addView(view, layoutParams)

        view.translationX = view.translationX + (this.width - view.width) / 2f
        view.translationY = view.translationY + (this.height - view.height) / 2f
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

    fun setImageBitmap(bm: Bitmap) {
//        Glide.with(this)
//            .load(bm)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .into(imageView)

        this.post {
            imageView.setImageBitmap(bm)
//            Log.d("AppLog", "PhotoEditorView: top: ${this.top} - left: ${this.left} - bottom: ${this.bottom} - right: ${this.right}")
//            Log.d("AppLog", "ImageView: top: ${imageView.top} - left: ${imageView.left} - bottom: ${imageView.bottom} - right: ${imageView.right}")
        }
    }
}