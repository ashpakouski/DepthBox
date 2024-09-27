package com.shpak.depthbox.data.repository.xmp_directory

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.xmp.XmpDirectory
import com.shpak.depthbox.data.model.GoogleCameraXmpDirectoryStruct

class GoogleCameraXmpDirectoryRepository : XmpDirectoryRepository<GoogleCameraXmpDirectoryStruct> {

    companion object {
        private const val CONTAINER_NAMESPACE = "http://ns.google.com/photos/1.0/container/"
        private const val DIRECTORY_PATH = "Container:Directory"
    }

    override fun extractXmpDirectoryStructs(fileBytes: ByteArray): List<GoogleCameraXmpDirectoryStruct> =
        ImageMetadataReader.readMetadata(fileBytes.inputStream())
            ?.getDirectoriesOfType(XmpDirectory::class.java)
            ?.flatMap {
                it.extractStructs()
            } ?: emptyList()

    private fun XmpDirectory.extractStructs(): List<GoogleCameraXmpDirectoryStruct> {
        val arraySize = xmpMeta.countArrayItems(CONTAINER_NAMESPACE, DIRECTORY_PATH)

        return (1..arraySize).mapNotNull { index ->
            val itemPath = "$DIRECTORY_PATH[$index]/Container:Item"

            GoogleCameraXmpDirectoryStruct(
                length = xmpMeta.getPropertyInteger(
                    CONTAINER_NAMESPACE, "$itemPath/Item:Length"
                ) ?: 0,
                mimeType = xmpMeta.getPropertyString(
                    CONTAINER_NAMESPACE, "$itemPath/Item:Mime"
                ) ?: "",
                semantic = xmpMeta.getPropertyString(
                    CONTAINER_NAMESPACE, "$itemPath/Item:Semantic"
                ) ?: ""
            )
        }
    }
}