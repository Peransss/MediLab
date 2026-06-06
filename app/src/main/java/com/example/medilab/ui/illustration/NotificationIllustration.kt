package com.example.medilab.ui.illustration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun NotificationIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val primary = Color(0xFF5B5FED)
        val primaryContainer = Color(0xFFE8E8FF)
        val white = Color(0xFFFFFFFF)
        val secondary = Color(0xFFFF6B9D)

        drawCircle(color = primaryContainer, radius = size.minDimension / 2)

        drawRect(
            color = primary,
            topLeft = Offset(size.width * 0.3f, size.height * 0.25f),
            size = Size(size.width * 0.4f, size.height * 0.4f)
        )
        drawRect(color = primary, topLeft = Offset(size.width * 0.25f, size.height * 0.55f), size = Size(size.width * 0.5f, size.height * 0.05f))
        drawCircle(color = secondary, radius = size.minDimension * 0.05f, center = Offset(size.width * 0.65f, size.height * 0.3f))
        drawCircle(color = white, radius = size.minDimension * 0.04f, center = Offset(size.width / 2, size.height * 0.7f))
    }
}
