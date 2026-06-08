package com.example.medilab.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.UserRepository
import com.example.medilab.ui.illustration.EmptyLabIllustration
import com.example.medilab.ui.navigation.Route
import com.example.medilab.ui.navigation.SharedNavigationViewModel
import com.example.medilab.util.Constants
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(sharedNavigationViewModel: SharedNavigationViewModel = viewModel()) {
    val authRepo = AuthRepository()
    val userRepo = UserRepository()
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(2500)
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            sharedNavigationViewModel.navigateTo(Route.Onboarding1.path)
            return@LaunchedEffect
        }
        isLoading = true
        val deferred = CompletableDeferred<com.example.medilab.model.User?>()
        userRepo.getUser(uid) { user -> deferred.complete(user) }
        val user = deferred.await()
        isLoading = false
        val target = when (user?.role) {
            Constants.ROLE_ADMIN, Constants.ROLE_PETUGAS, Constants.ROLE_DOKTER -> Route.StaffRoot.path
            Constants.ROLE_PASIEN -> Route.PatientRoot.path
            else -> Route.Login.path
        }
        sharedNavigationViewModel.navigateTo(target, popUpTo = Route.Splash.path, inclusive = true)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Center content
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                EmptyLabIllustration(modifier = Modifier.size(100.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "MediLab",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Sistem informasi laboratorium medis yang modern dan mudah digunakan.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        // Bottom: Page indicator dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), shape = CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), shape = CircleShape)
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
