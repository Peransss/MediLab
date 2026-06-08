package com.example.medilab.ui.screen.onboarding

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.illustration.MedicalRecordIllustration
import com.example.medilab.ui.navigation.SharedNavigationViewModel

@Composable
fun Onboarding2Screen(sharedNavigationViewModel: SharedNavigationViewModel = viewModel()) {
    OnboardingScreen(
        title = "Akses Hasil Lab dengan Mudah",
        description = "Lihat hasil pemeriksaan, diagnosa, dan resep obat dari genggaman Anda.",
        page = 1,
        totalPages = 3,
        illustration = { MedicalRecordIllustration() },
        buttonText = "Lanjut",
        sharedNavigationViewModel = sharedNavigationViewModel
    )
}
