package com.shpak.depthbox.data.util

import java.io.InputStream

fun InputStream.toByteArray(): ByteArray = buffered().use { it.readBytes() }