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
    
    vec2 translatePixelCoordPortrait(vec2 contentSize, vec2 viewSize, vec2 fragCoord) {
        float viewAspectRatio = viewSize.x / viewSize.y;
        float contentAspectRatio = contentSize.x / contentSize.y;
        
        float contentCoordMultiplierX =  contentSize.y / viewSize.y;
        float contentOffsetX = (contentSize.x - viewSize.x * contentCoordMultiplierX) / 2.0;

        return vec2(
            fragCoord.x * contentCoordMultiplierX + contentOffsetX,
            fragCoord.y * contentCoordMultiplierX
        );
    }
    
    vec2 translatePixelCoordLandscape(vec2 contentSize, vec2 viewSize, vec2 fragCoord) {
        float contentCoordMultiplierY = contentSize.x / viewSize.x;
        float contentOffsetY = (contentSize.y - viewSize.y * contentCoordMultiplierY) / 2.0;

        return vec2(
            fragCoord.x * contentCoordMultiplierY,
            fragCoord.y * contentCoordMultiplierY + contentOffsetY
        );
    }

    vec4 main(vec2 fragCoord) {
        vec4 inputPixel = inputTexture.eval(fragCoord);

        float viewportAspectRatio = viewportSize.x / viewportSize.y;
        float pictureAspectRatio = pictureSize.x / pictureSize.y;
        
        vec2 pictureCoordTranslated;
        vec2 depthCoordTranslated;

        if (viewportAspectRatio < pictureAspectRatio) {
            pictureCoordTranslated = translatePixelCoordPortrait(pictureSize, viewportSize, fragCoord);
            depthCoordTranslated = translatePixelCoordPortrait(depthMapSize, viewportSize, fragCoord);            
        } else {
            pictureCoordTranslated = translatePixelCoordLandscape(pictureSize, viewportSize, fragCoord);
            depthCoordTranslated = translatePixelCoordLandscape(depthMapSize, viewportSize, fragCoord);  
        }

        vec4 picturePixel = picture.eval(pictureCoordTranslated);
        vec4 depthPixel = depthMap.eval(depthCoordTranslated);
        
        if (depthPixel.x > 0.65 && inputPixel.a != 0.0) {
            return inputTexture.eval(fragCoord);
        } else {
            return picturePixel;
        }
    }
""".trimIndent()