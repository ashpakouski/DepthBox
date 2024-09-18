package com.shpak.depthbox

import android.graphics.BitmapShader
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.shpak.depthbox.data.model.DepthImage
import com.shpak.depthbox.data.repository.GoogleCameraDepthImageRepository
import com.shpak.depthbox.data.repository.GoogleCameraXmpDirectoryRepository
import kotlinx.coroutines.runBlocking

private val depthMapShader = RuntimeShader(
    """
    uniform shader inputTexture;
    uniform shader depthMap;
    uniform shader originalPicture;

    uniform vec2 viewSize;
    uniform vec2 originalPictureSize;
    // uniform float depthCoordMultiplier;
    // uniform float originalCoordMultiplier;

    uniform vec4 testViewRect;

    vec4 main(vec2 fragCoord) {
        float viewAspectRatio = viewSize.x / viewSize.y;
        float pictureAspectRatio = originalPictureSize.x / originalPictureSize.y;

        if (viewAspectRatio < pictureAspectRatio) {
            float originalCoordMultiplier = viewSize.y / originalPictureSize.y;
            float croppedImageWidth = viewSize.y * originalCoordMultiplier;
            float xOffset = (originalPictureSize.x - croppedImageWidth) / 2.0;
            vec2 croppedPixelCoord = vec2(fragCoord.x + xOffset, fragCoord.y) / originalCoordMultiplier;
            vec4 imagePixel = originalPicture.eval(croppedPixelCoord);
            
            return imagePixel;
        } else {
        
        }
        
        // vec4 depthPixel = depthMap.eval(fragCoord * depthCoordMultiplier);
        // vec4 originalPixel = originalPicture.eval(fragCoord);
        // vec4 viewPixel = inputTexture.eval(fragCoord);
        
        // return originalPixel;

        // float depth = depthPixel.r;

        // if (fragCoord.x >= testViewRect.x &&
        //     fragCoord.x <= testViewRect.z &&
        //     fragCoord.y >= testViewRect.y &&
        //     fragCoord.y <= testViewRect.w
        // ) {
        //     if (depthPixel.r < 0.60 || depthPixel.r > 0.98) {
        //         return originalPixel;
        //     } else {
        //         return viewPixel;
        //     }
        // }

        return vec4(0.0, 1.0, 1.0, 1.0);
    }
    """
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val depthImage = runBlocking {
            GoogleCameraDepthImageRepository(GoogleCameraXmpDirectoryRepository()).getDepthImage(
                assets.open("test_3.jpg").readBytes()
            )
        }

        setContent {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                DepthBox(
                    depthImage = depthImage,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun DepthBox(
    depthImage: DepthImage,
    modifier: Modifier = Modifier
) {
    val originalBitmap = depthImage.original
    val depthBitmap = depthImage.depth

    var viewSize by remember { mutableStateOf(IntSize.Zero) }
    var testViewRect by remember { mutableStateOf(Rect.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Red)
            .graphicsLayer {
                depthMapShader.setFloatUniform(
                    "viewSize",
                    viewSize.width.toFloat(),
                    viewSize.height.toFloat()
                )

                depthMapShader.setFloatUniform(
                    "originalPictureSize",
                    originalBitmap.width.toFloat(),
                    originalBitmap.height.toFloat()
                )

//                depthMapShader.setFloatUniform(
//                    "depthCoordMultiplier",
//                    depthBitmap.width / resolution.width.toFloat()
//                )

//                depthMapShader.setFloatUniform(
//                    "originalCoordMultiplier",
//                    originalBitmap.width / resolution.width.toFloat()
//                )

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
            .onGloballyPositioned {
                viewSize = it.size
            }
    ) {
//        Image(
//            bitmap = originalBitmap.asImageBitmap(),
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.Yellow)
//                .align(Alignment.Center)
//
//        )

        Text(
            text = "23\n55",
            color = Color.Yellow,
            fontWeight = FontWeight.Bold,
            fontSize = 191.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .onGloballyPositioned {
                    testViewRect = it.boundsInParent()
                }
        )
    }
}