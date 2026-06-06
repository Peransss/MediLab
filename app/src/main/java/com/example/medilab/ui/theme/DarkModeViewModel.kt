package com.example.medilab.ui.theme

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

val Application.darkModeDataStore: DataStore<Preferences> by preferencesDataStore(name = "dark_mode_prefs")

class DarkModeViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.darkModeDataStore

    val isDarkMode: StateFlow<Boolean> = dataStore.data
        .map { it[KEY_DARK_MODE] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun toggle() {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_DARK_MODE] = !(prefs[KEY_DARK_MODE] ?: false)
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_DARK_MODE] = enabled
            }
        }
    }

    companion object {
        private val KEY_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }
}
