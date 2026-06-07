package com.example.medilab.ui.screen.staff.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.model.User
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.UserRepository
import com.example.medilab.ui.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StaffProfileViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val userRepo = UserRepository()
    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val uiState: StateFlow<UiState<User>> = _uiState.asStateFlow()

    fun load() {
        _uiState.value = UiState.Loading
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            _uiState.value = UiState.Error("Tidak ada user login")
            return
        }
        viewModelScope.launch {
            userRepo.getUser(uid) { user ->
                _uiState.value = if (user == null) UiState.Error("Gagal memuat") else UiState.Success(user)
            }
        }
    }

    fun updateProfile(updates: Map<String, Any>, onDone: () -> Unit) {
        val uid = authRepo.getCurrentUid()
        viewModelScope.launch {
            userRepo.updateUser(uid, updates) { ok ->
                if (ok) load()
                onDone()
            }
        }
    }

    // Firebase requires recent re-authentication for sensitive operations like
    // password changes. We use AuthRepository.changePassword() which handles
    // re-authentication via EmailAuthProvider before calling updatePassword().
    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()
    private val _passwordSubmitting = MutableStateFlow(false)
    val passwordSubmitting: StateFlow<Boolean> = _passwordSubmitting.asStateFlow()

    fun changePassword(oldPassword: String, newPassword: String) {
        if (oldPassword.isBlank()) {
            _passwordError.value = "Password lama harus diisi"
            return
        }
        if (newPassword.length < 8) {
            _passwordError.value = "Password minimal 8 karakter"
            return
        }
        _passwordError.value = null
        _passwordSubmitting.value = true
        authRepo.changePassword(oldPassword, newPassword) { ok, msg ->
            _passwordSubmitting.value = false
            _passwordError.value = if (ok) null else msg
        }
    }

    fun clearPasswordError() {
        _passwordError.value = null
    }

    fun logout(onLogout: () -> Unit) {
        authRepo.logout()
        onLogout()
    }
}
