package com.shpak.depthbox.data.repository.depth_image

import android.content.Context
import com.shpak.depthbox.data.model.DepthImage
import com.shpak.depthbox.data.util.toBitmap
import com.shpak.depthbox.data.util.toByteArray

class TestDepthImageRepository(
    private val context: Context
) : SingleSourceDepthImageRepository {

    override suspend fun getDepthImage(fileBytes: ByteArray, isInverted: Boolean): DepthImage =
        DepthImage(
            main = context.assets.open("test_50_cm.jpg").toByteArray().toBitmap(),
            depth = context.assets.open("test_50_cm_depth.jpg").toByteArray().toBitmap()
        )
}