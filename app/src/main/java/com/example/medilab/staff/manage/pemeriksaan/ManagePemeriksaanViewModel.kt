package com.example.medilab.staff.manage.pemeriksaan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Pemeriksaan
import com.example.medilab.repository.PemeriksaanRepository

class ManagePemeriksaanViewModel : ViewModel() {
    private val repo = PemeriksaanRepository()

    private val _list = MutableLiveData<List<Pemeriksaan>>()
    val list: LiveData<List<Pemeriksaan>> = _list

    fun load() {
        repo.getAll { _list.value = it }
    }

    fun delete(id: String) {
        repo.delete(id) { if (it) load() }
    }
}
