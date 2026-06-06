package com.example.medilab.ui.screen.patient.profile

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

class PatientProfileViewModel : ViewModel() {
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

    // NOTE: Firebase requires recent re-authentication for sensitive operations
    // like password changes. This implementation will fail with
    // 'This operation is sensitive and requires recent authentication' for users
    // signed in more than ~5 minutes ago. A full reauth flow (re-prompt for
    // current password, then reauthenticate) is deferred.
    // The dialog below shows the actual Firebase error message so the user
    // understands why it failed rather than failing silently.
    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()
    private val _passwordSubmitting = MutableStateFlow(false)
    val passwordSubmitting: StateFlow<Boolean> = _passwordSubmitting.asStateFlow()

    fun changePassword(newPassword: String) {
        if (newPassword.length < 6) {
            _passwordError.value = "Password minimal 6 karakter"
            return
        }
        _passwordError.value = null
        _passwordSubmitting.value = true
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user == null) {
            _passwordError.value = "Tidak ada user login"
            _passwordSubmitting.value = false
            return
        }
        user.updatePassword(newPassword).addOnCompleteListener { task ->
            _passwordSubmitting.value = false
            _passwordError.value = if (task.isSuccessful) null
                else (task.exception?.message ?: "Gagal mengubah password")
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
