package com.example.medilab.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.repository.AuthRepository
import com.example.medilab.util.PasswordStrengthChecker
import com.example.medilab.util.Validators
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

    private val allowedEmailDomains = setOf(
        "gmail.com",
        "yahoo.com",
        "yahoo.co.id",
        "outlook.com",
        "hotmail.com",
        "mail.com",
        "protonmail.com",
        "icloud.com"
    )

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
            !isAllowedEmailDomain(email) ->
                "Domain email tidak diizinkan. Gunakan: ${allowedEmailDomains.joinToString(", ")}"
            else -> null
        }
    }

    private fun isAllowedEmailDomain(email: String): Boolean {
        val domain = email.substringAfterLast("@").lowercase()
        return allowedEmailDomains.contains(domain)
    }


    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = null
        )
    }

    fun validatePassword(): Boolean {
        val password = _uiState.value.password
        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(passwordError = "Password tidak boleh kosong")
            return false
        }
        viewModelScope.launch {
            val result = PasswordStrengthChecker.isPasswordStrong(password)
            _uiState.value = _uiState.value.copy(passwordError = result.message)
        }
        return true
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
            _uiState.value = state.copy(passwordError = "Password minimal 8 karakter")
            return
        }
        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        authRepo.login(state.email, state.password) { success, msg, role ->
            if (success && !role.isNullOrEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null,
                    successMessage = msg,
                    userRole = role,
                    email = "",
                    password = ""
                )
                onSuccess(role)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = if (success) "Akun tidak memiliki role. Hubungi admin." else msg,
                    successMessage = if (success) null else null
                )
            }
        }
    }

    fun registerPatient(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.nama.isBlank() || state.email.isBlank() || state.password.isBlank() ||
            state.noHP.isBlank() || state.alamat.isBlank() || state.tanggalLahir.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Semua field harus diisi")
            return
        }
        viewModelScope.launch {
            val result = PasswordStrengthChecker.isPasswordStrong(state.password)
            if (!result.isStrong) {
                _uiState.value = _uiState.value.copy(
                    passwordError = result.message,
                    errorMessage = null
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(
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
                if (success) onSuccess()
            }
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
