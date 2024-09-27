package com.shpak.depthbox.data.repository.xmp_directory

import com.shpak.depthbox.data.model.XmpDirectoryStruct

interface XmpDirectoryRepository<T : XmpDirectoryStruct> {

    fun extractXmpDirectoryStructs(fileBytes: ByteArray): List<T>
}