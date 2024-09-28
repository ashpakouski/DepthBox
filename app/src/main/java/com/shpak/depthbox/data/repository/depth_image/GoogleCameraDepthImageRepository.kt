package com.shpak.depthbox.data.repository.depth_image

import android.graphics.BitmapFactory
import com.shpak.depthbox.data.model.DepthImage
import com.shpak.depthbox.data.model.GoogleCameraXmpDirectoryStruct
import com.shpak.depthbox.data.repository.xmp_directory.XmpDirectoryRepository
import com.shpak.depthbox.data.util.JpegExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

class GoogleCameraDepthImageRepository(
    private val xmpDirectoryRepository: XmpDirectoryRepository<GoogleCameraXmpDirectoryStruct>
) : SingleSourceDepthImageRepository {

    private companion object {
        const val ORIGINAL_IMAGE_SEMANTIC_NAME = "Original"
        const val DEPTH_IMAGE_SEMANTIC_NAME = "Depth"
    }

    override suspend fun getDepthImage(fileBytes: ByteArray, isInverted: Boolean): DepthImage =
        withContext(Dispatchers.Default) {
            val jpegs = JpegExtractor.extractEmbeddedJpegs(fileBytes)
            val xmpDirectories = xmpDirectoryRepository.extractXmpDirectoryStructs(fileBytes)

            val originalImage = xmpDirectories.firstOrNull {
                it.semantic == ORIGINAL_IMAGE_SEMANTIC_NAME
            }?.length?.let { originalImageLength ->
                jpegs.minByOrNull { (it.size - originalImageLength).absoluteValue }
            } ?: throw Exception("Failed to find original image")

            val depthImage = xmpDirectories.firstOrNull {
                it.semantic == DEPTH_IMAGE_SEMANTIC_NAME
            }?.length?.let { depthImageLength ->
                jpegs.minByOrNull { (it.size - depthImageLength).absoluteValue }
            } ?: throw Exception("Failed to find depth image")

            DepthImage(
                main = BitmapFactory.decodeByteArray(originalImage, 0, originalImage.size),
                depth = BitmapFactory.decodeByteArray(depthImage, 0, depthImage.size),
            )
        }
}