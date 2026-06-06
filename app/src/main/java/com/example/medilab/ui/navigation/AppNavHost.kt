package com.example.medilab.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medilab.ui.screen.SplashScreen
import com.example.medilab.util.Constants

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash.path
    ) {
        composable(Route.Splash.path) {
            SplashScreen()
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
        composable(Route.StaffHome.path) { StaffHomeScreen() }
        composable(Route.StaffManage.path) { StaffManageScreen() }
        composable(Route.StaffLaporan.path) { StaffLaporanScreen() }
        composable(Route.StaffNotifikasi.path) { StaffNotifikasiScreen() }
        composable(Route.StaffProfile.path) {
            StaffProfileScreen(onLogout = {
                navController.navigate(Route.Login.path) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
        composable(
            route = Route.StaffLaporanDetail.path,
            arguments = listOf(navArgument("laporanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("laporanId") ?: ""
            StaffLaporanDetailScreen(laporanId = id, onBack = { navController.popBackStack() })
        }

        composable(Route.PatientRoot.path) {
            PatientRootScreen(rootNavController = navController)
        }
        composable(Route.PatientHome.path) { PatientHomeScreen() }
        composable(Route.PatientHasil.path) { PatientHasilScreen() }
        composable(Route.PatientRiwayat.path) { PatientRiwayatScreen() }
        composable(Route.PatientNotifikasi.path) { PatientNotifikasiScreen() }
        composable(Route.PatientProfile.path) {
            PatientProfileScreen(onLogout = {
                navController.navigate(Route.Login.path) {
                    popUpTo(0) { inclusive = true }
                }
            })
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
