package com.example.medilab.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class MediLabButtonVariant { FILLED, OUTLINED, TEXT }

@Composable
fun MediLabButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MediLabButtonVariant = MediLabButtonVariant.FILLED,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    fullWidth: Boolean = true
) {
    val buttonModifier = if (fullWidth) modifier.fillMaxWidth() else modifier
    val content: @Composable () -> Unit = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null)
                Spacer(Modifier.width(8.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
    when (variant) {
        MediLabButtonVariant.FILLED -> Button(
            onClick = onClick,
            modifier = buttonModifier.height(56.dp),
            enabled = enabled,
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) { content() }
        MediLabButtonVariant.OUTLINED -> OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier.height(56.dp),
            enabled = enabled,
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) { content() }
        MediLabButtonVariant.TEXT -> TextButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled
        ) { content() }
    }
}
