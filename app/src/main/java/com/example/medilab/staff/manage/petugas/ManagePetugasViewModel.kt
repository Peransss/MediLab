package com.example.medilab.staff.manage.petugas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.User
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.UserRepository
import com.example.medilab.util.Constants

class ManagePetugasViewModel : ViewModel() {
    private val userRepo = UserRepository()
    private val authRepo = AuthRepository()

    private val _petugasList = MutableLiveData<List<User>>()
    val petugasList: LiveData<List<User>> = _petugasList

    fun load() {
        userRepo.getUsersByRole(Constants.ROLE_ADMIN) { admin ->
            userRepo.getUsersByRole(Constants.ROLE_PETUGAS) { petugas ->
                _petugasList.value = admin + petugas
            }
        }
    }

    fun create(email: String, password: String, nama: String, role: String, noHP: String) {
        authRepo.createStaffAccount(email, password, nama, role, noHP) { _, _ -> load() }
    }
}
