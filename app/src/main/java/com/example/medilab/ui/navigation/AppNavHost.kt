package com.example.medilab.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.UserRepository
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.screen.SplashScreen
import com.example.medilab.ui.screen.auth.ForgotPasswordScreen
import com.example.medilab.ui.screen.auth.LoginScreen
import com.example.medilab.ui.screen.auth.RegisterScreen
import com.example.medilab.ui.screen.onboarding.OnboardingRootScreen
import com.example.medilab.ui.screen.patient.laporan.LaporanDetailScreen
import com.example.medilab.ui.screen.patient.PatientRootScreen
import com.example.medilab.ui.screen.staff.StaffRootScreen
import com.example.medilab.ui.screen.staff.laporan.StaffLaporanDetailScreen
import com.example.medilab.ui.theme.DarkModeViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    sharedNavigationViewModel: SharedNavigationViewModel = viewModel(),
    darkModeViewModel: DarkModeViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        sharedNavigationViewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route) {
                        event.popUpTo?.let { popUpTo(it) { inclusive = event.inclusive } }
                        launchSingleTop = event.singleTop
                    }
                }
                is NavigationEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Route.Splash.path
    ) {
        composable(Route.Splash.path) {
            SplashScreen(sharedNavigationViewModel = sharedNavigationViewModel)
        }

        composable(Route.Onboarding1.path) {
            OnboardingRootScreen(sharedNavigationViewModel = sharedNavigationViewModel)
        }

        composable(Route.Login.path) {
            LoginScreen(sharedNavigationViewModel = sharedNavigationViewModel)
        }
        composable(Route.Register.path) {
            RegisterScreen(sharedNavigationViewModel = sharedNavigationViewModel)
        }
        composable(Route.ForgotPassword.path) {
            ForgotPasswordScreen(sharedNavigationViewModel = sharedNavigationViewModel)
        }

        composable(Route.StaffRoot.path) {
            StaffRootScreen(rootNavController = navController, darkModeViewModel = darkModeViewModel)
        }
        composable(
            route = Route.StaffLaporanDetail.path,
            arguments = listOf(navArgument("laporanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("laporanId") ?: ""
            StaffLaporanDetailRoute(
                laporanId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.PatientRoot.path) {
            PatientRootScreen(rootNavController = navController, darkModeViewModel = darkModeViewModel)
        }
        composable(
            route = Route.LaporanDetail.path,
            arguments = listOf(navArgument("laporanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("laporanId") ?: ""
            LaporanDetailScreen(laporanId = id, onBack = { navController.popBackStack() })
        }
    }
}

@Composable
private fun StaffLaporanDetailRoute(
    laporanId: String,
    onBack: () -> Unit
) {
    val authRepo = remember { AuthRepository() }
    val userRepo = remember { UserRepository() }
    var userRole by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            error = true
            isLoading = false
            return@LaunchedEffect
        }
        userRepo.getUser(uid) { user ->
            userRole = user?.role ?: ""
            isLoading = false
            if (user == null) error = true
        }
    }

    when {
        isLoading -> LoadingState()
        error -> ErrorState(
            message = "Gagal memuat data user",
            onRetry = { }
        )
        else -> StaffLaporanDetailScreen(
            laporanId = laporanId,
            onBack = onBack,
            userRole = userRole ?: ""
        )
    }
}
