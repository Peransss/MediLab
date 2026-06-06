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
    // like password changes. The current implementation does not reauth the user,
    // so this will fail for accounts that have been signed in for a long time.
    // Proper reauth flow is deferred.
    fun changePassword(newPassword: String, onDone: (Boolean) -> Unit) {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            onDone(task.isSuccessful)
        } ?: onDone(false)
    }

    fun logout(onLogout: () -> Unit) {
        authRepo.logout()
        onLogout()
    }
}
