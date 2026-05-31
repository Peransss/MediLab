package com.example.medilab.patient.hasil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Laporan
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.util.Constants

class HasilViewModel : ViewModel() {
    private val repo = LaporanRepository()
    private val authRepo = AuthRepository()

    private val _laporanSelesai = MutableLiveData<List<Laporan>>()
    val laporanSelesai: LiveData<List<Laporan>> = _laporanSelesai

    fun load() {
        repo.getByPasienId(authRepo.getCurrentUid()) { list ->
            _laporanSelesai.value = list.filter { it.status == Constants.STATUS_SELESAI }
        }
    }
}
