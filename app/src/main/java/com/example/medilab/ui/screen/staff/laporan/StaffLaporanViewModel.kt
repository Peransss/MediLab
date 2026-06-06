package com.example.medilab.ui.screen.staff.laporan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.model.Laporan
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.util.Constants
import com.example.medilab.ui.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StaffLaporanViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val laporanRepo = LaporanRepository()

    private val _listState = MutableStateFlow<UiState<List<Laporan>>>(UiState.Loading)
    val listState: StateFlow<UiState<List<Laporan>>> = _listState.asStateFlow()

    private val _detail = MutableStateFlow<Laporan?>(null)
    val detail: StateFlow<Laporan?> = _detail.asStateFlow()

    fun loadByStatus(status: String) {
        _listState.value = UiState.Loading
        viewModelScope.launch {
            laporanRepo.getByStatus(status) { list ->
                _listState.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list.sortedByDescending { it.createdAt })
            }
        }
    }

    fun loadDetail(id: String) {
        viewModelScope.launch {
            laporanRepo.getById(id) { l -> _detail.value = l }
        }
    }

    fun updateStatus(laporanId: String, newStatus: String, userRole: String, onDone: () -> Unit) {
        val uid = authRepo.getCurrentUid()
        val adminId = if (userRole == Constants.ROLE_ADMIN) uid else ""
        laporanRepo.updateStatus(laporanId, newStatus, adminId) { ok ->
            if (ok) loadDetail(laporanId)
            onDone()
        }
    }

    fun saveHasil(laporan: Laporan, onDone: () -> Unit) {
        viewModelScope.launch {
            val data = mapOf(
                "hasilParameter" to laporan.hasilParameter,
                "diagnosa" to laporan.diagnosa,
                "resepObat" to laporan.resepObat,
                "updatedAt" to System.currentTimeMillis()
            )
            laporanRepo.update(laporan.id, data) { ok -> if (ok) loadDetail(laporan.id); onDone() }
        }
    }
}
