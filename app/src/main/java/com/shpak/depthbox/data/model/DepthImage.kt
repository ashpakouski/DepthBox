package com.shpak.depthbox.data.model

import android.graphics.Bitmap

data class DepthImage(
    val original: Bitmap,
    val depth: Bitmap
)