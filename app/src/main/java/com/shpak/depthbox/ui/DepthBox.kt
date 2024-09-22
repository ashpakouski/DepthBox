package com.shpak.depthbox.ui

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
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import com.shpak.depthbox.data.model.DepthImage
import com.shpak.depthbox.ui.shaders.DepthEffectShader
import com.shpak.depthbox.ui.shaders.struct.Rect
import com.shpak.depthbox.ui.shaders.struct.setRectUniform
import com.shpak.depthbox.ui.shaders.struct.toViewRect

@Composable
fun DepthBox(
    image: DepthImage,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val originalBitmap = image.original
    val depthBitmap = image.depth

    var rootCoordinates by remember { mutableStateOf(Rect.Zero) }

    val depthMapShader = RuntimeShader(DepthEffectShader)

    Box(
        modifier = modifier
            .graphicsLayer {
                depthMapShader.setRectUniform("rootViewRect", rootCoordinates)

                depthMapShader.setFloatUniform(
                    "originalPictureSize",
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
                    "originalPicture",
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
            .onGloballyPositioned {
                rootCoordinates = it.toViewRect()
            }
    ) {
        content()
    }
}