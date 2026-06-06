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
import com.example.medilab.ui.illustration.MedicalRecordIllustration
import com.example.medilab.ui.theme.Spacing

@Composable
fun Onboarding2Screen(onNext: () -> Unit, onSkip: () -> Unit) {
    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onSkip) { Text("Lewati") }
            }
            OnboardingPage(
                title = "Akses Hasil Lab dengan Mudah",
                description = "Lihat hasil pemeriksaan, diagnosa, dan resep obat dari genggaman Anda.",
                page = 1,
                totalPages = 3,
                content = { MedicalRecordIllustration() }
            )
            Spacer(Modifier.height(Spacing.lg))
            MediLabButton(text = "Lanjut", onClick = onNext, modifier = Modifier.padding(horizontal = Spacing.lg))
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}
