package com.example.medilab.ui.screen.patient.hasil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.MediLabApp
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.repository.LocalLaporanRepository
import com.example.medilab.repository.AuthRepository
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientHasilViewModel : ViewModel() {
    private val app by lazy { MediLabApp.instance }
    private val authRepo = AuthRepository()
    private val localLaporanRepo = LocalLaporanRepository(app.database, app.syncManager)
    private val _uiState = MutableStateFlow<UiState<List<LaporanEntity>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<LaporanEntity>>> = _uiState.asStateFlow()

    fun load() {
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            _uiState.value = UiState.Empty
            return
        }
        viewModelScope.launch {
            localLaporanRepo.getByPasienIdAndStatus(uid, Constants.STATUS_SELESAI).collect { mine ->
                _uiState.value = if (mine.isEmpty()) UiState.Empty else UiState.Success(mine)
            }
        }
    }
}
