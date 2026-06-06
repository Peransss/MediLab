package com.example.medilab.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(onLoginSuccess: (role: String) -> Unit, onRegisterClick: () -> Unit, onForgotClick: () -> Unit) {
    PlaceholderScreen(text = "Login - Coming Soon")
}

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBack: () -> Unit) {
    PlaceholderScreen(text = "Register - Coming Soon")
}

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    PlaceholderScreen(text = "Forgot Password - Coming Soon")
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
