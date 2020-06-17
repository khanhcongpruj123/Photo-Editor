package com.icongkhanh.photoeditor

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity(), OnResultListener, OnRequestEditListener,
    OnItemFocusListener, OnItemUnfocusListener {

    lateinit var alphaBar: SeekBar

    private val handlerThread by lazy { HandlerThread("photoedit").apply {
        start()
    } }

    private val handler by lazy { Handler(handlerThread.looper) }

    private lateinit var photoEditor: PhotoEditor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 10)
        }

        val photoEditorView = findViewById<PhotoEditorView>(R.id.photo_editor)
        photoEditor = PhotoEditor(photoEditorView)
        photoEditor.setOnItemFocusListener(this)
        photoEditor.setOnItemUnfocusListener(this)

        val btnAddImage = findViewById<Button>(R.id.btn_add_image)
        val btnAddText = findViewById<Button>(R.id.btn_add_text)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val btnChangeSource = findViewById<Button>(R.id.btn_add_source)
        val seekBar = findViewById<SeekBar>(R.id.seek_bar)

        alphaBar = findViewById<SeekBar>(R.id.alpha_bar).apply {
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) photoEditor.getFocusView()?.alpha = progress / 100f
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            })
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                handler.post {
                    photoEditor.setSaturation(progress / 100f)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        btnSave.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val bm = withContext(Dispatchers.Main) { photoEditor.saveBitmap() }
                val path = bm?.let { saveImage(it) }

                withContext(Dispatchers.Main) {
                    path?.let { supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PreviewFragemnt(it)).addToBackStack(null).commit() }
                }
            }
        }

        btnChangeSource.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT;
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 11)
        }

        btnAddImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT;
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 12)
        }

        btnAddText.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, EditTextFragment(this, EditTextFragment.CREATE, color = Color.BLACK))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11) {
            data?.data?.let {
                photoEditor.setImageSource(it)
            }
        } else if (requestCode == 12) {
            data?.data?.let {
                photoEditor.addImageView(it)
            }
        }
    }

    override fun onResult(mode: Int, text: String, typeface: Typeface?, color: Int) {
        when(mode) {
            EditTextFragment.CREATE -> {
                photoEditor.addTextView(text, typeface, color, this)
            }
            EditTextFragment.EDIT -> {
                val view = photoEditor.getFocusView()
                if (view is InsertTextView) {
                    view.setText(text)
                    view.setFont(typeface)
                    view.setColor(color)
                }
            }
        }
    }

    @RequiresPermission(value = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun saveImage(bm: Bitmap): Uri? {
        val value = ContentValues()
        value.put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpeg")
        value.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        value.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)

        uri?.let {
            val os = contentResolver.openOutputStream(uri)
            photoEditor.saveBitmap()?.let {
                it.compress(Bitmap.CompressFormat.JPEG, 100,  os)
            }
            os?.close()
        }

        return uri
    }

    override fun onEdit() {
        val view = photoEditor.getFocusView()
        if (view is InsertTextView) {
            val text = view.getText().toString()
            val color = view.getTextColor()
            val typeface = view.getTypeface()

            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, EditTextFragment(this, EditTextFragment.EDIT, text, typeface, color))
                .addToBackStack(null)
                .commit()
        }

    }

    override fun onItemFocus(insertView: InsertView) {
        alphaBar.visibility = View.VISIBLE
        alphaBar.progress = (insertView.alpha * 100).toInt()
    }

    override fun onItemUnfocus(insertView: InsertView) {
        alphaBar.visibility = View.INVISIBLE
    }
}
