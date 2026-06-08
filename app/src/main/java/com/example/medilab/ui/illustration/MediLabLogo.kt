package com.example.medilab.ui.illustration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform

@Composable
fun MediLabLogo(modifier: Modifier = Modifier) {
    val purpleStart = Color(0xFF6C5CE7)
    val purpleEnd = Color(0xFF4A148C)
    val white = Color.White
    val darkPurple = Color(0xFF522CA6)

    Canvas(modifier = modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val scale = size.minDimension / 512f
        val contentSize = 512f * scale
        val ox = (size.width - contentSize) / 2f
        val oy = (size.height - contentSize) / 2f

        withTransform({
            translate(ox, oy)
            scale(scale, scale)
        }) {
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(purpleStart, purpleEnd),
                    start = Offset.Zero,
                    end = Offset(512f, 512f)
                ),
                topLeft = Offset.Zero,
                size = Size(512f, 512f),
                cornerRadius = CornerRadius(128f, 128f)
            )

            drawCircle(white.copy(alpha = 0.5f), radius = 10f, center = Offset(256f, 90f))
            drawCircle(white.copy(alpha = 0.3f), radius = 6f, center = Offset(285f, 65f))
            drawCircle(white.copy(alpha = 0.4f), radius = 5f, center = Offset(235f, 110f))

            drawRoundRect(
                color = white,
                topLeft = Offset(226f, 130f),
                size = Size(60f, 12f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            val bodyPath = Path().apply {
                moveTo(236f, 142f)
                lineTo(276f, 142f)
                lineTo(276f, 210f)
                lineTo(358f, 335f)
                quadraticTo(358f, 368f, 339f, 368f)
                lineTo(173f, 368f)
                quadraticTo(154f, 368f, 154f, 335f)
                lineTo(236f, 210f)
                close()
            }
            drawPath(bodyPath, white)

            val plusPath = Path().apply {
                moveTo(244f, 255f)
                lineTo(268f, 255f)
                lineTo(268f, 279f)
                lineTo(292f, 279f)
                lineTo(292f, 303f)
                lineTo(268f, 303f)
                lineTo(268f, 327f)
                lineTo(244f, 327f)
                lineTo(244f, 303f)
                lineTo(220f, 303f)
                lineTo(220f, 279f)
                lineTo(244f, 279f)
                close()
            }
            drawPath(plusPath, darkPurple)
        }
    }
}
