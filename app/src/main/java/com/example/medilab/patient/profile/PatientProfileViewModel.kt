package com.example.medilab.patient.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.User
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.UserRepository

class PatientProfileViewModel : ViewModel() {
    private val userRepo = UserRepository()
    private val authRepo = AuthRepository()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    fun load() {
        userRepo.getUser(authRepo.getCurrentUid()) { _user.value = it }
    }

    fun logout() {
        authRepo.logout()
    }
}
