package com.shpak.depthbox.data.repository.depth_image

import android.graphics.Bitmap
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

            DepthImage(
                main = findBitmap(jpegs, xmpDirectories, ORIGINAL_IMAGE_SEMANTIC_NAME),
                depth = findBitmap(jpegs, xmpDirectories, DEPTH_IMAGE_SEMANTIC_NAME)
            )
        }

    private fun findBitmap(
        jpegs: List<ByteArray>,
        xmpDirectories: List<GoogleCameraXmpDirectoryStruct>,
        directorySemantic: String
    ): Bitmap {
        val image = xmpDirectories.firstOrNull {
            it.semantic == directorySemantic
        }?.length?.let { imageLength ->
            // There should be a better way, but I still can't find it
            jpegs.minByOrNull { (it.size - imageLength).absoluteValue }
        } ?: throw Exception("Failed to find image for: $directorySemantic")

        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }
}