package com.example.medilab.ui.navigation

sealed class NavigationEvent {
    data class NavigateTo(val route: String, val popUpTo: String? = null, val inclusive: Boolean = false, val singleTop: Boolean = false) : NavigationEvent()
    object PopBackStack : NavigationEvent()
    // Add other navigation events as needed, e.g., PopUpTo, NavigateAndPopUp
}
