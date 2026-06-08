package com.example.medilab.ui.screen.staff.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.MediLabApp
import com.example.medilab.database.entity.DokterEntity
import com.example.medilab.database.entity.ObatEntity
import com.example.medilab.database.entity.PemeriksaanEntity
import com.example.medilab.database.repository.LocalDokterRepository
import com.example.medilab.database.repository.LocalObatRepository
import com.example.medilab.database.repository.LocalPemeriksaanRepository
import com.example.medilab.model.Dokter
import com.example.medilab.model.Obat
import com.example.medilab.model.Pemeriksaan
import com.example.medilab.model.User
import com.example.medilab.repository.UserRepository
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageViewModel : ViewModel() {
    private val app by lazy { MediLabApp.instance }
    private val userRepo = UserRepository()
    private val localDokterRepo = LocalDokterRepository(app.database, app.syncManager)
    private val localPemeriksaanRepo = LocalPemeriksaanRepository(app.database, app.syncManager)
    private val localObatRepo = LocalObatRepository(app.database, app.syncManager)

    private val _pasien = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val pasien: StateFlow<UiState<List<User>>> = _pasien.asStateFlow()

    private val _petugas = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val petugas: StateFlow<UiState<List<User>>> = _petugas.asStateFlow()

    val dokter: StateFlow<UiState<List<Dokter>>> = localDokterRepo.getAll()
        .map { entities ->
            if (entities.isEmpty()) UiState.Empty
            else UiState.Success(entities.map { it.toModel() })
        }
        .catch { e -> emit(UiState.Error("Gagal memuat dokter: ${e.message}")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val pemeriksaan: StateFlow<UiState<List<Pemeriksaan>>> = localPemeriksaanRepo.getAll()
        .map { entities ->
            if (entities.isEmpty()) UiState.Empty
            else UiState.Success(entities.map { it.toModel() })
        }
        .catch { e -> emit(UiState.Error("Gagal memuat tes: ${e.message}")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val obat: StateFlow<UiState<List<Obat>>> = localObatRepo.getAll()
        .map { entities ->
            if (entities.isEmpty()) UiState.Empty
            else UiState.Success(entities.map { it.toModel() })
        }
        .catch { e -> emit(UiState.Error("Gagal memuat obat: ${e.message}")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun loadPasien() {
        _pasien.value = UiState.Loading
        userRepo.getUsersByRole(Constants.ROLE_PASIEN) { list ->
            _pasien.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
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

    fun deletePasien(id: String, onDone: () -> Unit) {
        userRepo.deleteUser(id) { onDone(); loadPasien() }
    }

    fun deleteDokter(id: String, onDone: () -> Unit) {
        localDokterRepo.delete(id)
        onDone()
    }

    fun deletePemeriksaan(id: String, onDone: () -> Unit) {
        localPemeriksaanRepo.delete(id)
        onDone()
    }

    fun deleteObat(id: String, onDone: () -> Unit) {
        localObatRepo.delete(id)
        onDone()
    }

    fun saveDokter(d: Dokter, onDone: () -> Unit) {
        localDokterRepo.upsert(
            DokterEntity(
                id = d.id, nama = d.nama, spesialis = d.spesialis,
                alamat = d.alamat, noHP = d.noHP, email = d.email
            )
        )
        onDone()
    }

    fun savePemeriksaan(p: Pemeriksaan, onDone: () -> Unit) {
        localPemeriksaanRepo.upsert(
            PemeriksaanEntity(
                id = p.id, namaPemeriksaan = p.namaPemeriksaan,
                kategori = p.kategori, deskripsi = p.deskripsi,
                parameter = p.parameter
            )
        )
        onDone()
    }

    fun saveObat(o: Obat, onDone: () -> Unit) {
        localObatRepo.upsert(
            ObatEntity(
                id = o.id, namaObat = o.namaObat, bentuk = o.bentuk,
                dosis = o.dosis, satuan = o.satuan, keterangan = o.keterangan
            )
        )
        onDone()
    }

    private val _petugasSaveError = MutableStateFlow<String?>(null)
    val petugasSaveError: StateFlow<String?> = _petugasSaveError.asStateFlow()
    private val _petugasSaving = MutableStateFlow(false)
    val petugasSaving: StateFlow<Boolean> = _petugasSaving.asStateFlow()

    fun savePetugas(email: String, password: String, nama: String, role: String, noHP: String) {
        _petugasSaveError.value = null
        _petugasSaving.value = true
        com.example.medilab.repository.AuthRepository().createStaffAccount(
            email, password, nama, role, noHP
        ) { ok, msg ->
            _petugasSaving.value = false
            if (ok) {
                loadPetugas()
            } else {
                _petugasSaveError.value = msg
            }
        }
    }

    fun clearPetugasSaveError() {
        _petugasSaveError.value = null
    }
}

private fun DokterEntity.toModel() = Dokter(
    id = id, nama = nama, spesialis = spesialis,
    alamat = alamat, noHP = noHP, email = email
)

private fun PemeriksaanEntity.toModel() = Pemeriksaan(
    id = id, namaPemeriksaan = namaPemeriksaan,
    kategori = kategori, deskripsi = deskripsi,
    parameter = parameter
)

private fun ObatEntity.toModel() = Obat(
    id = id, namaObat = namaObat, bentuk = bentuk,
    dosis = dosis, satuan = satuan, keterangan = keterangan
)
