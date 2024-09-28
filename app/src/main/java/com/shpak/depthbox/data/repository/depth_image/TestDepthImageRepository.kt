package com.shpak.depthbox.data.repository.depth_image

import android.content.Context
import android.graphics.BitmapFactory
import com.shpak.depthbox.data.model.DepthImage

class TestDepthImageRepository(
    private val context: Context
) : SingleSourceDepthImageRepository {

    override suspend fun getDepthImage(fileBytes: ByteArray, isInverted: Boolean): DepthImage = DepthImage(
        main = BitmapFactory.decodeStream(context.assets.open("test_50_cm.jpg")),
        depth = BitmapFactory.decodeStream(context.assets.open("test_50_cm_depth.jpg"))
    )
}