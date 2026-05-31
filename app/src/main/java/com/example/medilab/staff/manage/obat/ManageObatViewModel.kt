package com.example.medilab.staff.manage.obat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Obat
import com.example.medilab.repository.ObatRepository

class ManageObatViewModel : ViewModel() {
    private val repo = ObatRepository()

    private val _list = MutableLiveData<List<Obat>>()
    val list: LiveData<List<Obat>> = _list

    fun load() {
        repo.getAll { _list.value = it }
    }

    fun delete(id: String) {
        repo.delete(id) { if (it) load() }
    }
}
