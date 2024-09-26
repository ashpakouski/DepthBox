package com.shpak.depthbox.ui.component

import android.graphics.BitmapShader
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
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
import com.shpak.depthbox.ui.shaders.DepthEffectShader

@Composable
fun DepthBox(
    image: DepthImage,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val originalBitmap = image.original
    val depthBitmap = image.depth

    var viewportSize by remember { mutableStateOf(IntSize.Zero) }

    val depthMapShader = RuntimeShader(DepthEffectShader)

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .graphicsLayer {
                depthMapShader.setFloatUniform(
                    "viewportSize",
                    viewportSize.width.toFloat(),
                    viewportSize.height.toFloat()
                )

                depthMapShader.setFloatUniform(
                    "pictureSize",
                    originalBitmap.width.toFloat(),
                    originalBitmap.height.toFloat()
                )

                depthMapShader.setFloatUniform(
                    "depthMapSize",
                    depthBitmap.width.toFloat(),
                    depthBitmap.height.toFloat()
                )

//                depthMapShader.setFloatUniform(
//                    "testViewRect",
//                    testViewRect.left,
//                    testViewRect.top,
//                    testViewRect.right,
//                    testViewRect.bottom
//                )

                depthMapShader.setInputBuffer(
                    "depthMap",
                    BitmapShader(
                        depthBitmap,
                        Shader.TileMode.DECAL,
                        Shader.TileMode.DECAL
                    )
                )

                depthMapShader.setInputBuffer(
                    "picture",
                    BitmapShader(
                        originalBitmap,
                        Shader.TileMode.DECAL,
                        Shader.TileMode.DECAL
                    )
                )

                renderEffect = RenderEffect
                    .createRuntimeShaderEffect(depthMapShader, "inputTexture")
                    .asComposeRenderEffect()
            }
            .onSizeChanged {
                viewportSize = it
            }
    ) {
        content()
    }
}