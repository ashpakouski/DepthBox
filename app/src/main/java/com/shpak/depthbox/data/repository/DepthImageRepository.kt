package com.shpak.depthbox.data.repository

import com.shpak.depthbox.data.model.DepthImage

interface DepthImageRepository {

    suspend fun getDepthImage(fileBytes: ByteArray): DepthImage
}