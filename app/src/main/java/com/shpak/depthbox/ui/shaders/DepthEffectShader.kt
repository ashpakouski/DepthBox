package com.shpak.depthbox.ui.shaders

import org.intellij.lang.annotations.Language

/**
 * This string is annotated as "GLSL" just because is allows to use some syntax highlighting
 * enabled by "GLSL" plugin. Most of GLSL functionality can't be applied to AGSL, so it's
 * up to you to use this lifehack.
 */

@Language("GLSL")
val DepthEffectShader = """
    uniform shader inputTexture;
    uniform shader picture;
    uniform shader depthMap;
    
    uniform vec2 viewportSize;
    uniform vec2 pictureSize;
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
        vec2 pictureCoordTranslated = translatePixelCoord(pictureSize, viewportSize, fragCoord);
        vec2 depthCoordTranslated = translatePixelCoord(depthMapSize, viewportSize, fragCoord);
    
        vec4 inputPixel = inputTexture.eval(fragCoord);
        vec4 picturePixel = picture.eval(pictureCoordTranslated);
        vec4 depthPixel = depthMap.eval(depthCoordTranslated);
    
        if (depthPixel.x > 0.65 && inputPixel.a != 0.0) {
            return inputTexture.eval(fragCoord);
        } else {
            return picturePixel;
        }
    }
""".trimIndent()