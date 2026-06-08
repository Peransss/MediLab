package com.example.medilab.ui.screen.staff.laporan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.MediLabApp
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.repository.LocalLaporanRepository
import com.example.medilab.repository.AuthRepository
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StaffLaporanViewModel : ViewModel() {
    private val app = MediLabApp.instance
    private val authRepo = AuthRepository()
    private val localLaporanRepo = LocalLaporanRepository(app.database, app.syncManager)

    private val _listState = MutableStateFlow<UiState<List<LaporanEntity>>>(UiState.Loading)
    val listState: StateFlow<UiState<List<LaporanEntity>>> = _listState.asStateFlow()

    private val _detail = MutableStateFlow<LaporanEntity?>(null)
    val detail: StateFlow<LaporanEntity?> = _detail.asStateFlow()

    private var listJob: Job? = null

    fun loadByStatus(status: String) {
        listJob?.cancel()
        _listState.value = UiState.Loading
        listJob = viewModelScope.launch {
            localLaporanRepo.getByStatus(status).collect { list ->
                _listState.value = if (list.isEmpty()) UiState.Empty
                else UiState.Success(list.sortedByDescending { it.createdAt })
            }
        }
    }

    fun loadDetail(id: String) {
        viewModelScope.launch {
            localLaporanRepo.getById(id).collect { l -> _detail.value = l }
        }
    }

    fun updateStatus(laporanId: String, newStatus: String, userRole: String, onDone: () -> Unit) {
        localLaporanRepo.updateStatus(laporanId, newStatus)
        val uid = authRepo.getCurrentUid()
        if (userRole == Constants.ROLE_ADMIN) {
            // update adminId if needed — simplified for now
        }
        loadDetail(laporanId)
        onDone()
    }

    fun saveHasil(laporan: LaporanEntity, onDone: () -> Unit) {
        localLaporanRepo.update(
            id = laporan.id,
            hasilParameter = laporan.hasilParameter,
            diagnosa = laporan.diagnosa,
            resepObat = laporan.resepObat
        )
        loadDetail(laporan.id)
        onDone()
    }
}
