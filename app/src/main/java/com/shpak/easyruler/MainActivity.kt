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
import androidx.core.graphics.green
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.xmp.XmpDirectory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        listXmpDirectories()

        val depthMapNear = 0.578811
        val depthMapFar = 1.707531

        val principalPointX = 1523.186035
        val principalPointY = 2019.192139

        val focalLength = 2904.9021

        fun getX(coordX: Float, depth: Float): Float {
            return ((coordX - principalPointX) * depth / focalLength).toFloat()
        }

        fun getY(coordY: Float, depth: Float): Float {
            return ((coordY - principalPointY) * depth / focalLength).toFloat()
        }

        extractAllEmbeddedJpegs(assets.open("test_50_cm.jpg"), "$filesDir")

        val depthBitmap = BitmapFactory.decodeStream(assets.open("test_50_cm_depth.jpg"))

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
                                for (x in imageX - pointWidth..imageX + pointWidth) {
                                    for (y in imageY - pointWidth..imageY + pointWidth) {
                                        tempBmp.setPixel(x, y, Color.RED)
                                    }
                                }

                                viewableBitmap = tempBmp

                                val pixelDepthMeters =
                                    depthMapNear + (pixel.green / 255f) * (depthMapFar - depthMapNear)

                                val x = getX(imageX.toFloat(), pixelDepthMeters.toFloat())
                                val y = getY(imageY.toFloat(), pixelDepthMeters.toFloat())
                                val z = pixelDepthMeters.toFloat()

                                val distance = sqrt(
                                    (x - lastX).pow(2f) + (y - lastY).pow(2f) + (z - lastZ).pow(2f)
                                )

                                Log.d("TAG123", "Dist: $distance")

                                lastX = x
                                lastY = y
                                lastZ = z
                            }
                        }
                        .onGloballyPositioned { layoutCoordinates ->
                            imageViewSize = layoutCoordinates.size
                        }
                )
            }
        }
    }

    private fun listXmpDirectories() {
        val metadata = ImageMetadataReader.readMetadata(assets.open("test.jpg"))

        metadata.getFirstDirectoryOfType(XmpDirectory::class.java)?.let { xmpDirectory ->
            val xmpMeta = xmpDirectory.xmpMeta
            val containerNamespace = "http://ns.google.com/photos/1.0/container/"
            val directoryPath = "Container:Directory"

            val arraySize = xmpMeta.countArrayItems(containerNamespace, directoryPath)

            for (i in 1..arraySize) {
                val itemPath = "$directoryPath[$i]/Container:Item"
                val length = xmpMeta.getPropertyString(containerNamespace, "$itemPath/Item:Length")
                val mime = xmpMeta.getPropertyString(containerNamespace, "$itemPath/Item:Mime")
                val semantic =
                    xmpMeta.getPropertyString(containerNamespace, "$itemPath/Item:Semantic")

                Log.d("TAG123", "Length[$i]: $length")
                Log.d("TAG123", "Mime[$i]: $mime")
                Log.d("TAG123", "Semantic[$i]: $semantic")
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