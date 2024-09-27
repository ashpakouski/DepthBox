package com.shpak.depthbox.data.repository.depth_image

import com.shpak.depthbox.data.model.DepthImage

interface SingleSourceDepthImageRepository : DepthImageRepository {

    suspend fun getDepthImage(fileBytes: ByteArray, isInverted: Boolean): DepthImage
}