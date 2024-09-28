package com.shpak.depthbox.ui.component

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.shpak.depthbox.data.model.DepthImage
import com.shpak.depthbox.ui.shader.DepthEffectShader

@Composable
fun DepthBox(
    image: DepthImage,
    @FloatRange(from = 0.0, to = 1.0) contentDepth: Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .graphicsLayer {

                renderEffect = DepthEffectShader
                    .createRenderEffect(viewportSize, image.main, image.depth, contentDepth)
                    .asComposeRenderEffect()
            }
            .onSizeChanged {
                viewportSize = it
            }
    ) {
        content()
    }
}