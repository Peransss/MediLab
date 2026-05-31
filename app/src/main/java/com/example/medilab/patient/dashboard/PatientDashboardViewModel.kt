package com.example.medilab.patient.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Laporan
import com.example.medilab.model.Notifikasi
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository

class PatientDashboardViewModel : ViewModel() {
    private val laporanRepo = LaporanRepository()
    private val notifRepo = NotifikasiRepository()
    private val authRepo = AuthRepository()

    private val _laporanTerbaru = MutableLiveData<Laporan?>()
    val laporanTerbaru: LiveData<Laporan?> = _laporanTerbaru

    private val _notifikasi = MutableLiveData<List<Notifikasi>>()
    val notifikasi: LiveData<List<Notifikasi>> = _notifikasi

    fun load() {
        val uid = authRepo.getCurrentUid()
        laporanRepo.getByPasienId(uid) { list ->
            _laporanTerbaru.value = list.firstOrNull()
        }
        notifRepo.getByUserId(uid) { _notifikasi.value = it }
    }
}
