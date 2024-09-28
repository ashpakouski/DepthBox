package com.shpak.depthbox.ui

import android.app.Activity
import android.view.WindowInsets
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shpak.depthbox.R
import com.shpak.depthbox.data.model.DepthImage
import com.shpak.depthbox.ui.component.DepthBox

val OpenSans = FontFamily(
    Font(R.font.open_sans_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.open_sans_semi_bold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.open_sans_bold, FontWeight.Bold, FontStyle.Normal)
)

@Composable
fun DummyPixelLauncher(depthImage: DepthImage) {
    DepthBox(
        image = depthImage,
        contentDepth = 0.1f,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "18\n23",
            fontFamily = OpenSans,
            fontWeight = FontWeight.Normal,
            fontSize = 190.sp,
            lineHeight = 190.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StatusBar()

        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {

        }
    }
}

@Composable
private fun BoxScope.StatusBar(
    itemColor: Color = Color.White
) {
    val window = (LocalContext.current as? Activity)?.window?.decorView?.rootWindowInsets
    val statusBarHeight = window?.getInsets(WindowInsets.Type.statusBars())?.top ?: 0
    val statusBarHeightDp = with(LocalDensity.current) { statusBarHeight.toDp() }

    val statusIcons = listOf(
        Icons.Filled.NetworkWifi,
        Icons.Filled.NetworkCell,
        Icons.Filled.BatteryFull
    )

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
            color = itemColor
        )

        Spacer(modifier = Modifier.weight(1f))

        statusIcons.forEachIndexed { index, image ->
            Icon(
                imageVector = image,
                contentDescription = null,
                tint = itemColor,
                modifier = Modifier.size(16.dp)
            )

            if (index < statusIcons.lastIndex) {
                Spacer(modifier = Modifier.width(1.dp))
            }
        }
    }
}