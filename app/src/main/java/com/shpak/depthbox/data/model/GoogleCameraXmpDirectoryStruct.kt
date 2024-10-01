package com.shpak.depthbox.data.model

// This model matches the struct of the XMP directory created by Google Camera.
// Have no idea if this properties are reusable.
data class GoogleCameraXmpDirectoryStruct(
    val length: Int,
    val mimeType: String,
    val semantic: String
) : XmpDirectoryStruct
