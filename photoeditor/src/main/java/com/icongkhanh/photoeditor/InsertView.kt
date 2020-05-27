package com.icongkhanh.photoeditor

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout

abstract class InsertView : FrameLayout {

    private var mOnItemFocusListener: OnItemFocusListener? = null
    private var mOnItemUnfocusListener: OnItemUnfocusListener? = null
    private var mOnPressDeleteListener: OnPressDeleteListener? = null
    private var isFocus = true
    private val gestureDetector: GestureDetector
    private lateinit var btnDelete: ImageButton
    private lateinit var btnScale: ImageButton
    private lateinit var btnEdit: ImageButton
    private var onRequestEditListener: OnRequestEditListener? = null

    init {

        setupDeleteButton()
        setupScaleButton()
        setupEditButton()

        gestureDetector = GestureDetector(
            onMove = {deltaX, deltaY ->
                adjustTranslation(this, deltaX, deltaY)
            },
            onRotate = {rotation ->
                val rotation = adjustAngle(this.rotation + rotation)
                this.rotation = rotation
            }
        )

        setOnTouchListener { v, event ->

            focus()
            gestureDetector.onTouch(v, event)
            true
        }
    }

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {

    }

    constructor(context: Context): super(context) {

    }

    fun setOnItemFocusListener(l: OnItemFocusListener) {
        mOnItemFocusListener = l
    }

    fun setOnUnfocusListener(l: OnItemUnfocusListener) {
        mOnItemUnfocusListener = l
    }

    fun setOnCloseListener(l: OnPressDeleteListener) {
        Log.d("AppLog", "Setup listener")
        mOnPressDeleteListener = l
    }

    fun focus() {
        isFocus = true
        onFocus()
        mOnItemFocusListener?.onItemFocus(this)
    }

    fun unFocus() {
        isFocus = false
        onUnfocus()
        mOnItemUnfocusListener?.onItemUnfocus(this)
    }

    private fun setupScaleButton() {
        btnScale = ImageButton(context)
        btnScale.scaleType = ImageView.ScaleType.FIT_CENTER

        val scaleDetector = ScaleDetector {deltaX, deltaY ->
            val deltaVector = floatArrayOf(deltaX, deltaY)
            this.matrix.mapVectors(deltaVector)

            val width = this.width + deltaX.toInt()
            val height = this.height + deltaY.toInt()
            if (width < 40 || height < 40) return@ScaleDetector
            val layout = FrameLayout.LayoutParams(width, height)
            layout.topMargin = (this.layoutParams as FrameLayout.LayoutParams).topMargin
            layout.marginStart = (this.layoutParams as FrameLayout.LayoutParams).marginStart
            this.layoutParams = layout
        }
        btnScale.setOnTouchListener(scaleDetector)

        val outValue = TypedValue()
        context.theme
            .resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        btnScale.setBackgroundResource(outValue.resourceId)

        btnScale.setImageResource(R.drawable.ic_resize)
        btnScale.setPadding(5, 5, 5, 5)

        btnScale.elevation = 10F

        val layoutParams = FrameLayout.LayoutParams(60, 60)
        layoutParams.gravity = Gravity.BOTTOM or Gravity.END

        this.addView(btnScale, layoutParams)
    }

    private fun setupEditButton() {
        btnEdit = ImageButton(context)
        btnEdit.scaleType = ImageView.ScaleType.FIT_CENTER

        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        btnEdit.setBackgroundResource(outValue.resourceId)

        btnEdit.setImageResource(R.drawable.ic_edit_black_24dp)

        btnEdit.elevation = 10F

        btnEdit.setOnClickListener {
            onRequestEditListener?.onEdit()
        }

        val layoutParams = FrameLayout.LayoutParams(60, 60)
        layoutParams.gravity = Gravity.TOP or Gravity.END

        this.addView(btnEdit, layoutParams)
    }

    fun setOnRequestEdit(l: OnRequestEditListener) {
        onRequestEditListener = l
    }


    private fun setupDeleteButton() {
        btnDelete = ImageButton(context)
        btnDelete.scaleType = ImageView.ScaleType.FIT_CENTER
        btnDelete.setOnClickListener {
            mOnPressDeleteListener?.onPressDelete(this)
        }

        btnDelete.elevation = 10F

        val outValue = TypedValue()
        context.theme
            .resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        btnDelete.setBackgroundResource(outValue.resourceId)

        btnDelete.setImageResource(R.drawable.ic_cancel_black_18dp)
        btnDelete.setPadding(5, 5, 5, 5)

        val layoutParams = FrameLayout.LayoutParams(60, 60)
        layoutParams.gravity = Gravity.TOP and Gravity.START

        this.addView(btnDelete, layoutParams)
    }

    fun getDeleteView(): ImageButton {
        return btnDelete
    }

    fun getScaleView(): ImageButton {
        return btnDelete
    }

    open fun onFocus() {
        this.bringToFront()
        btnScale.visibility = View.VISIBLE
        btnDelete.visibility = View.VISIBLE
        btnEdit.visibility = View.VISIBLE
    }

    open fun onUnfocus() {
        btnScale.visibility = View.INVISIBLE
        btnDelete.visibility = View.INVISIBLE
        btnEdit.visibility = View.INVISIBLE
    }

    abstract fun getType(): InsertViewType
}

sealed class InsertViewType {
    object Image: InsertViewType()
    object Text: InsertViewType()
}