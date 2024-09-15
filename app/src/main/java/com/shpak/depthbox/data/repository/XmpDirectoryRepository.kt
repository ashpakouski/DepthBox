package com.shpak.depthbox.data.repository

import com.shpak.depthbox.data.model.XmpDirectoryStruct

interface XmpDirectoryRepository<T : XmpDirectoryStruct> {

    fun extractXmpDirectoryStructs(fileBytes: ByteArray): List<T>
}