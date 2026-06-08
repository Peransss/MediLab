package com.example.medilab.ui.screen.patient

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.medilab.ui.component.BottomNavItems
import com.example.medilab.ui.component.MediLabBottomBar
import com.example.medilab.ui.navigation.Route
import com.example.medilab.ui.screen.patient.hasil.PatientBookingScreen
import com.example.medilab.ui.screen.patient.hasil.PatientHasilScreen
import com.example.medilab.ui.screen.patient.home.PatientHomeScreen
import com.example.medilab.ui.screen.patient.notifikasi.PatientNotifikasiScreen
import com.example.medilab.ui.screen.patient.profile.PatientProfileScreen
import com.example.medilab.ui.screen.patient.riwayat.PatientRiwayatScreen
import com.example.medilab.ui.theme.DarkModeViewModel

@Composable
fun PatientRootScreen(rootNavController: NavHostController, darkModeViewModel: DarkModeViewModel) {
    val isDark by darkModeViewModel.isDarkMode.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Route.PatientHome.path

    Scaffold(
        bottomBar = {
            MediLabBottomBar(
                items = BottomNavItems.Patient,
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.PatientHome.path,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            composable(Route.PatientHome.path) {
                PatientHomeScreen(
                    onLaporanClick = { id -> rootNavController.navigate(Route.LaporanDetail.build(id)) },
                    onNotificationClick = { navController.navigate(Route.PatientNotifikasi.path) },
                    onProfileClick = { navController.navigate(Route.PatientProfile.path) },
                    onSeeAllLaporanClick = { navController.navigate(Route.PatientHasil.path) }
                )
            }
            composable(Route.PatientHasil.path) {
                PatientHasilScreen(
                    onLaporanClick = { id -> rootNavController.navigate(Route.LaporanDetail.build(id)) },
                    onBookingClick = { navController.navigate(Route.PatientBooking.path) }
                )
            }
            composable(Route.PatientBooking.path) {
                PatientBookingScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }
            composable(Route.PatientRiwayat.path) {
                PatientRiwayatScreen(onItemClick = { id -> rootNavController.navigate(Route.LaporanDetail.build(id)) })
            }
            composable(Route.PatientNotifikasi.path) { PatientNotifikasiScreen() }
            composable(Route.PatientProfile.path) {
                PatientProfileScreen(
                    onLogout = {
                        rootNavController.navigate(Route.Login.path) { popUpTo(0) { inclusive = true } }
                    },
                    isDark = isDark,
                    onToggleDark = { darkModeViewModel.toggle() }
                )
            }
        }
    }
}