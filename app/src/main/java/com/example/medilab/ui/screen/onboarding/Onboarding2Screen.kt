package com.example.medilab.ui.screen.onboarding

import androidx.compose.runtime.Composable
import com.example.medilab.ui.illustration.MedicalRecordIllustration

@Composable
fun Onboarding2Screen(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingScreen(
        title = "Akses Hasil Lab dengan Mudah",
        description = "Lihat hasil pemeriksaan, diagnosa, dan resep obat dari genggaman Anda.",
        page = 1,
        totalPages = 3,
        illustration = { MedicalRecordIllustration() },
        buttonText = "Lanjut",
        onNext = onNext,
        onSkip = onSkip
    )
}
