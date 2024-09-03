package com.shpak.easyruler

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        extractAllEmbeddedJpegs(assets.open("test.jpg"), "$filesDir")
    }
}

fun extractAllEmbeddedJpegs(inputStream: InputStream, outputDirPath: String) {
    val fileBytes = inputStream.readBytes()
    inputStream.close()

    val jpegMarkers = mutableListOf<Pair<Int, Int>>()

    var startMarker = -1
    for (i in fileBytes.indices) {
        // Looking for FFD8 marker (Start of JPEG)
        if (fileBytes[i] == 0xFF.toByte() && fileBytes[i + 1] == 0xD8.toByte()) {
            startMarker = i
        }

        // Looking for FFD9 marker (End of JPEG)
        if (fileBytes[i] == 0xFF.toByte() && fileBytes[i + 1] == 0xD9.toByte()) {
            if (startMarker != -1) {
                // Found a complete JPEG segment
                jpegMarkers.add(Pair(startMarker, i + 2))
                startMarker = -1 // Reset for the next JPEG
            }
        }
    }

    for ((index, markers) in jpegMarkers.withIndex()) {
        val (start, end) = markers
        val embeddedJpegBytes = fileBytes.copyOfRange(start, end)

        val outputFile = File(outputDirPath, "extracted_image_$index.jpg")
        FileOutputStream(outputFile).use { fos ->
            fos.write(embeddedJpegBytes)
            fos.flush()
        }
    }
}