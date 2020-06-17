package com.icongkhanh.photoeditor

import android.graphics.*
import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import com.icongkhanh.photoeditor.NativeImageProcessor.doBrightness
import com.icongkhanh.photoeditor.NativeImageProcessor.doSaturation
import java.io.FileNotFoundException
import kotlin.math.max
import kotlin.math.min

class PhotoEditor(val photoEditor: PhotoEditorView) : OnPressDeleteListener, OnItemFocusListener, OnItemUnfocusListener {

    private val DEFAULT_IMAGE_SIZE = 360

    private val listInsertView = mutableListOf<InsertView>()
    private var itemFocusListener: OnItemFocusListener? = null
    private var itemUnfocusListener: OnItemUnfocusListener? = null
    private var focusView: InsertView? = null
    private var originBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null

    init {
        System.loadLibrary("NativeImageProcessor")
    }

    fun setImageSource(uri: Uri) {

       try {
           val inputStream = photoEditor.context.contentResolver.openInputStream(uri)

           val bm = BitmapFactory.decodeStream(inputStream, null, BitmapFactory.Options().apply {
               inMutable = true
           })

           bm?.let {
               originBitmap = scaledBitmapTo(it, DEFAULT_IMAGE_SIZE)
           }

           inputStream?.close()

           originBitmap?.let {
               currentBitmap = Bitmap.createBitmap(it)
               photoEditor.setImageBitmap(currentBitmap!!)
           }

       } catch (ex: FileNotFoundException) {
           ex.printStackTrace()
       }
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
        if (focusView == insertView) {
            focusView = null
            itemUnfocusListener?.onItemUnfocus(insertView)
        }
    }

    override fun onItemFocus(insertView: InsertView) {
        if (focusView != insertView) focusView?.unFocus()
        focusView = insertView
        itemFocusListener?.onItemFocus(insertView)
    }

    override fun onItemUnfocus(insertView: InsertView) {
        focusView = null
        itemUnfocusListener?.onItemUnfocus(insertView)
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

    fun setBrighness(percent: Float) {
       originBitmap?.let {
           val value = 255 * percent
           val width: Int = it.getWidth()
           val height: Int = it.getHeight()
           val pixels = IntArray(width * height)
           currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

           it.getPixels(pixels, 0, width, 0, 0, width, height)
           doBrightness(pixels, value.toInt(), width, height)
           currentBitmap?.let {
               it.setPixels(pixels, 0, width, 0, 0, width, height)
               photoEditor.setImageBitmap(it)
           }
       }
    }

    fun setOnItemFocusListener(l: OnItemFocusListener) {
        this.itemFocusListener = l
    }

    fun setOnItemUnfocusListener(l: OnItemUnfocusListener) {
        this.itemUnfocusListener = l
    }

    fun setSaturation(percent: Float) {
        originBitmap?.let {
            val width: Int = it.getWidth()
            val height: Int = it.getHeight()
            val pixels = IntArray(width * height)
            val value = (percent * 100) / 50

            currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            it.getPixels(pixels, 0, width, 0, 0, width, height)
            doSaturation(pixels, value, width, height)
            currentBitmap?.let {
                it.setPixels(pixels, 0, width, 0, 0, width, height)
                photoEditor.setImageBitmap(it)
            }
        }
    }

    private fun scaledBitmapTo(bm: Bitmap, size: Int) : Bitmap {
        val scaled = max(size.toFloat() / bm.width, size.toFloat() / bm.height)
        return Bitmap.createScaledBitmap(bm, (bm.width * scaled).toInt(), (bm.height * scaled).toInt(), true)
    }
}
