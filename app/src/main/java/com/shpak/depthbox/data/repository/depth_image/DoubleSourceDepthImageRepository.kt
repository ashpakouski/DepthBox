package com.shpak.depthbox.data.repository.depth_image

import com.shpak.depthbox.data.model.DepthImage

interface DoubleSourceDepthImageRepository : DepthImageRepository {

    suspend fun getDepthImage(
        originalImageBytes: ByteArray, depthImageBytes: ByteArray, isInverted: Boolean
    ): DepthImage
}