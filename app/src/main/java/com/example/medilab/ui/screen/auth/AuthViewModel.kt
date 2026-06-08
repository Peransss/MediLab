package com.example.medilab.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.repository.AuthRepository
import com.example.medilab.util.PasswordStrengthChecker
import com.example.medilab.util.Validators
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val nama: String = "",
    val noHP: String = "",
    val alamat: String = "",
    val tanggalLahir: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val userRole: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isPasswordStrong: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private var passwordValidationJob: Job? = null

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(
            email = newEmail,
            emailError = validateEmail(newEmail)
        )
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email tidak boleh kosong"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Format email tidak valid"
            !Validators.isAllowedEmailDomain(email) ->
                "Domain email tidak diizinkan"
            else -> null
        }
    }


    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = null // Clear error immediately
        )
        passwordValidationJob?.cancel()
        passwordValidationJob = viewModelScope.launch {
            delay(500) // Debounce
            val result = PasswordStrengthChecker.isPasswordStrong(value)
            _uiState.value = _uiState.value.copy(
                passwordError = result.message,
                isPasswordStrong = result.isStrong
            )
        }
    }

    fun onNamaChange(value: String) { _uiState.value = _uiState.value.copy(nama = value) }
    fun onNoHPChange(value: String) { _uiState.value = _uiState.value.copy(noHP = value) }
    fun onAlamatChange(value: String) { _uiState.value = _uiState.value.copy(alamat = value) }
    fun onTanggalLahirChange(value: String) { _uiState.value = _uiState.value.copy(tanggalLahir = value) }

    fun login() {
        val state = _uiState.value
        if (!Validators.isValidEmail(state.email)) {
            _uiState.value = state.copy(emailError = "Email tidak valid")
            return
        }
        if (!Validators.isAllowedEmailDomain(state.email)) {
            _uiState.value = state.copy(emailError = "Domain email tidak diizinkan")
            return
        }
        if (!Validators.isValidPassword(state.password)) {
            _uiState.value = state.copy(passwordError = "Password minimal 8 karakter")
            return
        }
        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        authRepo.login(state.email, state.password) { success, msg, role ->
            if (success && !role.isNullOrEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    isLoading = false,
                    errorMessage = null,
                    successMessage = msg,
                    userRole = role,
                    email = "",
                    password = ""
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = false,
                    isLoading = false,
                    errorMessage = if (success) "Akun tidak memiliki role. Hubungi admin." else msg,
                    successMessage = if (success) null else null
                )
            }
        }
    }

    fun registerPatient() {
        val state = _uiState.value
        if (state.nama.isBlank() || state.email.isBlank() || state.password.isBlank() ||
            state.noHP.isBlank() || state.alamat.isBlank() || state.tanggalLahir.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Semua field harus diisi")
            return
        }
        if (!Validators.isValidEmail(state.email)) {
            _uiState.value = state.copy(emailError = "Format email tidak valid")
            return
        }
        if (!Validators.isAllowedEmailDomain(state.email)) {
            _uiState.value = state.copy(emailError = "Domain email tidak diizinkan")
            return
        }
        if (!state.isPasswordStrong) { // Use the pre-calculated strength from onPasswordChange
            _uiState.value = state.copy(
                passwordError = _uiState.value.passwordError ?: "Password tidak cukup kuat",
                errorMessage = null
            )
            return
        }
        _uiState.value = state.copy(
            isLoading = true,
            errorMessage = null,
            passwordError = null
        )
        authRepo.registerPatient(state.email, state.password, state.nama, state.noHP, state.alamat, state.tanggalLahir) { success, msg ->
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = if (success) null else msg,
                successMessage = if (success) msg else null
            )
        }
    }

    fun sendPasswordReset() {
        val state = _uiState.value
        if (!Validators.isValidEmail(state.email)) {
            _uiState.value = state.copy(emailError = "Email tidak valid")
            return
        }
        if (!Validators.isAllowedEmailDomain(state.email)) {
            _uiState.value = state.copy(emailError = "Domain email tidak diizinkan")
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
