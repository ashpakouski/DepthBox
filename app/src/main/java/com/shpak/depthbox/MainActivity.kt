package com.shpak.depthbox

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
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

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
}