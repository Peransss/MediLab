package com.example.medilab.ui.screen.patient.hasil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.model.Laporan
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientHasilViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val laporanRepo = LaporanRepository()
    private val _uiState = MutableStateFlow<UiState<List<Laporan>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Laporan>>> = _uiState.asStateFlow()

    fun load() {
        _uiState.value = UiState.Loading
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            _uiState.value = UiState.Empty
            return
        }
        viewModelScope.launch {
            laporanRepo.getByStatus(Constants.STATUS_SELESAI) { allSelesai ->
                val mine = allSelesai.filter { it.pasienId == uid }.sortedByDescending { it.createdAt }
                _uiState.value = if (mine.isEmpty()) UiState.Empty else UiState.Success(mine)
            }
        }
    }
}
