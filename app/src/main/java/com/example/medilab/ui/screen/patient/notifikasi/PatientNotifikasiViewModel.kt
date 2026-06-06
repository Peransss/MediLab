package com.example.medilab.ui.screen.patient.notifikasi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.model.Notifikasi
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.NotifikasiRepository
import com.example.medilab.ui.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientNotifikasiViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val notifikasiRepo = NotifikasiRepository()
    private val _uiState = MutableStateFlow<UiState<List<Notifikasi>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Notifikasi>>> = _uiState.asStateFlow()

    fun load() {
        _uiState.value = UiState.Loading
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            _uiState.value = UiState.Empty
            return
        }
        viewModelScope.launch {
            notifikasiRepo.getByUserId(uid) { list ->
                _uiState.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
            }
        }
    }

    fun markAsRead(id: String) {
        notifikasiRepo.markAsRead(id) { viewModelScope.launch { load() } }
    }
}
