package com.shpak.depthbox.data.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color

fun ByteArray.toBitmap(shouldInvert: Boolean = false): Bitmap? =
    BitmapFactory.decodeByteArray(this, 0, size).run {
        if (shouldInvert) this.invertColors() else this
    }

fun Bitmap.invertColors(): Bitmap? = copy(Bitmap.Config.ARGB_8888, true)?.apply {
    val pixels = IntArray(width * height)

    getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in pixels.indices) {
        val pixel = pixels[i]

        pixels[i] = Color.argb(
            Color.alpha(pixel),
            0xFF - Color.red(pixel),
            0xFF - Color.green(pixel),
            0xFF - Color.blue(pixel)
        )
    }

    setPixels(pixels, 0, width, 0, 0, width, height)
}