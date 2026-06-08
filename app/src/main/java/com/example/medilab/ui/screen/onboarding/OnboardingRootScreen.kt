package com.example.medilab.ui.screen.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.R
import com.example.medilab.ui.navigation.SharedNavigationViewModel

@Composable
fun OnboardingRootScreen(sharedNavigationViewModel: SharedNavigationViewModel = viewModel()) {
    val onboardingPages = listOf(
        OnboardingPageContent(
            title = "Selamat Datang di MediLab",
            description = "Sistem informasi laboratorium medis yang modern dan mudah digunakan.",
            illustration = {
                Image(
                    painter = painterResource(R.drawable.vector_1),
                    contentDescription = null,
                    modifier = Modifier.size(240.dp)
                )
            }
        ),
        OnboardingPageContent(
            title = "Akses Hasil Lab dengan Mudah",
            description = "Lihat hasil pemeriksaan, diagnosa, dan resep obat dari genggaman Anda.",
            illustration = {
                Image(
                    painter = painterResource(R.drawable.vector_2),
                    contentDescription = null,
                    modifier = Modifier.size(240.dp)
                )
            }
        ),
        OnboardingPageContent(
            title = "Pantau Progress Real-time",
            description = "Dapatkan notifikasi setiap ada update pada laporan hasil lab Anda.",
            illustration = {
                Image(
                    painter = painterResource(R.drawable.vector_3),
                    contentDescription = null,
                    modifier = Modifier.size(240.dp)
                )
            }
        )
    )

    OnboardingScreen(
        pages = onboardingPages,
        sharedNavigationViewModel = sharedNavigationViewModel
    )
}
