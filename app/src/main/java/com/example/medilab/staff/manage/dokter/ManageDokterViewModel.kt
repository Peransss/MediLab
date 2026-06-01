package com.example.medilab.staff.manage.dokter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Dokter
import com.example.medilab.repository.DokterRepository

class ManageDokterViewModel : ViewModel() {
    private val repo = DokterRepository()

    private val _dokterList = MutableLiveData<List<Dokter>>()
    val dokterList: LiveData<List<Dokter>> = _dokterList

    private val _allDokter = mutableListOf<Dokter>()

    fun load() {
        repo.getAll { list ->
            _allDokter.clear()
            _allDokter.addAll(list)
            _dokterList.value = list
        }
    }

    fun search(query: String) {
        _dokterList.value = if (query.isBlank()) _allDokter
        else _allDokter.filter { it.nama.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true) }
    }

    fun delete(id: String) {
        repo.delete(id) { if (it) load() }
    }
}
