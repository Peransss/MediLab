package com.example.medilab.ui.screen.onboarding

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.illustration.NotificationIllustration
import com.example.medilab.ui.navigation.SharedNavigationViewModel

@Composable
fun Onboarding3Screen(sharedNavigationViewModel: SharedNavigationViewModel = viewModel()) {
    OnboardingScreen(
        title = "Pantau Progress Real-time",
        description = "Dapatkan notifikasi setiap ada update pada laporan hasil lab Anda.",
        page = 2,
        totalPages = 3,
        illustration = { NotificationIllustration() },
        buttonText = "Mulai",
        sharedNavigationViewModel = sharedNavigationViewModel
    )
}
