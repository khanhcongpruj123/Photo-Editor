package com.icongkhanh.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View


fun adjustTranslation(view: View, deltaX: Float, deltaY: Float) {
    val deltaVector = floatArrayOf(deltaX, deltaY)
    view.matrix.mapVectors(deltaVector)
    view.translationX = view.translationX + deltaVector[0]
    view.translationY = view.translationY + deltaVector[1]
}

fun adjustAngle(degrees: Float): Float {
    var degrees = degrees
    if (degrees > 180.0f) {
        degrees -= 360.0f
    } else if (degrees < -180.0f) {
        degrees += 360.0f
    }
    return degrees
}

fun removeTransparency(source: Bitmap): Bitmap? {
    var firstX = 0
    var firstY = 0
    var lastX = source.width
    var lastY = source.height
    val pixels = IntArray(source.width * source.height)
    source.getPixels(pixels, 0, source.width, 0, 0, source.width, source.height)
    loop@ for (x in 0 until source.width) {
        for (y in 0 until source.height) {
            if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                firstX = x
                break@loop
            }
        }
    }
    loop@ for (y in 0 until source.height) {
        for (x in firstX until source.height) {
            if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                firstY = y
                break@loop
            }
        }
    }
    loop@ for (x in source.width - 1 downTo firstX) {
        for (y in source.height - 1 downTo firstY) {
            if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                lastX = x
                break@loop
            }
        }
    }
    loop@ for (y in source.height - 1 downTo firstY) {
        for (x in source.width - 1 downTo firstX) {
            if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                lastY = y
                break@loop
            }
        }
    }
    return Bitmap.createBitmap(source, firstX, firstY, lastX - firstX, lastY - firstY)
}