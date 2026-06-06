package com.example.medilab.ui.screen.staff.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.model.Dokter
import com.example.medilab.model.Obat
import com.example.medilab.model.Pemeriksaan
import com.example.medilab.model.User
import com.example.medilab.repository.DokterRepository
import com.example.medilab.repository.ObatRepository
import com.example.medilab.repository.PemeriksaanRepository
import com.example.medilab.repository.UserRepository
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageViewModel : ViewModel() {
    private val userRepo = UserRepository()
    private val dokterRepo = DokterRepository()
    private val pemeriksaanRepo = PemeriksaanRepository()
    private val obatRepo = ObatRepository()

    private val _pasien = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val pasien: StateFlow<UiState<List<User>>> = _pasien.asStateFlow()

    private val _dokter = MutableStateFlow<UiState<List<Dokter>>>(UiState.Loading)
    val dokter: StateFlow<UiState<List<Dokter>>> = _dokter.asStateFlow()

    private val _petugas = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val petugas: StateFlow<UiState<List<User>>> = _petugas.asStateFlow()

    private val _pemeriksaan = MutableStateFlow<UiState<List<Pemeriksaan>>>(UiState.Loading)
    val pemeriksaan: StateFlow<UiState<List<Pemeriksaan>>> = _pemeriksaan.asStateFlow()

    private val _obat = MutableStateFlow<UiState<List<Obat>>>(UiState.Loading)
    val obat: StateFlow<UiState<List<Obat>>> = _obat.asStateFlow()

    fun loadPasien() {
        _pasien.value = UiState.Loading
        userRepo.getUsersByRole(Constants.ROLE_PASIEN) { list ->
            _pasien.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
        }
    }

    fun loadDokter() {
        _dokter.value = UiState.Loading
        dokterRepo.getAll { list ->
            _dokter.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
        }
    }

    fun loadPetugas() {
        _petugas.value = UiState.Loading
        viewModelScope.launch {
            val result = mutableListOf<User>()
            userRepo.getUsersByRole(Constants.ROLE_ADMIN) { admins ->
                result.addAll(admins)
                userRepo.getUsersByRole(Constants.ROLE_PETUGAS) { petugass ->
                    result.addAll(petugass)
                    _petugas.value = if (result.isEmpty()) UiState.Empty else UiState.Success(result)
                }
            }
        }
    }

    fun loadPemeriksaan() {
        _pemeriksaan.value = UiState.Loading
        pemeriksaanRepo.getAll { list ->
            _pemeriksaan.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
        }
    }

    fun loadObat() {
        _obat.value = UiState.Loading
        obatRepo.getAll { list ->
            _obat.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
        }
    }

    fun deletePasien(id: String, onDone: () -> Unit) {
        userRepo.deleteUser(id) { onDone(); loadPasien() }
    }

    fun deleteDokter(id: String, onDone: () -> Unit) {
        dokterRepo.delete(id) { onDone(); loadDokter() }
    }

    fun deletePemeriksaan(id: String, onDone: () -> Unit) {
        pemeriksaanRepo.delete(id) { onDone(); loadPemeriksaan() }
    }

    fun deleteObat(id: String, onDone: () -> Unit) {
        obatRepo.delete(id) { onDone(); loadObat() }
    }

    fun saveDokter(d: Dokter, onDone: () -> Unit) {
        dokterRepo.add(d) { onDone(); loadDokter() }
    }

    fun savePemeriksaan(p: Pemeriksaan, onDone: () -> Unit) {
        pemeriksaanRepo.add(p) { onDone(); loadPemeriksaan() }
    }

    fun saveObat(o: Obat, onDone: () -> Unit) {
        obatRepo.add(o) { onDone(); loadObat() }
    }
}
