package com.icongkhanh.photoeditor

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A simple [Fragment] subclass.
 */
class EditTextFragment(val onResultListener: OnResultListener, val mode: Int, val text: String = "", val typeface: Typeface? = null, val color: Int) : Fragment() {

    companion object {
        val EDIT = 2
        val CREATE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_text, container, false)
    }

    private val listItemFont = mutableListOf<ItemFont>()
    private val listItemColor = mutableListOf<ItemColor>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose = view.findViewById<Button>(R.id.btn_close)
        val btnDone = view.findViewById<Button>(R.id.btn_done)
        val edit = view.findViewById<EditText>(R.id.edt)
        val listFontRcv = view.findViewById<RecyclerView>(R.id.list_font)
        val listColorRcv = view.findViewById<RecyclerView>(R.id.list_color)

        val listFont = requireContext().assets.list("font")
        listFont?.forEach {
            val typeface = Typeface.createFromAsset(requireContext().assets, "font/${it}")
            this.listItemFont.add(
                ItemFont(
                it,
                    if (text.isNullOrEmpty()) "ABC" else text,
                typeface
                ) {
                    edit.typeface = typeface
                }
            )
        }

        listFontRcv.adapter = ListFontAdapter(requireContext()).apply {
            submitList(this@EditTextFragment.listItemFont)
        }
        listFontRcv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        val listColor = listOf(Color.RED, Color.BLUE)
        listColor.forEach {
            this.listItemColor.add(
                ItemColor(
                    "red",
                    it
                ) {
                    edit.setTextColor(it)
                }
            )
        }

        listColorRcv.adapter = ListColorAdapter(requireContext()).apply {
            submitList(this@EditTextFragment.listItemColor)
        }
        listColorRcv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        edit.typeface = typeface
        edit.setTextColor(color)
        edit.setText(text)
        edit.requestFocus()

        btnClose.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        btnDone.setOnClickListener {
            val text = edit.text.toString()
            if (text.isNotEmpty()) onResultListener.onResult(mode, text, edit.typeface, edit.currentTextColor)
            fragmentManager?.popBackStack()
        }
    }

}

interface OnResultListener {
    fun onResult(mode: Int, text: String, typeface: Typeface?, color: Int)
}
