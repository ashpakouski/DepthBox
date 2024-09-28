package com.shpak.depthbox.data.model

import android.graphics.Bitmap

data class DepthImage(
    val main: Bitmap,
    val depth: Bitmap
)