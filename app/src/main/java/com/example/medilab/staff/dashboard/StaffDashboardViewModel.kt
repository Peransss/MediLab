package com.example.medilab.staff.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Laporan
import com.example.medilab.model.Notifikasi
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository
import com.example.medilab.util.Constants

class StaffDashboardViewModel : ViewModel() {
    private val laporanRepo = LaporanRepository()
    private val notifRepo = NotifikasiRepository()
    private val authRepo = AuthRepository()

    private val _laporanBaru = MutableLiveData<Int>()
    val laporanBaru: LiveData<Int> = _laporanBaru

    private val _laporanProses = MutableLiveData<Int>()
    val laporanProses: LiveData<Int> = _laporanProses

    private val _laporanSelesai = MutableLiveData<Int>()
    val laporanSelesai: LiveData<Int> = _laporanSelesai

    private val _laporanPendingVerifikasi = MutableLiveData<List<Laporan>>()
    val laporanPendingVerifikasi: LiveData<List<Laporan>> = _laporanPendingVerifikasi

    private val _notifikasi = MutableLiveData<List<Notifikasi>>()
    val notifikasi: LiveData<List<Notifikasi>> = _notifikasi

    fun loadData() {
        laporanRepo.getByStatus(Constants.STATUS_BARU) { list -> _laporanBaru.value = list.size }
        laporanRepo.getByStatus(Constants.STATUS_PROSES) { list -> _laporanProses.value = list.size }
        laporanRepo.getByStatus(Constants.STATUS_SELESAI) { list -> _laporanSelesai.value = list.size }
        laporanRepo.getByStatus(Constants.STATUS_VERIFIKASI) { list -> _laporanPendingVerifikasi.value = list }
        notifRepo.getByUserId(authRepo.getCurrentUid()) { list -> _notifikasi.value = list }
    }
}
