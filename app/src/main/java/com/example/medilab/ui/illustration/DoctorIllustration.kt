package com.example.medilab.ui.illustration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun DoctorIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val white = Color(0xFFFFFFFF)
        val primary = Color(0xFF5B5FED)
        val skin = Color(0xFFFFD9B3)
        val coat = Color(0xFFFFFFFF)

        drawCircle(color = primary.copy(alpha = 0.1f), radius = size.minDimension / 2)

        drawRect(
            color = coat,
            topLeft = Offset(size.width * 0.25f, size.height * 0.45f),
            size = Size(size.width * 0.5f, size.height * 0.4f)
        )

        drawCircle(color = skin, radius = size.minDimension * 0.12f, center = Offset(size.width / 2, size.height * 0.32f))

        drawArc(
            color = Color(0xFF3D2817),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(size.width * 0.38f, size.height * 0.18f),
            size = Size(size.width * 0.24f, size.height * 0.18f)
        )

        drawCircle(color = Color(0xFF1F2937), radius = size.minDimension * 0.04f, center = Offset(size.width * 0.38f, size.height * 0.6f))
    }
}
