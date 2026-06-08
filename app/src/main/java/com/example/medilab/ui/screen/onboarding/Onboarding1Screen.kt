package com.example.medilab.ui.screen.onboarding

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.illustration.DoctorIllustration
import com.example.medilab.ui.navigation.SharedNavigationViewModel

@Composable
fun Onboarding1Screen(sharedNavigationViewModel: SharedNavigationViewModel = viewModel()) {
    OnboardingScreen(
        title = "Selamat Datang di MediLab",
        description = "Sistem informasi laboratorium medis yang modern dan mudah digunakan.",
        page = 0,
        totalPages = 3,
        illustration = { DoctorIllustration() },
        buttonText = "Lanjut",
        sharedNavigationViewModel = sharedNavigationViewModel
    )
}
