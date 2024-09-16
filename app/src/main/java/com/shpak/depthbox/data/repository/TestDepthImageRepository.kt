package com.shpak.depthbox.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import com.shpak.depthbox.data.model.DepthImage

class TestDepthImageRepository(
    private val context: Context
) : DepthImageRepository {

    override suspend fun getDepthImage(fileBytes: ByteArray): DepthImage = DepthImage(
        original = BitmapFactory.decodeStream(context.assets.open("test_50_cm.jpg")),
        depth = BitmapFactory.decodeStream(context.assets.open("test_50_cm_depth.jpg"))
    )
}