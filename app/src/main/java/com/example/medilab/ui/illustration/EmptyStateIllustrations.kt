package com.example.medilab.ui.illustration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun EmptyLabIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val primaryContainer = Color(0xFFE8E8FF)
        val primary = Color(0xFF5B5FED)
        drawCircle(color = primaryContainer, radius = size.minDimension / 2)
        drawRect(color = primary.copy(alpha = 0.5f), topLeft = Offset(size.width * 0.3f, size.height * 0.3f), size = Size(size.width * 0.4f, size.height * 0.4f))
    }
}

@Composable
fun EmptyNotificationIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val primaryContainer = Color(0xFFE8E8FF)
        val primary = Color(0xFF5B5FED)
        drawCircle(color = primaryContainer, radius = size.minDimension / 2)
        drawCircle(color = primary.copy(alpha = 0.5f), radius = size.minDimension * 0.15f, center = Offset(size.width * 0.5f, size.height * 0.4f))
    }
}

@Composable
fun EmptyHistoryIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val primaryContainer = Color(0xFFE8E8FF)
        val primary = Color(0xFF5B5FED)
        drawCircle(color = primaryContainer, radius = size.minDimension / 2)
        drawRect(color = primary.copy(alpha = 0.5f), topLeft = Offset(size.width * 0.3f, size.height * 0.25f), size = Size(size.width * 0.4f, size.height * 0.5f))
    }
}
