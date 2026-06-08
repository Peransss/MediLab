package com.example.medilab.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SharedNavigationViewModel : ViewModel() {

    private val _navigationEvents = Channel<NavigationEvent>()
    val navigationEvents = _navigationEvents.receiveAsFlow()

    fun navigateTo(route: String, popUpTo: String? = null, inclusive: Boolean = false, singleTop: Boolean = false) {
        viewModelScope.launch {
            _navigationEvents.send(NavigationEvent.NavigateTo(route, popUpTo, inclusive, singleTop))
        }
    }

    fun popBackStack() {
        viewModelScope.launch {
            _navigationEvents.send(NavigationEvent.PopBackStack)
        }
    }
}
