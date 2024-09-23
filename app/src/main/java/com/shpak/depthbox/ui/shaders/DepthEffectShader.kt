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
    
    vec2 translatePixelCoordPortrait(vec2 contentSize, vec2 parentSize, vec2 fragCoord) {
        float contentCoordMultiplier = contentSize.y / parentSize.y;
        float contentOffsetX = (contentSize.x - parentSize.x * contentCoordMultiplier) / 2.0;

        return vec2(
            fragCoord.x * contentCoordMultiplier + contentOffsetX,
            fragCoord.y * contentCoordMultiplier
        );
    }

    vec4 main(vec2 fragCoord) {
        vec4 inputPixel = inputTexture.eval(fragCoord);

        float viewportAspectRatio = viewportSize.x / viewportSize.y;
        float pictureAspectRatio = pictureSize.x / pictureSize.y;

        if (viewportAspectRatio < pictureAspectRatio) {
            vec4 picturePixel = picture.eval(translatePixelCoordPortrait(pictureSize, viewportSize, fragCoord));
            vec4 depthPixel = depthMap.eval(translatePixelCoordPortrait(depthMapSize, viewportSize, fragCoord));

            if (depthPixel.x > 0.35 && inputPixel.a != 0.0) {
                return inputTexture.eval(fragCoord);
            } else {
                return picturePixel;
            }
        } else {
        
        }
    
        return vec4(0.0, 1.0, 1.0, 1.0);
    }
""".trimIndent()