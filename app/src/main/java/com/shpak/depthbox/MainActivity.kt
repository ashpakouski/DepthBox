package com.shpak.depthbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shpak.depthbox.data.repository.GoogleCameraDepthImageRepository
import com.shpak.depthbox.data.repository.GoogleCameraXmpDirectoryRepository
import com.shpak.depthbox.ui.DepthBox
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val depthImage = runBlocking {
            GoogleCameraDepthImageRepository(GoogleCameraXmpDirectoryRepository()).getDepthImage(
                assets.open("vase.jpg").readBytes()
            )
        }

        setContent {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                DepthBox(
                    image = depthImage,
                    modifier = Modifier
                        .size(400.dp, 700.dp)
                        .fillMaxSize()
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = "15\n20",
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 191.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}