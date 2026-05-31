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

    fun load() {
        repo.getAll { _dokterList.value = it }
    }

    fun delete(id: String) {
        repo.delete(id) { if (it) load() }
    }
}
