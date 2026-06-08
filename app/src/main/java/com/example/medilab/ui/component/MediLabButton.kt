package com.example.medilab.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class MediLabButtonVariant { FILLED, OUTLINED, TEXT, CTA }

val CtaGreen = Color(0xFF059669)
val CtaGreenOn = Color(0xFFFFFFFF)

@Composable
fun MediLabButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MediLabButtonVariant = MediLabButtonVariant.FILLED,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    fullWidth: Boolean = true
) {
    val buttonModifier = if (fullWidth) modifier.fillMaxWidth() else modifier
    val content: @Composable () -> Unit = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = if (variant == MediLabButtonVariant.FILLED) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
            } else if (leadingIcon != null) {
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
            enabled = enabled && !isLoading,
            shape = MaterialTheme.shapes.small, // 12 dp
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) { content() }
        MediLabButtonVariant.OUTLINED -> OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier.height(56.dp),
            enabled = enabled && !isLoading,
            shape = MaterialTheme.shapes.small, // 12 dp
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) { content() }
        MediLabButtonVariant.TEXT -> TextButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled && !isLoading,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) { content() }
        MediLabButtonVariant.CTA -> Button(
            onClick = onClick,
            modifier = buttonModifier.height(56.dp),
            enabled = enabled && !isLoading,
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CtaGreen,
                contentColor = CtaGreenOn
            )
        ) { content() }
    }
}
