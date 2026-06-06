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
import com.example.medilab.repository.UserRepository
import com.example.medilab.ui.navigation.Route
import com.example.medilab.util.Constants
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: (route: String) -> Unit) {
    val authRepo = AuthRepository()
    val userRepo = UserRepository()
    LaunchedEffect(Unit) {
        delay(800)
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            onNavigate(Route.Onboarding1.path)
            return@LaunchedEffect
        }
        userRepo.getUser(uid) { user ->
            val target = when (user?.role) {
                Constants.ROLE_ADMIN, Constants.ROLE_PETUGAS, Constants.ROLE_DOKTER -> Route.StaffRoot.path
                Constants.ROLE_PASIEN -> Route.PatientRoot.path
                else -> Route.Login.path
            }
            onNavigate(target)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("MediLab", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(24.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
        }
    }
}
