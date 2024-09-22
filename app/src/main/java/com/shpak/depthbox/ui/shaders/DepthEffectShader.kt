package com.shpak.depthbox.ui.shaders

import org.intellij.lang.annotations.Language

/**
 * This string is annotated as "GLSL" just because is allows to use some syntax highlighting
 * enabled by "GLSL" plugin. Most of GLSL functionality can't be applied to AGSL, so it's
 * up to you to use this lifehack.
 */

@Language("GLSL")
val DepthEffectShader = """
    struct Rect {
        float x;
        float y;
        float width;
        float height;
    };

    uniform shader inputTexture;
    uniform shader depthMap;
    uniform shader originalPicture;

    uniform vec2 originalPictureSize;
    uniform vec2 depthMapSize;
    
    uniform vec4 testViewRect;

    uniform vec4 rootViewRect; // x, y, width, height

    Rect parseRect(vec4 coordinates) {
        Rect vc;
        vc.x = coordinates.x;
        vc.y = coordinates.y;
        vc.width = coordinates.z;
        vc.height = coordinates.w;
        return vc;
    }
    
    bool isCoordInRect(vec2 coord, Rect rect) {
        return coord.x >= rect.x && coord.x < rect.x + rect.width
                && coord.y >= rect.y && coord.y < rect.y + rect.height;
    }

    vec4 main(vec2 fragCoord) {
        Rect rootRect = parseRect(rootViewRect);
        
        vec4 viewPixel = inputTexture.eval(fragCoord);

        if (!isCoordInRect(fragCoord, rootRect)) {
            return viewPixel;
        }

        float viewAspectRatio = rootRect.width / rootRect.height;
        float pictureAspectRatio = originalPictureSize.x / originalPictureSize.y;
    
        // return viewPixel;
    
        if (viewAspectRatio < pictureAspectRatio) {
            float originalCoordMultiplier = rootRect.height / originalPictureSize.y;
            float croppedImageWidth = rootRect.height * originalCoordMultiplier;
            float xOffset = (originalPictureSize.x - croppedImageWidth) / 2.0;
            vec2 croppedPixelCoord = vec2(fragCoord.x + xOffset, fragCoord.y) / originalCoordMultiplier;
            vec4 imagePixel = originalPicture.eval(croppedPixelCoord);

            float depthCoordMultiplier = rootRect.height / depthMapSize.y;
            float croppedDepthMapWidth = rootRect.height * depthCoordMultiplier;
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