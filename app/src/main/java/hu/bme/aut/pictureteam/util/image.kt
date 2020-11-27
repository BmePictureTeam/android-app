package hu.bme.aut.pictureteam.util

import android.graphics.Bitmap
import kotlin.math.floor

fun Bitmap.resize(maxWidth: Int, maxHeight: Int): Bitmap {
    return if (maxHeight > 0 && maxWidth > 0) {
        val width = this.width
        val height = this.height
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
        var finalWidth = maxWidth
        var finalHeight = maxHeight
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }
        Bitmap.createScaledBitmap(this, finalWidth, finalHeight, true)
    } else {
        this
    }
}

fun Bitmap.scalePercent(width: Float, height: Float = width): Bitmap {
    return this.resize(
        floor(this.width * width).toInt(),
        floor(this.height * height).toInt()
    )
}
