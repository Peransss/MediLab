package com.example.medilab.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medilab.repository.AuthRepository
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: (route: String) -> Unit) {
    val authRepo = AuthRepository()
    LaunchedEffect(Unit) {
        delay(800)
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            onNavigate(Route_Splash_To_Onboarding)
        } else {
            onNavigate(Route_Splash_To_Login)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("MediLab", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(24.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
        }
    }
}

const val Route_Splash_To_Onboarding = "onboarding/1"
const val Route_Splash_To_Login = "auth/login"
