package com.shpak.depthbox.data.util

object JpegExtractor {
    private val JPEG_MARKER_START = byteArrayOf(0xFF.toByte(), 0xD8.toByte())
    private val JPEG_MARKER_END = byteArrayOf(0xFF.toByte(), 0xD9.toByte())

    fun extractEmbeddedJpegs(fileBytes: ByteArray): List<ByteArray> =
        fileBytes.findRangeMarkers().map {
            fileBytes.copyOfRange(it.first, it.second)
        }

    private fun ByteArray.findRangeMarkers(): List<Pair<Int, Int>> {
        val jpegMarkers = mutableListOf<Pair<Int, Int>>()

        var startMarker = -1
        for (i in indices) {

            // Looking JPEG start
            if (
                get(i) == JPEG_MARKER_START[0] &&
                get(i + 1) == JPEG_MARKER_START[1]
            ) {
                startMarker = i
            }

            // Looking JPEG end
            if (
                get(i) == JPEG_MARKER_END[0] &&
                get(i + 1) == JPEG_MARKER_END[1] &&
                startMarker != -1
            ) {
                jpegMarkers.add(Pair(startMarker, i + 2))
                startMarker = -1
            }
        }

        return jpegMarkers
    }
}