package com.example.medilab.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.repository.AuthRepository

class AuthViewModel : ViewModel() {
    private val authRepo = AuthRepository()

    private val _loginResult = MutableLiveData<Pair<Boolean, String>>()
    val loginResult: LiveData<Pair<Boolean, String>> = _loginResult

    private val _registerResult = MutableLiveData<Pair<Boolean, String>>()
    val registerResult: LiveData<Pair<Boolean, String>> = _registerResult

    private val _loginRole = MutableLiveData<String>()
    val loginRole: LiveData<String> = _loginRole

    fun login(email: String, password: String) {
        authRepo.login(email, password) { success, msg, role ->
            _loginResult.value = Pair(success, msg)
            if (success) _loginRole.value = role
        }
    }

    fun registerPatient(email: String, password: String, nama: String, noHP: String, alamat: String, tanggalLahir: String) {
        authRepo.registerPatient(email, password, nama, noHP, alamat, tanggalLahir) { success, msg ->
            _registerResult.value = Pair(success, msg)
        }
    }

    fun sendPasswordReset(email: String) {
        authRepo.sendPasswordReset(email) { success, msg ->
            _loginResult.value = Pair(success, msg)
        }
    }
}
