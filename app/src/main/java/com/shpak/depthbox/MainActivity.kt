package com.shpak.depthbox

import android.graphics.BitmapShader
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.shpak.depthbox.data.repository.TestDepthImageRepository
import kotlinx.coroutines.runBlocking

private val depthMapShader = RuntimeShader(
    """
    uniform shader inputTexture;
    uniform shader depthMap;
    uniform float coordMultiplier;

    vec4 main(vec2 fragCoord) {
        vec4 depthPixel = depthMap.eval(fragCoord * coordMultiplier);
        vec4 originalPixel = inputTexture.eval(fragCoord);
        
        float depth = depthPixel.r;
        
        if (depthPixel.r < 0.2) {
            return depthPixel;
        }

        return originalPixel;
    }
    """
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val testImage = runBlocking {
            TestDepthImageRepository(applicationContext).getDepthImage(byteArrayOf())
        }

        val originalBitmap = testImage.original
        val depthBitmap = testImage.depth

        setContent {
            var resolution by remember { mutableStateOf(IntSize.Zero) }

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = originalBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {
                            depthMapShader.setFloatUniform(
                                "coordMultiplier",
                                depthBitmap.width / resolution.width.toFloat()
                            )

                            depthMapShader.setInputBuffer(
                                "depthMap",
                                BitmapShader(
                                    depthBitmap,
                                    Shader.TileMode.DECAL,
                                    Shader.TileMode.DECAL
                                )
                            )

                            renderEffect = RenderEffect
                                .createRuntimeShaderEffect(depthMapShader, "inputTexture")
                                .asComposeRenderEffect()
                        }
                        .onGloballyPositioned {
                            resolution = it.size
                        }
                )
            }
        }
    }
}