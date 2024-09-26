package com.shpak.depthbox.ui

import android.app.Activity
import android.graphics.BitmapFactory
import android.view.WindowInsets
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shpak.depthbox.R

val OpenSans = FontFamily(
    Font(R.font.open_sans_regular, FontWeight.Normal, FontStyle.Normal)
)

@Composable
@Preview
fun DummyPixelLauncher() {
    val backgroundBitmap = BitmapFactory.decodeStream(LocalContext.current.assets.open("test.jpg"))
    Image(
        bitmap = backgroundBitmap.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

    Box(modifier = Modifier.fillMaxSize()) {
        StatusBar()

        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "18\n23",
                fontFamily = OpenSans,
                fontWeight = FontWeight.Normal,
                fontSize = 190.sp,
                lineHeight = 190.sp
            )
        }
    }
}

@Composable
private fun BoxScope.StatusBar() {
    val window = (LocalContext.current as? Activity)?.window?.decorView?.rootWindowInsets
    val statusBarHeight = window?.getInsets(WindowInsets.Type.statusBars())?.top ?: 0
    val statusBarHeightDp = with(LocalDensity.current) { statusBarHeight.toDp() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(statusBarHeightDp)
            .padding(horizontal = 32.dp)
            .align(Alignment.TopCenter)
    ) {
        Text(
            text = "Pineapple",
            fontFamily = OpenSans,
            fontWeight = FontWeight.Normal,
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Filled.NetworkWifi,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )

        Icon(
            imageVector = Icons.Filled.NetworkCell,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )

        Icon(
            imageVector = Icons.Filled.BatteryFull,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}