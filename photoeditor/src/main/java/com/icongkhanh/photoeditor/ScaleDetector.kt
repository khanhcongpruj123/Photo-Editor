package com.icongkhanh.photoeditor

import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

class ScaleDetector(val  onScale: (deltaX: Float, deltaY: Float) -> Unit): View.OnTouchListener {


    private var mPrevY = -1F
    private var mPrevX = -1F

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when(event.action and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mPrevX = event.x
                mPrevY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val currX = event.x
                val currY = event.y

                val deltaX = currX - mPrevX
                val deltaY = currY - mPrevY

                onScale(deltaX, deltaY)
            }
        }
        return true
    }

}