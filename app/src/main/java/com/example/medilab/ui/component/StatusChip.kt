package com.example.medilab.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medilab.ui.util.StatusColors

@Composable
fun StatusChip(status: String, modifier: Modifier = Modifier) {
    val color = StatusColors.from(status)
    Text(
        text = status.replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.labelSmall,
        color = color.text,
        modifier = modifier
            .background(color = color.background, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}
