package com.shpak.depthbox.ui.shaders

import org.intellij.lang.annotations.Language

/**
 * This string is annotated as "GLSL" just because is allows to use some syntax highlighting
 * enabled by "GLSL" plugin. Most of GLSL functionality can't be applied to AGSL, so it's
 * up to you to use this lifehack.
 */

@Language("GLSL")
val depthEffectShader = """
    uniform shader inputTexture;
    uniform shader depthMap;
    uniform shader originalPicture;
    
    uniform vec2 viewSize;
    uniform vec2 originalPictureSize;
    uniform vec2 depthMapSize;
    
    uniform vec4 testViewRect;
    
    vec4 main(vec2 fragCoord) {
        float viewAspectRatio = viewSize.x / viewSize.y;
        float pictureAspectRatio = originalPictureSize.x / originalPictureSize.y;
        vec4 viewPixel = inputTexture.eval(fragCoord);
    
        // return vec4(1.0, 0.0, 1.0, 1.0);
    
        if (viewAspectRatio < pictureAspectRatio) {
            float originalCoordMultiplier = viewSize.y / originalPictureSize.y;
            float croppedImageWidth = viewSize.y * originalCoordMultiplier;
            float xOffset = (originalPictureSize.x - croppedImageWidth) / 2.0;
            vec2 croppedPixelCoord = vec2(fragCoord.x + xOffset, fragCoord.y) / originalCoordMultiplier;
            vec4 imagePixel = originalPicture.eval(croppedPixelCoord);
    
            float depthCoordMultiplier = viewSize.y / depthMapSize.y;
            float croppedDepthMapWidth = viewSize.y * depthCoordMultiplier;
            float xDepthOffset = (depthMapSize.x - croppedDepthMapWidth) / 2.0;
            vec2 croppedDepthPixelCoord = vec2(fragCoord.x + xDepthOffset, fragCoord.y) / depthCoordMultiplier;
            vec4 depthPixel = depthMap.eval(croppedDepthPixelCoord);
    
            if (depthPixel.x < 0.6 || viewPixel.a == 0.0) {
                return imagePixel;
            } else {
                return viewPixel;
            }
        } else {
    
        }
    
        return vec4(0.0, 1.0, 1.0, 1.0);
    }
""".trimIndent()