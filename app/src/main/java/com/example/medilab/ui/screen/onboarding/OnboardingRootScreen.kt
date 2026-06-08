package com.example.medilab.ui.screen.onboarding

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.illustration.DoctorIllustration
import com.example.medilab.ui.illustration.MedicalRecordIllustration
import com.example.medilab.ui.illustration.NotificationIllustration
import com.example.medilab.ui.navigation.SharedNavigationViewModel

@Composable
fun OnboardingRootScreen(sharedNavigationViewModel: SharedNavigationViewModel = viewModel()) {
    val onboardingPages = listOf(
        OnboardingPageContent(
            title = "Selamat Datang di MediLab",
            description = "Sistem informasi laboratorium medis yang modern dan mudah digunakan.",
            illustration = { DoctorIllustration() }
        ),
        OnboardingPageContent(
            title = "Akses Hasil Lab dengan Mudah",
            description = "Lihat hasil pemeriksaan, diagnosa, dan resep obat dari genggaman Anda.",
            illustration = { MedicalRecordIllustration() }
        ),
        OnboardingPageContent(
            title = "Pantau Progress Real-time",
            description = "Dapatkan notifikasi setiap ada update pada laporan hasil lab Anda.",
            illustration = { NotificationIllustration() }
        )
    )

    OnboardingScreen(
        pages = onboardingPages,
        sharedNavigationViewModel = sharedNavigationViewModel
    )
}
