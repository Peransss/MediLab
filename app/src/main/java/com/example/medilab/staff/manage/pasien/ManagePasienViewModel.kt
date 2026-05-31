package com.example.medilab.staff.manage.pasien

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.User
import com.example.medilab.repository.UserRepository
import com.example.medilab.util.Constants

class ManagePasienViewModel : ViewModel() {
    private val repo = UserRepository()

    private val _pasienList = MutableLiveData<List<User>>()
    val pasienList: LiveData<List<User>> = _pasienList

    private val _allPasien = mutableListOf<User>()

    fun load() {
        repo.getUsersByRole(Constants.ROLE_PASIEN) { list ->
            _allPasien.clear()
            _allPasien.addAll(list)
            _pasienList.value = list
        }
    }

    fun search(query: String) {
        _pasienList.value = if (query.isBlank()) _allPasien
        else _allPasien.filter { it.nama.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true) }
    }

    fun delete(uid: String) {
        repo.deleteUser(uid) { if (it) load() }
    }
}
