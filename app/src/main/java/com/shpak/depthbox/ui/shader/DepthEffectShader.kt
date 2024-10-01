package com.shpak.depthbox.ui.shader

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import androidx.compose.ui.unit.IntSize
import org.intellij.lang.annotations.Language

object DepthEffectShader {

    /**
     * This property is annotated with "GLSL", because is allows to use some syntax highlighting
     * enabled by "GLSL" plugin. Most of GLSL functionality can't be applied to AGSL, so it's
     * up to you to use this lifehack.
     */
    @Language("GLSL")
    private val DepthEffectShader = """
    uniform shader inputTexture;
    uniform shader mainPicture;
    uniform shader depthMap;
    
    uniform float contentDepth;
    
    uniform vec2 viewportSize;
    uniform vec2 mainPictureSize;
    uniform vec2 depthMapSize;
    
    vec2 translatePixelCoord(vec2 contentSize, vec2 viewSize, vec2 fragCoord) {
        float viewportAspectRatio = viewSize.x / viewSize.y;
        float pictureAspectRatio = contentSize.x / contentSize.y;
    
        bool isPortrait = viewportAspectRatio < pictureAspectRatio;
        float coordMultiplier = isPortrait ? (contentSize.y / viewSize.y) : (contentSize.x / viewSize.x);
    
        vec2 offset = isPortrait 
            ? vec2((contentSize.x - viewSize.x * coordMultiplier) / 2.0, 0.0) 
            : vec2(0.0, (contentSize.y - viewSize.y * coordMultiplier) / 2.0);
            
        return fragCoord * coordMultiplier + offset;
    }
    
    vec4 main(vec2 fragCoord) {
        vec2 pictureCoordTranslated = translatePixelCoord(mainPictureSize, viewportSize, fragCoord);
        vec2 depthCoordTranslated = translatePixelCoord(depthMapSize, viewportSize, fragCoord);
    
        vec4 inputPixel = inputTexture.eval(fragCoord);
        vec4 picturePixel = mainPicture.eval(pictureCoordTranslated);
        vec4 depthPixel = depthMap.eval(depthCoordTranslated);
    
        if (depthPixel.x >= contentDepth && inputPixel.a != 0.0) {
            return inputTexture.eval(fragCoord);
        } else {
            return picturePixel;
        }
    }
    """.trimIndent().let { RuntimeShader(it) }

    fun createRenderEffect(
        viewportSize: IntSize,
        bitmapMain: Bitmap,
        bitmapDepth: Bitmap,
        contentDepth: Float,
    ): RenderEffect {
        val shader = DepthEffectShader.apply {
            setFloatUniform(
                "viewportSize",
                viewportSize.width.toFloat(), viewportSize.height.toFloat()
            )

            setFloatUniform(
                "mainPictureSize",
                bitmapMain.width.toFloat(), bitmapMain.height.toFloat()
            )

            setFloatUniform(
                "depthMapSize",
                bitmapDepth.width.toFloat(), bitmapDepth.height.toFloat()
            )

            setFloatUniform("contentDepth", contentDepth)

            setInputBuffer(
                "depthMap",
                BitmapShader(bitmapDepth, Shader.TileMode.DECAL, Shader.TileMode.DECAL)
            )

            setInputBuffer(
                "mainPicture",
                BitmapShader(bitmapMain, Shader.TileMode.DECAL, Shader.TileMode.DECAL)
            )
        }

        return RenderEffect.createRuntimeShaderEffect(shader, "inputTexture")
    }
}