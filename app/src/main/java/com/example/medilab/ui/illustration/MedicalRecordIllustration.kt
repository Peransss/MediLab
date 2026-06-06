package com.example.medilab.ui.illustration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun MedicalRecordIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val primary = Color(0xFF5B5FED)
        val primaryContainer = Color(0xFFE8E8FF)
        val white = Color(0xFFFFFFFF)

        drawCircle(color = primaryContainer, radius = size.minDimension / 2)

        drawRect(
            color = white,
            topLeft = Offset(size.width * 0.25f, size.height * 0.2f),
            size = Size(size.width * 0.5f, size.height * 0.6f)
        )
        for (i in 0..3) {
            drawRect(
                color = primary.copy(alpha = 0.3f),
                topLeft = Offset(size.width * 0.32f, size.height * (0.32f + i * 0.08f)),
                size = Size(size.width * 0.36f, size.height * 0.02f)
            )
        }
        drawRect(color = Color(0xFFEF4444), topLeft = Offset(size.width * 0.42f, size.height * 0.4f), size = Size(size.width * 0.16f, size.height * 0.04f))
        drawRect(color = Color(0xFFEF4444), topLeft = Offset(size.width * 0.47f, size.height * 0.34f), size = Size(size.width * 0.06f, size.height * 0.16f))
    }
}
