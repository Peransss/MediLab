package com.example.medilab.ui.screen.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.OnboardingPage
import com.example.medilab.ui.theme.Spacing

@Composable
fun OnboardingScreen(
    title: String,
    description: String,
    page: Int,
    totalPages: Int,
    illustration: @Composable () -> Unit,
    buttonText: String,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onSkip) { Text("Lewati") }
            }
            OnboardingPage(
                title = title,
                description = description,
                page = page,
                totalPages = totalPages,
                content = illustration
            )
            Spacer(Modifier.height(Spacing.lg))
            MediLabButton(text = buttonText, onClick = onNext, modifier = Modifier.padding(horizontal = Spacing.lg))
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}
