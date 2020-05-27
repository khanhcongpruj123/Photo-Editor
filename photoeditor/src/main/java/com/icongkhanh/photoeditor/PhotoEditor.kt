package com.icongkhanh.photoeditor

import android.graphics.*
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import androidx.annotation.DrawableRes

class PhotoEditor(val photoEditor: PhotoEditorView) : OnPressDeleteListener, OnItemFocusListener, OnItemUnfocusListener{

    private val listInsertView = mutableListOf<InsertView>()
    private var focusView : InsertView? = null


    fun setImageSource(uri: Uri) {
        photoEditor.setImageURI(uri)
    }

    fun addImageView(bm: Bitmap) {
        val insertImageView = InsertImageView(photoEditor.context)
        insertImageView.setImageBitmap(bm)
        addImageView(insertImageView)
    }

    fun addImageView(@DrawableRes resId: Int) {
        val insertImageView = InsertImageView(photoEditor.context)
        insertImageView.setImageResource(resId)
        addImageView(insertImageView)
    }

    fun addImageView(insertImageView: InsertImageView) {
        insertImageView.setOnItemFocusListener(this)
        insertImageView.setOnUnfocusListener(this)
        insertImageView.setOnCloseListener(this)

        photoEditor.addInsertView(insertImageView)
        listInsertView.add(insertImageView)
    }

    fun addTextView(insertTextView: InsertTextView) {
        insertTextView.setOnItemFocusListener(this)
        insertTextView.setOnUnfocusListener(this)
        insertTextView.setOnCloseListener(this)

        photoEditor.addInsertView(insertTextView)
        listInsertView.add(insertTextView)
    }

    fun addTextView(text: String, listener: OnRequestEditListener) {
        val insertTextView = InsertTextView(photoEditor.context)
        insertTextView.setOnRequestEdit(listener)
        insertTextView.setText(text)

        addTextView(insertTextView)
    }

    fun addTextView(text: String, typeface: Typeface?, color: Int, listener: OnRequestEditListener) {
        val insertTextView = InsertTextView(photoEditor.context)
        insertTextView.setOnRequestEdit(listener)
        insertTextView.setText(text)
        insertTextView.setFont(typeface)
        insertTextView.setColor(color)

        addTextView(insertTextView)
    }

    override fun onPressDelete(insertView: InsertView) {
        photoEditor.removeView(insertView)
        listInsertView.remove(insertView)
        if (focusView == insertView) focusView = null
    }

    override fun onItemFocus(insertView: InsertView) {
        if (focusView != insertView) focusView?.unFocus()
        focusView = insertView
    }

    override fun onItemUnfocus(insertView: InsertView) {
        focusView = null
    }

    fun addImageView(uri: Uri) {
        val insertImageView = InsertImageView(photoEditor.context)
        insertImageView.setImageURI(uri)

        addImageView(insertImageView)
    }

    fun getFocusView(): InsertView? {
        return focusView
    }

    fun saveBitmap(): Bitmap? {
        val bm = Bitmap.createBitmap(photoEditor.width, photoEditor.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        listInsertView.forEach { it.unFocus() }
        photoEditor.draw(canvas)
        removeTransparency(bm)
        return bm
    }
}