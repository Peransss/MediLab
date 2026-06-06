package com.example.medilab.ui.screen.onboarding

import androidx.compose.runtime.Composable
import com.example.medilab.ui.illustration.DoctorIllustration

@Composable
fun Onboarding1Screen(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingScreen(
        title = "Selamat Datang di MediLab",
        description = "Sistem informasi laboratorium medis yang modern dan mudah digunakan.",
        page = 0,
        totalPages = 3,
        illustration = { DoctorIllustration() },
        buttonText = "Lanjut",
        onNext = onNext,
        onSkip = onSkip
    )
}
