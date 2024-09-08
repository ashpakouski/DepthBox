package com.shpak.easyruler

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // extractAllEmbeddedJpegs(assets.open("test.jpg"), "$filesDir")

        val depthBitmap = BitmapFactory.decodeStream(assets.open("depth.jpg"))

        setContent {
            var viewableBitmap by remember { mutableStateOf(depthBitmap) }
            var imageViewSize by remember { mutableStateOf(IntSize.Zero) }

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = viewableBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val imageX =
                                    (depthBitmap.width * offset.x / imageViewSize.width).toInt()
                                val imageY =
                                    (depthBitmap.height * offset.y / imageViewSize.height).toInt()

                                val pixel = depthBitmap.getPixel(imageX, imageY)

                                val tempBmp = depthBitmap.copy(depthBitmap.config, true)

                                val pointWidth = 10
                                for (x in imageX - pointWidth .. imageX + pointWidth) {
                                    for (y in imageY - pointWidth .. imageY + pointWidth) {
                                        tempBmp.setPixel(x, y, Color.RED)
                                    }
                                }

                                viewableBitmap = tempBmp

                                for (i in 0..24 step 8) {
                                    Log.d(
                                        "TAG123",
                                        "Pixel[$imageX, $imageY]: ${pixel shr i and 0xFF}"
                                    )
                                }
                            }
                        }
                        .onGloballyPositioned { layoutCoordinates ->
                            imageViewSize = layoutCoordinates.size
                        }
                )
            }
        }
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