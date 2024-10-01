package com.shpak.depthbox.data.repository.depth_image

import com.shpak.depthbox.data.model.DepthImage
import com.shpak.depthbox.data.util.toBitmap

class DefaultDoubleSourceImageRepository : DoubleSourceDepthImageRepository {

    override suspend fun getDepthImage(
        originalImageBytes: ByteArray, depthImageBytes: ByteArray, isInverted: Boolean
    ): DepthImage = DepthImage(
        main = originalImageBytes.toBitmap(),
        depth = depthImageBytes.toBitmap(isInverted)
    )
}