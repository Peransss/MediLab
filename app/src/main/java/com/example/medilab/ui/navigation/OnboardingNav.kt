package com.example.medilab.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Onboarding1Screen(onNext: () -> Unit, onSkip: () -> Unit) {
    PlaceholderScreen(text = "Onboarding 1 - Coming Soon")
}

@Composable
fun Onboarding2Screen(onNext: () -> Unit, onSkip: () -> Unit) {
    PlaceholderScreen(text = "Onboarding 2 - Coming Soon")
}

@Composable
fun Onboarding3Screen(onNext: () -> Unit, onSkip: () -> Unit) {
    PlaceholderScreen(text = "Onboarding 3 - Coming Soon")
}

@Composable
private fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
    }
}
