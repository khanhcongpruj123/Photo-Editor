package com.icongkhanh.photoeditor

import android.view.MotionEvent
import android.view.View

class GestureDetector(
    val onMove: (deltaX: Float, deltaY: Float) -> Unit,
    val onRotate: (rotation: Float) -> Unit
) : View.OnTouchListener {

    private var mPrevX = -1F
    private var mPrevY = -1F

    private val scaleGestureDetector : ScaleGestureDetector
    private val mPrevSpanVector = Vector2D()

    init {
        scaleGestureDetector = ScaleGestureDetector(object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(view: View?, detector: ScaleGestureDetector?): Boolean {
                mPrevSpanVector.set(detector?.getCurrentSpanVector())
                return true
            }

            override fun onScale(view: View?, detector: ScaleGestureDetector?): Boolean {
                val rotation = Vector2D.getAngle(mPrevSpanVector, detector?.getCurrentSpanVector()?: Vector2D())
                onRotate(rotation)
                return true
            }

            override fun onScaleEnd(view: View?, detector: ScaleGestureDetector?) {
                super.onScaleEnd(view, detector)
            }
        })
    }


    override fun onTouch(v: View, event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(v, event)

        when(event.action and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mPrevX = event.x
                mPrevY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (!scaleGestureDetector.isInProgress) {
                    val currX = event.x
                    val currY = event.y

                    if (event.pointerCount == 1) onMove(currX - mPrevX, currY - mPrevY)
                }
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }

}