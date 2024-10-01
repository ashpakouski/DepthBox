package com.shpak.depthbox.ui.demo

import android.content.Context
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shpak.depthbox.data.model.DepthImage
import com.shpak.depthbox.data.repository.depth_image.GoogleCameraDepthImageRepository
import com.shpak.depthbox.data.repository.xmp_directory.GoogleCameraXmpDirectoryRepository
import com.shpak.depthbox.data.util.toByteArray
import com.shpak.depthbox.ui.component.DepthBox
import kotlinx.coroutines.runBlocking
import java.util.Locale

@Composable
fun DepthViewDemo() {
    val context = LocalContext.current
    val depthImage = remember { context.getDepthImage("bench.jpg") }

    var contentDepth by remember { mutableFloatStateOf(0.0f) }
    var innerViewOffset by remember { mutableStateOf(IntOffset.Zero) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            DepthBox(
                image = depthImage,
                contentDepth = contentDepth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val position = change.position
                            innerViewOffset = IntOffset(position.x.toInt(), position.y.toInt())
                        }
                    }
            ) {
                Text(
                    text = "🦜",
                    fontSize = 100.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset { innerViewOffset }
                )
            }

            DepthController(
                depthNormalized = contentDepth,
                onDepthChange = { contentDepth = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun DepthController(
    depthNormalized: Float,
    onDepthChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = depthNormalized,
            onValueChange = onDepthChange,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Depth ${String.format(Locale.US, "%.2f", depthNormalized)}"
        )
    }
}

private fun Context.getDepthImage(assetFileName: String): DepthImage =
    runBlocking { // Just for demonstration
        val repository = GoogleCameraDepthImageRepository(GoogleCameraXmpDirectoryRepository())
        repository.getDepthImage(assets.open(assetFileName).toByteArray(), isInverted = false)
    }