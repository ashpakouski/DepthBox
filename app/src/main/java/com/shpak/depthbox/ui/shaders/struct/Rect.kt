package com.shpak.depthbox.ui.shaders.struct

import android.graphics.RuntimeShader
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot

data class Rect(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    companion object {
        val Zero get() = Rect(0.0f, 0.0f, 0.0f, 0.0f)
    }
}

fun LayoutCoordinates.toViewRect() =
    Rect(
        x = positionInRoot().x,
        y = positionInRoot().y,
        width = size.width.toFloat(),
        height = size.height.toFloat()
    )

fun RuntimeShader.setRectUniform(uniformName: String, rect: Rect) {
    setFloatUniform(
        uniformName, rect.x, rect.y, rect.width, rect.height
    )
}