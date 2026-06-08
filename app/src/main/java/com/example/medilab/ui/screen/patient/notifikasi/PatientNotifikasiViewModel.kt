package com.example.medilab.ui.screen.patient.notifikasi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.MediLabApp
import com.example.medilab.database.entity.NotifikasiEntity
import com.example.medilab.database.repository.LocalNotifikasiRepository
import com.example.medilab.repository.AuthRepository
import com.example.medilab.ui.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientNotifikasiViewModel : ViewModel() {
    private val app = MediLabApp.instance
    private val authRepo = AuthRepository()
    private val localNotifikasiRepo = LocalNotifikasiRepository(app.database, app.syncManager)
    private val _uiState = MutableStateFlow<UiState<List<NotifikasiEntity>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<NotifikasiEntity>>> = _uiState.asStateFlow()

    fun load() {
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            _uiState.value = UiState.Empty
            return
        }
        viewModelScope.launch {
            localNotifikasiRepo.getByUserId(uid).collect { list ->
                _uiState.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
            }
        }
    }

    fun markAsRead(id: String) {
        localNotifikasiRepo.markAsRead(id)
    }
}
