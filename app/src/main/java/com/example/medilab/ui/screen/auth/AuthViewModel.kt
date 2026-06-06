package com.example.medilab.ui.screen.auth

import androidx.lifecycle.ViewModel
import com.example.medilab.repository.AuthRepository
import com.example.medilab.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val nama: String = "",
    val noHP: String = "",
    val alamat: String = "",
    val tanggalLahir: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val userRole: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)

class AuthViewModel : ViewModel() {
    private val authRepo = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, emailError = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, passwordError = null)
    }

    fun onNamaChange(value: String) { _uiState.value = _uiState.value.copy(nama = value) }
    fun onNoHPChange(value: String) { _uiState.value = _uiState.value.copy(noHP = value) }
    fun onAlamatChange(value: String) { _uiState.value = _uiState.value.copy(alamat = value) }
    fun onTanggalLahirChange(value: String) { _uiState.value = _uiState.value.copy(tanggalLahir = value) }

    fun login(onSuccess: (String) -> Unit) {
        val state = _uiState.value
        if (!Validators.isValidEmail(state.email)) {
            _uiState.value = state.copy(emailError = "Email tidak valid")
            return
        }
        if (!Validators.isValidPassword(state.password)) {
            _uiState.value = state.copy(passwordError = "Password minimal 6 karakter")
            return
        }
        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        authRepo.login(state.email, state.password) { success, msg, role ->
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = if (success) null else msg,
                userRole = role,
                successMessage = if (success) msg else null
            )
            if (success && role != null) onSuccess(role)
        }
    }

    fun registerPatient(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.nama.isBlank() || state.email.isBlank() || state.password.isBlank() ||
            state.noHP.isBlank() || state.alamat.isBlank() || state.tanggalLahir.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Semua field harus diisi")
            return
        }
        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        authRepo.registerPatient(state.email, state.password, state.nama, state.noHP, state.alamat, state.tanggalLahir) { success, msg ->
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = if (success) null else msg,
                successMessage = if (success) msg else null
            )
            if (success) onSuccess()
        }
    }

    fun sendPasswordReset() {
        val state = _uiState.value
        if (!Validators.isValidEmail(state.email)) {
            _uiState.value = state.copy(emailError = "Email tidak valid")
            return
        }
        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        authRepo.sendPasswordReset(state.email) { success, msg ->
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                successMessage = if (success) msg else "Gagal mengirim email",
                errorMessage = if (success) null else msg
            )
        }
    }

    fun logout() {
        authRepo.logout()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
