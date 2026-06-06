package com.example.medilab.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medilab.ui.screen.SplashScreen
import com.example.medilab.ui.screen.auth.ForgotPasswordScreen
import com.example.medilab.ui.screen.auth.LoginScreen
import com.example.medilab.ui.screen.auth.RegisterScreen
import com.example.medilab.ui.screen.onboarding.Onboarding1Screen
import com.example.medilab.ui.screen.onboarding.Onboarding2Screen
import com.example.medilab.ui.screen.onboarding.Onboarding3Screen
import com.example.medilab.ui.screen.patient.laporan.LaporanDetailScreen
import com.example.medilab.ui.screen.patient.PatientRootScreen
import com.example.medilab.ui.screen.staff.StaffLaporanDetailScreen
import com.example.medilab.ui.screen.staff.StaffRootScreen
import com.example.medilab.util.Constants

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash.path
    ) {
        composable(Route.Splash.path) {
            SplashScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable(Route.Onboarding1.path) {
            Onboarding1Screen(
                onNext = { navController.navigate(Route.Onboarding2.path) },
                onSkip = { navController.navigate(Route.Login.path) }
            )
        }
        composable(Route.Onboarding2.path) {
            Onboarding2Screen(
                onNext = { navController.navigate(Route.Onboarding3.path) },
                onSkip = { navController.navigate(Route.Login.path) }
            )
        }
        composable(Route.Onboarding3.path) {
            Onboarding3Screen(
                onNext = { navController.navigate(Route.Login.path) },
                onSkip = { navController.navigate(Route.Login.path) }
            )
        }

        composable(Route.Login.path) {
            LoginScreen(
                onLoginSuccess = { role -> navigateByRole(navController, role) },
                onRegisterClick = { navController.navigate(Route.Register.path) },
                onForgotClick = { navController.navigate(Route.ForgotPassword.path) }
            )
        }
        composable(Route.Register.path) {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Route.ForgotPassword.path) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.StaffRoot.path) {
            StaffRootScreen(rootNavController = navController)
        }
        composable(
            route = Route.StaffLaporanDetail.path,
            arguments = listOf(navArgument("laporanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("laporanId") ?: ""
            StaffLaporanDetailScreen(
                laporanId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.PatientRoot.path) {
            PatientRootScreen(rootNavController = navController)
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

private fun navigateByRole(navController: NavHostController, role: String) {
    val target = when (role) {
        Constants.ROLE_ADMIN, Constants.ROLE_PETUGAS, Constants.ROLE_DOKTER -> Route.StaffRoot.path
        Constants.ROLE_PASIEN -> Route.PatientRoot.path
        else -> Route.Login.path
    }
    navController.navigate(target) {
        popUpTo(0) { inclusive = true }
    }
}
