package com.example.criminalintent.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import kotlin.math.roundToInt

object PictureUtils {

    @JvmStatic
    fun getScaledBitmap(activity: Activity, path: String): Bitmap {
        val displaySize = Point()
        activity.windowManager.defaultDisplay.getSize(displaySize)
        return getScaledBitmap(path, displaySize.x, displaySize.y)
    }


    @JvmStatic
    fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
        var inSampleSize = 1
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(path, options)
        val originWidth = options.outWidth.toFloat()
        val originHeight = options.outHeight.toFloat()

        if (originWidth > destWidth || originHeight > destHeight) {
            val widthScale = originWidth / destWidth
            val heightScale = originWidth / destHeight
            val sampleScale = if (widthScale > heightScale) {
                widthScale
            } else {
                heightScale
            }
            inSampleSize = sampleScale.roundToInt()
        }
        return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
        })
    }
}