package com.icongkhanh.photoeditor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), OnResultListener, OnRequestEditListener {
    private lateinit var photoEditor: PhotoEditor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 10)
        }

        val photoEditorView = findViewById<PhotoEditorView>(R.id.photo_editor)
        photoEditor = PhotoEditor(photoEditorView)

        val btnAddImage = findViewById<Button>(R.id.btn_add_image)
        val btnAddText = findViewById<Button>(R.id.btn_add_text)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val btnChangeSource = findViewById<Button>(R.id.btn_add_source)

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
    private fun saveImage(bm: Bitmap): String {
        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val saveFile = File(file, "IMG_${System.currentTimeMillis()}.jpeg")
        if (saveFile.exists()) saveFile.delete()
        saveFile.createNewFile()

        val fos = FileOutputStream(saveFile)
        bm.compress(Bitmap.CompressFormat.JPEG, 100, fos)

        fos.close()
        return saveFile.absolutePath
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
}
