package com.example.medilab.staff.laporan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Laporan
import com.example.medilab.repository.LaporanRepository

class LaporanViewModel : ViewModel() {
    private val repo = LaporanRepository()

    private val _laporanList = MutableLiveData<List<Laporan>>()
    val laporanList: LiveData<List<Laporan>> = _laporanList

    private val _currentLaporan = MutableLiveData<Laporan?>()
    val currentLaporan: LiveData<Laporan?> = _currentLaporan

    private var currentStatus = "baru"

    fun loadByStatus(status: String) {
        currentStatus = status
        repo.getByStatus(status) { _laporanList.value = it }
    }

    fun loadById(id: String) {
        repo.getById(id) { _currentLaporan.value = it }
    }

    fun updateStatus(id: String, status: String, adminId: String = "") {
        repo.updateStatus(id, status, adminId) { if (it) loadByStatus(currentStatus) }
    }

    fun updateLaporan(id: String, data: Map<String, Any>) {
        repo.update(id, data) { if (it) loadById(id) }
    }
}
