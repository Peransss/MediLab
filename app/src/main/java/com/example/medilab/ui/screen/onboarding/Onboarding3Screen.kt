package com.example.medilab.ui.screen.onboarding

import androidx.compose.runtime.Composable
import com.example.medilab.ui.illustration.NotificationIllustration

@Composable
fun Onboarding3Screen(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingScreen(
        title = "Pantau Progress Real-time",
        description = "Dapatkan notifikasi setiap ada update pada laporan hasil lab Anda.",
        page = 2,
        totalPages = 3,
        illustration = { NotificationIllustration() },
        buttonText = "Mulai",
        onNext = onNext,
        onSkip = onSkip
    )
}
