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
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.shpak.depthbox.data.repository.TestDepthImageRepository
import kotlinx.coroutines.runBlocking

private val depthMapShader = RuntimeShader(
    """
    uniform shader inputTexture;
    uniform shader depthMap;
    uniform shader originalPicture;

    uniform float depthCoordMultiplier;
    uniform float originalCoordMultiplier;

    uniform vec4 testViewRect;

    vec4 main(vec2 fragCoord) {
        vec4 depthPixel = depthMap.eval(fragCoord * depthCoordMultiplier);
        vec4 originalPixel = originalPicture.eval(fragCoord * originalCoordMultiplier);
        vec4 viewPixel = inputTexture.eval(fragCoord);

        float depth = depthPixel.r;

        if (fragCoord.x >= testViewRect.x &&
            fragCoord.x <= testViewRect.z &&
            fragCoord.y >= testViewRect.y &&
            fragCoord.y <= testViewRect.w
        ) {
            if (depthPixel.r < 0.15) {
                return originalPixel;
            } else {
                return viewPixel;
            }
        }

//        if (depthPixel.r < 0.2) {
//            return originalPixel;
//        }

        return viewPixel;
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
            var testViewRect by remember { mutableStateOf(Rect.Zero) }

            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {
                            depthMapShader.setFloatUniform(
                                "depthCoordMultiplier",
                                depthBitmap.width / resolution.width.toFloat()
                            )

                            depthMapShader.setFloatUniform(
                                "originalCoordMultiplier",
                                originalBitmap.width / resolution.width.toFloat()
                            )

                            depthMapShader.setFloatUniform(
                                "testViewRect",
                                testViewRect.left,
                                testViewRect.top,
                                testViewRect.right,
                                testViewRect.bottom
                            )

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
                ) {
                    Image(
                        bitmap = originalBitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .onGloballyPositioned {
                                resolution = it.size
                            }
                    )

                    Text(
                        text = "HELLO",
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 142.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .onGloballyPositioned {
                                testViewRect = it.boundsInParent()
                            }
                    )
                }
            }
        }
    }
}