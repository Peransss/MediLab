# MediLab Redesign — Migrasi ke Jetpack Compose

**Tanggal:** 6 Juni 2026
**Tipe:** Redesign UI/UX + Migrasi Stack
**Referensi Visual:** Health App UI (Pinterest — purple/indigo dengan ilustrasi dokter)
**Stack Target:** Kotlin + Jetpack Compose + Material 3 + Compose Navigation
**Status:** Disetujui untuk implementasi

---

## 1. Tujuan

Redesign total aplikasi MediLab (Android) dengan:

1. **Migrasi dari XML Layout + Fragment + ViewBinding** → **Jetpack Compose + Material 3** (single-activity).
2. **Visual style** mengikuti referensi health app modern: purple/indigo, ilustrasi vektor flat, layout card-based, gradient hero.
3. **Pengalaman lebih modern & intuitif** untuk pasien (consumer-grade) dan tetap powerful untuk staff (admin/petugas lab).
4. **Pertahankan Firebase backend** — tidak mengubah schema, model, repository, atau business logic.
5. **Memperbaiki fitur yang masih TODO** dari spec sebelumnya (edit profile, ganti password, status transition actions, dark mode, dsb).

---

## 2. Keputusan Desain Utama (Hasil Brainstorm)

| Topik | Keputusan |
|---|---|
| Stack implementasi | **Full Compose** — semua Activity/Fragment jadi Composable |
| Palet warna | **Indigo / Purple** (`#5B5FED` primary, `#E8E8FF` container) |
| Typography | **Plus Jakarta Sans** (headline) + **Inter** (body) |
| Onboarding flow | **3 halaman onboarding** + Login + Register |
| Aset visual | **Vector asset** (SVG/Compose drawable) + Material Symbols Outlined icons |
| Dark mode | **Light + manual toggle** (di Profile, no dynamic color) |
| Bottom nav | **5 items staff** (Home, Manage, Laporan, Notif, Profile), **4 items patient** (Home, Hasil, Riwayat, Profile) |
| Komponen interaksi | **Full Material 3** (BottomSheet, FAB, AlertDialog, AnimatedVisibility) |
| Navigation library | **Compose Navigation** (NavHost + nested) |

---

## 3. Design System

### 3.1 Color Palette (Material 3 ColorScheme)

**Light Theme**

| Token | Hex | Penggunaan |
|---|---|---|
| `primary` | `#5B5FED` | Brand, FAB, primary button, active nav |
| `onPrimary` | `#FFFFFF` | Text/icon on primary |
| `primaryContainer` | `#E8E8FF` | Soft indigo — hero card bg, chip bg |
| `onPrimaryContainer` | `#1A1A66` | Text on primaryContainer |
| `secondary` | `#FF6B9D` | Accent, love/favorite, notifikasi badge |
| `onSecondary` | `#FFFFFF` | Text on secondary |
| `tertiary` | `#10B981` | Success, status Selesai |
| `onTertiary` | `#FFFFFF` | Text on tertiary |
| `error` | `#EF4444` | Error, status Ditolak/Batal |
| `onError` | `#FFFFFF` | Text on error |
| `warning` | `#F59E0B` | Status Revisi, perhatian |
| `background` | `#FAFAFE` | Scaffold background |
| `onBackground` | `#0F172A` | Primary text |
| `surface` | `#FFFFFF` | Card, sheet, app bar |
| `onSurface` | `#0F172A` | Text on surface |
| `surfaceVariant` | `#F3F4F9` | Subtle card bg, divider area |
| `onSurfaceVariant` | `#475569` | Secondary text, label |
| `outline` | `#E2E8F0` | Border, divider |
| `outlineVariant` | `#F1F5F9` | Subtle border |

**Dark Theme**

| Token | Hex |
|---|---|
| `primary` | `#9D9DFF` |
| `onPrimary` | `#1A1A66` |
| `primaryContainer` | `#3F3FCC` |
| `onPrimaryContainer` | `#E8E8FF` |
| `secondary` | `#FF8FB5` |
| `tertiary` | `#34D399` |
| `error` | `#F87171` |
| `background` | `#0F0E17` |
| `onBackground` | `#F1F5F9` |
| `surface` | `#1A1925` |
| `onSurface` | `#F1F5F9` |
| `surfaceVariant` | `#252333` |
| `onSurfaceVariant` | `#94A3B8` |
| `outline` | `#3F3D52` |
| `outlineVariant` | `#2A2839` |

**Status Color Mapping** (untuk StatusChip)

| Status Laporan | Background | Text |
|---|---|---|
| Baru | `#E8E8FF` (primaryContainer) | `#1A1A66` |
| Proses | `#FEF3C7` (amber-100) | `#92400E` |
| Verifikasi | `#DBEAFE` (blue-100) | `#1E40AF` |
| Revisi | `#FEF3C7` (amber-100) | `#92400E` |
| Selesai | `#D1FAE5` (emerald-100) | `#065F46` |
| Ditolak | `#FEE2E2` (red-100) | `#991B1B` |
| Batal | `#F3F4F6` (gray-100) | `#374151` |

### 3.2 Typography

Font: **Plus Jakarta Sans** (display/headline/title) + **Inter** (body/label). Di-load dari Google Fonts via `androidx.compose.ui:ui-text-google-fonts` (offline cache setelah download pertama).

```
Display Large    Plus Jakarta Sans   36sp / 44sp / 700
Display Medium   Plus Jakarta Sans   28sp / 36sp / 700
Headline Large   Plus Jakarta Sans   24sp / 32sp / 700
Headline Medium  Plus Jakarta Sans   20sp / 28sp / 600
Headline Small   Plus Jakarta Sans   18sp / 24sp / 600
Title Large      Plus Jakarta Sans   16sp / 24sp / 600
Title Medium     Inter               14sp / 20sp / 600
Title Small      Inter               12sp / 16sp / 500
Body Large       Inter               16sp / 24sp / 400
Body Medium      Inter               14sp / 20sp / 400
Body Small       Inter               12sp / 16sp / 400
Label Large      Inter               14sp / 20sp / 500
Label Medium     Inter               12sp / 16sp / 500
Label Small      Inter               11sp / 16sp / 500
```

### 3.3 Shape

```
extraSmall: 4dp     — badge dot
small:      8dp     — button, chip
medium:     16dp    — card, list item
large:      24dp    — bottom sheet, hero card
extraLarge: 32dp    — onboarding hero
```

### 3.4 Spacing (4dp grid)

```
xxs: 4dp
xs:  8dp
sm:  12dp
md:  16dp
lg:  20dp   — default horizontal content padding
xl:  24dp
xxl: 32dp
xxxl: 48dp
```

### 3.5 Elevation

```
level0: 0dp   — flat
level1: 2dp   — resting card
level2: 8dp   — FAB, active card, bottom sheet
level3: 12dp  — modal dialog
```

---

## 4. Component Library (Reusable)

Package: `com.example.medilab.ui.component`

| Component | Deskripsi | Props utama |
|---|---|---|
| `MediLabButton` | Filled / Outlined / Text / Tonal | `onClick`, `enabled`, `text`, `icon`, `modifier` |
| `MediLabTextField` | Outlined dengan error state, leading/trailing icon | `value`, `onValueChange`, `label`, `isError`, `errorMessage`, `leadingIcon`, `trailingIcon`, `visualTransformation` |
| `MediLabPasswordField` | TextField khusus password (toggle visibility) | extend TextField, `passwordVisible: Boolean` |
| `MediLabCard` | Card rounded 16dp, elevation 1 | `onClick?`, `modifier`, `content: @Composable ()` |
| `HeroCard` | Gradient bg (primary→secondary), rounded 24dp, ilustrasi | `title`, `subtitle`, `illustration`, `action` |
| `StatCard` | Icon + value (large) + label, rounded 16dp | `icon`, `value`, `label`, `onClick?` |
| `StatusChip` | Pill chip dengan background color sesuai status | `status: LaporanStatus` |
| `LaporanCard` | Card khusus list laporan | `laporan: Laporan`, `onClick` |
| `SectionHeader` | Title + optional "Lihat semua" trailing | `title`, `onSeeAllClick?` |
| `MediLabBottomBar` | Material 3 NavigationBar | `items: List<BottomNavItem>`, `currentRoute`, `onNavigate` |
| `MediLabTopAppBar` | CenterAlignedTopAppBar dengan profile / back | `title`, `onBackClick?`, `actions: @Composable RowScope.() = {}` |
| `MediLabScaffold` | Wrapper: top bar + content + bottom bar | `topBar`, `bottomBar`, `content` |
| `EmptyState` | Ilustrasi + judul + deskripsi + CTA | `illustration`, `title`, `description`, `actionLabel?`, `onAction?` |
| `LoadingState` | Centered CircularProgress | `label?` |
| `ErrorState` | Icon + message + retry | `message`, `onRetry` |
| `NotifikasiItem` | Card notifikasi dengan unread dot | `notifikasi: Notifikasi`, `onClick` |
| `OnboardingPage` | HeroCard + title + body + indicator | `illustration`, `title`, `description`, `page`, `totalPages` |
| `PrimaryTabRow` | ScrollableTabRow dengan chip-style | `tabs: List<String>`, `selectedIndex`, `onTabSelected` |
| `ShimmerBox` | Skeleton placeholder dengan shimmer | `modifier` |

---

## 5. Struktur Navigasi

### 5.1 Top-Level Flow

```
Splash
  ├─ not logged in
  │   ├─ Onboarding1 → Onboarding2 → Onboarding3
  │   ├─ Login
  │   │   ├─ ForgotPassword
  │   │   └─ Register (modal/sheet)
  │   └─ (after login) → routing by role
  │
  └─ logged in
      ├─ role=admin OR petugas OR dokter → StaffRoot
      └─ role=pasien → PatientRoot
```

### 5.2 Staff Navigation Graph (5 tabs)

```
StaffRoot (NavigationBar 5 items)
├─ Home (StaffHomeScreen)
│   └─ LaporanDetail
├─ Manage (nested NavHost)
│   ├─ Pasien
│   │   └─ PasienForm (add/edit, bottom sheet)
│   ├─ Dokter
│   │   └─ DokterForm
│   ├─ Petugas
│   │   └─ PetugasForm
│   ├─ Pemeriksaan
│   │   └─ PemeriksaanForm
│   └─ Obat
│       └─ ObatForm
├─ Laporan (nested NavHost)
│   ├─ ListByStatus (TabRow: Baru|Proses|Verifikasi|Revisi|Selesai|Ditolak)
│   │   └─ LaporanDetail
│   │       └─ StatusTransition (modal/sheet)
│   └─ (Filter, Search, etc.)
├─ Notifikasi
│   └─ NotifikasiDetail (jika ada)
└─ Profile
    ├─ EditProfile (modal/sheet)
    ├─ GantiPassword (modal/sheet)
    └─ Logout (confirm dialog)
```

### 5.3 Patient Navigation Graph (4 tabs)

```
PatientRoot (NavigationBar 4 items)
├─ Home (PatientHomeScreen)
│   └─ LaporanDetail
├─ Hasil (HasilScreen)
│   └─ LaporanDetail
├─ Riwayat (RiwayatScreen)
│   └─ RekamMedisDetail
├─ Notifikasi
└─ Profile
    ├─ EditProfile
    ├─ GantiPassword
    └─ Logout
```

### 5.4 Routes (sealed class)

```kotlin
sealed class Route(val path: String) {
    // Onboarding & Auth
    object Splash : Route("splash")
    object Onboarding1 : Route("onboarding/1")
    object Onboarding2 : Route("onboarding/2")
    object Onboarding3 : Route("onboarding/3")
    object Login : Route("auth/login")
    object Register : Route("auth/register")
    object ForgotPassword : Route("auth/forgot")

    // Staff
    object StaffRoot : Route("staff")
    object StaffHome : Route("staff/home")
    object StaffManage : Route("staff/manage")
    object StaffLaporan : Route("staff/laporan")
    object StaffNotifikasi : Route("staff/notifikasi")
    object StaffProfile : Route("staff/profile")
    object StaffLaporanDetail : Route("staff/laporan/{laporanId}") {
        fun build(id: String) = "staff/laporan/$id"
    }

    // Patient
    object PatientRoot : Route("patient")
    object PatientHome : Route("patient/home")
    object PatientHasil : Route("patient/hasil")
    object PatientRiwayat : Route("patient/riwayat")
    object PatientNotifikasi : Route("patient/notifikasi")
    object PatientProfile : Route("patient/profile")
    object LaporanDetail : Route("patient/laporan/{laporanId}") {
        fun build(id: String) = "patient/laporan/$id"
    }
}
```

---

## 6. Arsitektur Compose

### 6.1 Single-Activity

Hapus semua Activity kecuali `MainActivity`. `MainActivity` hanya berisi:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkMode by darkModeViewModel.isDarkMode.collectAsState()
            MediLabTheme(darkTheme = darkMode) {
                AppNavHost()
            }
        }
    }
}
```

### 6.2 Package Structure (Final)

```
com.example.medilab/
├── MainActivity.kt                    -- single activity, setContent
├── MediLabApp.kt                      -- Application class (Firebase init)
│
├── ui/
│   ├── theme/
│   │   ├── Color.kt                   -- semua color token
│   │   ├── Type.kt                    -- Typography
│   │   ├── Shape.kt                   -- Shapes
│   │   ├── Spacing.kt                 -- Dimens object
│   │   ├── DarkModeViewModel.kt       -- toggle dark mode (DataStore)
│   │   └── Theme.kt                   -- MediLabTheme composable
│   │
│   ├── component/                     -- reusable composables (lihat §4)
│   │   ├── MediLabButton.kt
│   │   ├── MediLabTextField.kt
│   │   ├── MediLabCard.kt
│   │   ├── HeroCard.kt
│   │   ├── StatCard.kt
│   │   ├── StatusChip.kt
│   │   ├── LaporanCard.kt
│   │   ├── MediLabBottomBar.kt
│   │   ├── MediLabTopAppBar.kt
│   │   ├── MediLabScaffold.kt
│   │   ├── EmptyState.kt
│   │   ├── LoadingState.kt
│   │   ├── ErrorState.kt
│   │   ├── NotifikasiItem.kt
│   │   ├── OnboardingPage.kt
│   │   ├── PrimaryTabRow.kt
│   │   └── ShimmerBox.kt
│   │
│   ├── navigation/
│   │   ├── Routes.kt                  -- sealed class Route
│   │   ├── AppNavHost.kt              -- root NavHost
│   │   ├── OnboardingNav.kt
│   │   ├── AuthNav.kt
│   │   ├── StaffNav.kt                -- StaffRoot + nested
│   │   ├── ManageNav.kt               -- nested manage
│   │   ├── LaporanNav.kt              -- nested laporan
│   │   └── PatientNav.kt              -- PatientRoot + nested
│   │
│   ├── illustration/                  -- Compose Painter
│   │   ├── DoctorIllustration.kt
│   │   ├── MedicalRecordIllustration.kt
│   │   ├── NotificationIllustration.kt
│   │   └── EmptyStateIllustrations.kt
│   │
│   ├── util/
│   │   ├── StatusColors.kt            -- status -> Color mapping
│   │   └── UiState.kt                 -- sealed class Loading/Success/Error/Empty
│   │
│   └── screen/
│       ├── onboarding/                -- 3 screen
│       ├── auth/                      -- login, register, forgot
│       ├── staff/
│       │   ├── home/
│       │   ├── manage/                -- pasien, dokter, petugas, tes, obat
│       │   ├── laporan/               -- list by status, detail
│       │   ├── notifikasi/
│       │   └── profile/
│       └── patient/
│           ├── home/
│           ├── hasil/
│           ├── riwayat/
│           ├── notifikasi/
│           └── profile/
│
├── (existing) auth/, model/, repository/, util/, adapter/
│   auth/   -- DIKOSONGKAN (logic pindah ke ui/screen/auth)
│   adapter/-- DIKOSONGKAN (logic pindah ke ui/component + LazyColumn)
│   model/  -- TETAP (reuse)
│   repository/ -- TETAP (reuse)
│   util/   -- TETAP (reuse, tambah StatusColors ke ui/util)
│
└── res/
    ├── values/
    │   ├── colors.xml                 -- diminimalkan (untuk splash + status bar)
    │   ├── strings.xml                -- string resources
    │   └── themes.xml                 -- splash theme (Material3)
    ├── values-night/
    │   └── themes.xml                 -- splash dark
    ├── drawable/                      -- ikon launcher + ilustrasi vector
    │   ├── ic_launcher_foreground.xml
    │   ├── ic_launcher_background.xml
    │   ├── ic_doctor_hero.xml
    │   ├── ic_medical_record.xml
    │   ├── ic_notification_hero.xml
    │   ├── ic_empty_lab.xml
    │   ├── ic_empty_notification.xml
    │   ├── ic_empty_history.xml
    │   ├── ic_avatar_default.xml
    │   └── ic_splash_logo.xml
    ├── font/
    │   ├── plus_jakarta_sans_regular.ttf
    │   ├── plus_jakarta_sans_medium.ttf
    │   ├── plus_jakarta_sans_semibold.ttf
    │   ├── plus_jakarta_sans_bold.ttf
    │   ├── inter_regular.ttf
    │   ├── inter_medium.ttf
    │   └── inter_semibold.ttf
    └── mipmap-*                       -- launcher icons (TETAP)
```

### 6.3 State Management Pattern

ViewModel existing **dipertahankan**. Tambahkan `StateFlow<UiState>` di samping `LiveData` (backward-compatible).

```kotlin
class PatientHomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    fun loadHome() { ... }
}

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
    object Empty : UiState<Nothing>
}
```

Composable observe:

```kotlin
@Composable
fun PatientHomeScreen(
    viewModel: PatientHomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PatientHomeContent(state, onAction = viewModel::onAction)
}
```

### 6.4 Dependencies (Build Updates)

`gradle/libs.versions.toml` — tambahkan:

```toml
[versions]
composeBom = "2024.10.01"
composeCompiler = "1.5.15"
activityCompose = "1.9.3"
lifecycleViewmodelCompose = "2.8.7"
navigationCompose = "2.8.4"
material3 = "1.3.1"
hiltNavigationCompose = "1.2.0"   # optional
datastorePreferences = "1.1.1"

[libraries]
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-text-google-fonts = { group = "androidx.compose.ui", name = "ui-text-google-fonts" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3", version.ref = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-compose-foundation = { group = "androidx.compose.foundation", name = "foundation" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewmodelCompose" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastorePreferences" }
```

`app/build.gradle.kts` — perubahan:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.compose)  // atau kotlin("plugin.compose") versi 2.0+
}

android {
    buildFeatures {
        compose = true
        // viewBinding = true  -- DIHAPUS
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    // Tambah Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    // Hapus
    // implementation(libs.navigation.fragment.ktx)  -- DIHAPUS
    // implementation(libs.navigation.ui.ktx)        -- DIHAPUS
    // implementation(libs.androidx.viewpager2)      -- DIHAPUS

    // Firebase & existing
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    testImplementation(libs.junit)
    debugImplementation(libs.androidx.compose.ui.tooling)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
```

---

## 7. Screen Inventory & Behavior

### 7.1 Onboarding (3 halaman)

| Field | Isi |
|---|---|
| Route | `onboarding/1`, `/2`, `/3` |
| Header | TopAppBar dengan tombol "Lewati" (skip to Login) |
| Body | `OnboardingPage`: HeroCard dengan ilustrasi + Title (Headline Medium) + Description (Body Medium) + Page indicator (3 dots) |
| Bottom | Filled Button "Lanjut" / "Mulai" (halaman 3) |
| Behavior | Swipe horizontal; tombol "Lanjut" advance; "Lewati" → Login |

**Konten**:
1. "Selamat Datang di MediLab" + ilustrasi dokter
2. "Akses Hasil Lab dengan Mudah" + ilustrasi medical record
3. "Pantau Progress Real-time" + ilustrasi notifikasi

### 7.2 Auth Screens

#### Login (`auth/login`)

| Field | Isi |
|---|---|
| Top | TopAppBar transparan dengan back button (jika reachable dari onboarding) |
| Body | Logo + Title "Masuk ke MediLab" + Subtitle + Email TextField + Password TextField + Lupa password? link + Filled Button "Masuk" + divider "atau" + Outlined Button "Daftar sebagai Pasien" |
| Behavior | Validasi email & password (min 6 char), panggil `authViewModel.login`, navigasi ke StaffRoot/PatientRoot berdasarkan role |

#### Register (`auth/register`)

| Field | Isi |
|---|---|
| Body | Title "Daftar sebagai Pasien" + 6 TextField (Nama, Email, Password, No HP, Alamat, Tanggal Lahir) + Filled Button "Daftar" |
| Behavior | Validasi semua field, panggil `authViewModel.registerPatient`, success → back to Login (auto-login) |

#### Forgot Password (`auth/forgot`)

| Field | Isi |
|---|---|
| Body | Title "Reset Password" + Subtitle + Email TextField + Filled Button "Kirim Email Reset" |
| Behavior | Panggil `authViewModel.sendPasswordReset`, snackbar feedback |

### 7.3 Patient Screens

#### Patient Home (`patient/home`)

| Section | Komponen |
|---|---|
| TopAppBar | Greeting "Halo, {nama}" + notifikasi bell icon + avatar |
| Hero | HeroCard "Selamat datang" dengan ilustrasi dokter mini |
| Status Laporan | SectionHeader "Status Laporan" + LaporanCard dengan progress bar |
| Riwayat Terakhir | SectionHeader "Riwayat Terakhir" + 3 LaporanCard mini |
| Empty | Jika tidak ada laporan: EmptyState dengan ilustrasi + "Belum ada laporan" |
| Bottom Nav | 4 items: Home (active), Hasil, Riwayat, Profile |

#### Patient Hasil (`patient/hasil`)

- TopAppBar: "Hasil Pemeriksaan" + search icon
- LazyColumn: `LaporanCard` filtered `status = SELESAI`
- Empty: EmptyState "Belum ada hasil"

#### Detail Hasil (`patient/laporan/{id}`)

- TopAppBar: "Detail Laporan" + back
- Scrollable Column:
  1. Header card: Laporan ID + StatusChip + tanggal
  2. Info Pasien card: nama, no RM, tanggal lahir
  3. Info Dokter card: nama, spesialis
  4. Parameter Hasil card: tabel nilai + normal range + status (color-coded)
  5. Diagnosa card
  6. Resep Obat card: list ResepItem
  7. Info RS card: nama, alamat, kota, waktu
- Bottom: (future) "Download PDF" button

#### Patient Riwayat (`patient/riwayat`)

- TopAppBar: "Riwayat Rekam Medis"
- LazyColumn timeline: RekamMedisCard dengan tanggal, RS, diagnosa singkat
- Empty: EmptyState "Belum ada riwayat"

#### Notifikasi (Patient & Staff)

- TopAppBar: "Notifikasi" + "Tandai semua dibaca" action
- LazyColumn: NotifikasiItem dengan unread dot
- Tap → mark as read + navigate (jika ada deep link)

#### Profile (Patient & Staff)

- TopAppBar: "Profile"
- Hero card: Avatar + Nama + ID + Role
- List items:
  - Edit Profile (chevron right) → modal/sheet
  - Ganti Password → modal/sheet
  - Dark Mode (Switch, on/off)
  - Tentang Aplikasi
- Bottom: Outlined Button "Logout" (red, with confirm dialog)

### 7.4 Staff Screens

#### Staff Home (`staff/home`)

| Section | Komponen |
|---|---|
| TopAppBar | Greeting "Halo, {nama}" + role chip + notifikasi bell + avatar |
| Quick Stats | Row 3 StatCard: Baru (count) / Proses (count) / Selesai (count) |
| Perlu Verifikasi | SectionHeader + LazyColumn of LaporanCard (status=VERIFIKASI) |
| Empty | "Tidak ada yang perlu diverifikasi" |
| Bottom Nav | 5 items: Home (active), Manage, Laporan, Notif, Profile |

#### Manage (`staff/manage`)

- TopAppBar: "Kelola Data" + search icon
- PrimaryTabRow: Pasien | Dokter | Petugas | Tes | Obat (5 tabs)
- Per tab: LazyColumn + FAB "+" (kanan bawah)
- FAB → bottom sheet form (add)
- Tap card → bottom sheet form (edit)
- Long press / swipe → delete (dengan confirm dialog)

#### Laporan (`staff/laporan`)

- TopAppBar: "Laporan Hasil Lab" + filter icon
- PrimaryTabRow: Baru | Proses | Verifikasi | Revisi | Selesai | Ditolak (6 tabs)
- Per tab: LazyColumn of LaporanCard
- Tap card → LaporanDetail

#### Laporan Detail (Staff) (`staff/laporan/{id}`)

- TopAppBar: "Detail Laporan" + back + more menu (delete)
- Scrollable:
  1. Header: Laporan ID + StatusChip + tanggal
  2. Info Pasien
  3. Info Dokter
  4. Parameter Hasil (form input jika status=Baru/Proses/Revisi)
  5. Diagnosa (text input)
  6. Resep Obat (add/edit list)
  7. Info RS
- Bottom action bar (per role & status):
  - **Petugas, status=Baru**: Filled "Mulai Proses" + Outlined "Tolak"
  - **Petugas, status=Proses**: Filled "Kirim Verifikasi"
  - **Petugas, status=Revisi**: Filled "Kirim Verifikasi" (re-submit)
  - **Admin, status=Verifikasi**: Filled "Setujui" + Outlined "Minta Revisi"
  - **Admin, status=Selesai**: read-only
  - **Any**: Outlined "Batalkan" (jika bukan SELESAI)

---

## 8. Empty / Loading / Error States

Standar untuk semua screen yang load data:

```kotlin
@Composable
fun <T> StateHandler(
    state: UiState<T>,
    onRetry: () -> Unit,
    emptyContent: @Composable () -> Unit = { EmptyState(...) },
    content: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> LoadingState()
        is UiState.Error -> ErrorState(message = state.message, onRetry = onRetry)
        is UiState.Empty -> emptyContent()
        is UiState.Success -> content(state.data)
    }
}
```

- **Loading**: Centered `CircularProgressIndicator` (40dp, primary color), dengan optional label "Memuat..."
- **Empty**: Ilustrasi (sesuai konteks) + Title (Headline Small) + Description (Body Medium) + optional CTA Button
- **Error**: Icon warning + Title "Terjadi Kesalahan" + Message (Body Medium) + Filled Button "Coba Lagi"

---

## 9. Animasi & Micro-interactions

| Interaction | Animasi |
|---|---|
| Page transition | Compose Navigation default (slide 300ms) |
| Bottom sheet | Slide up + scrim fade (Material 3 default) |
| Card press | Ripple (default) + scale 0.98 (custom Modifier) |
| Status chip change | `animateColorAsState` saat background berubah |
| Progress bar | `animateFloatAsState` dari 0 ke value |
| Skeleton loading | `ShimmerBox` dengan `infiniteRepeatable` gradient |
| Snackbar | Slide up + auto-dismiss (Material 3) |
| FAB press | Scale 0.95 dengan `spring` |
| Dark mode toggle | `Crossfade` 300ms antar theme |

**prefers-reduced-motion**: Tambah check via `AccessibilityManager` (atau `Settings.Global.ANIMATOR_DURATION_SCALE` di level activity) dan disable animasi non-essential (cukup gunakan instant state change).

---

## 10. Accessibility

- **Touch target**: minimum 48dp x 48dp (Compose default + custom enforcement di custom button)
- **Contrast ratio**: 4.5:1 untuk body text, 3:1 untuk large text (verify via WCAG checker)
- **ContentDescription**: semua `Image`/`Icon` punya `contentDescription`
- **Semantics**: gunakan `Modifier.semantics { contentDescription = "..." }` untuk element interaktif
- **Focus**: `Modifier.focusable()` + visible focus indicator
- **Font scaling**: respect `LocalDensity.current.fontScale`; test pada 200% scale
- **TalkBack**: setiap screen punya title yang informatif

---

## 11. DataStore (Dark Mode Persistence)

```kotlin
val Context.darkModeDataStore by preferencesDataStore(name = "dark_mode_prefs")

class DarkModeViewModel(private val context: Context) : ViewModel() {
    val isDarkMode: StateFlow<Boolean> = context.darkModeDataStore.data
        .map { it[KEY_DARK_MODE] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun toggle() {
        viewModelScope.launch {
            context.darkModeDataStore.edit { it[KEY_DARK_MODE] = !it[KEY_DARK_MODE] }
        }
    }

    companion object { val KEY_DARK_MODE = booleanPreferencesKey("is_dark_mode") }
}
```

---

## 12. Migration Strategy (Detail)

### 12.1 Yang Dipertahankan (REUSE)

| Layer | Status |
|---|---|
| `model/*` (User, Dokter, Laporan, Rujukan, RekamMedis, Notifikasi, Obat, AuditLog, Pemeriksaan) | TETAP, tidak diubah |
| `repository/*` (Auth, User, Laporan, Dokter, Pemeriksaan, Obat, Notifikasi) | TETAP, interface tidak berubah |
| `util/Constants.kt`, `util/Validators.kt`, `util/DateUtils.kt` | TETAP |
| `MediLabApp.kt` | TETAP (Application class + Firebase init) |
| Firebase schema & security rules | TETAP |
| `google-services.json` | TETAP |

### 12.2 Yang Direplace

| Layer | Replacement |
|---|---|
| Semua `Activity` (LoginActivity, RegisterPatientActivity, StaffActivity, PatientActivity) | Hapus, pindah ke `MainActivity` + Compose routes |
| Semua `Fragment` (45+ file) | Hapus, ganti dengan Composable Screen |
| `res/layout/*` (25 file XML) | Hapus |
| `res/navigation/*` (2 file XML) | Hapus |
| `res/menu/*` (2 file XML) | Hapus |
| `viewBinding` enabled | Hapus dari build.gradle |
| `navigation-fragment-ktx`, `navigation-ui-ktx`, `viewpager2` | Hapus dari dependencies |
| `adapter/*` (3 file: LaporanAdapter, PasienAdapter, RiwayatAdapter) | Hapus (logic pindah ke LazyColumn + Composable item) |

### 12.3 Yang Ditambah

- Package `ui/` (theme, component, navigation, illustration, screen)
- Composable screens (40+ screen)
- 8 file vector drawable untuk ilustrasi
- 7 file font (Plus Jakarta Sans 4 weight + Inter 3 weight)
- DataStore preferences untuk dark mode
- `DarkModeViewModel`

### 12.4 Sequence Migrasi (Incremental)

Task sequencing detail akan di implementation plan. Urutan high-level:

1. Setup Compose dependencies + theme (foundation)
2. Single MainActivity + AppNavHost + routes (skeleton)
3. Component library (foundation components)
4. Onboarding + Auth (public pages, bisa di-test dulu)
5. Patient portal (priority utama, consumer-facing)
6. Staff portal — Home + Manage (data CRUD)
7. Staff portal — Laporan (core business flow)
8. Profile + Notifikasi + Dark mode (cross-cutting)
9. Cleanup: hapus XML/Fragment/Activity lama, verify build, smoke test

---

## 13. Verification

| Item | Cara verifikasi |
|---|---|
| Build success | `./gradlew assembleDebug --no-daemon` harus `BUILD SUCCESSFUL` setiap task |
| Single MainActivity | Hanya ada 1 Activity di AndroidManifest |
| Compose stack aktif | `buildFeatures.compose = true` di build.gradle |
| Onboarding flow | Splash → Onboarding1 → 2 → 3 → Login (manual swipe) |
| Login → role routing | Login sebagai admin → StaffHome, sebagai pasien → PatientHome |
| Bottom nav navigasi | Tap tiap item bottom nav ganti screen |
| Dark mode toggle | Toggle di Profile, app re-render dengan dark color, persists setelah restart |
| Laporan flow | Buat laporan baru di staff → muncul di patient home dengan progress |
| Status transition | Dari status Baru → Proses → Verifikasi → Selesai (full cycle) |
| Empty state | Buat akun baru, lihat empty state di semua list |
| Loading state | Slow network (throttle) → lihat shimmer/progress |
| Error state | Airplane mode → coba load → lihat error + retry |
| Build size | `apkanalyzer` check APK size reasonable (< 20MB) |
| Font rendering | Text terlihat jelas, tidak ada tofu box |

---

## 14. Risiko & Mitigasi

| Risiko | Dampak | Mitigasi |
|---|---|---|
| Effort migrasi besar (55 Kotlin + 42 XML files) | Schedule overrun | Sequence task per portal; setiap task punya verifiable deliverable |
| ViewModel `LiveData` → `StateFlow` breaking | Regress | Tidak wajib migrasi; observe LiveData via `observeAsState()` di Compose |
| Compose Navigation nested complexity | Bug nav | Single source of truth: `sealed class Route`; nested NavHost dengan `startDestination` jelas |
| Font loading dari Google Fonts offline | Blank text | Download TTF, bundle di `res/font/`; fallback ke system font |
| Dark mode toggle butuh re-render seluruh tree | Performance | `MediLabTheme` read dari `StateFlow` di root; Compose handle re-composition otomatis |
| 40+ screen baru = banyak kode sekaligus | Quality | Component library pertama, screens pakai component; review per screen |
| Ilustrasi vector banyak = APK size | Size | Single SVG path converted ke `ImageVector`; simple geometry; max 4-6 ilustrasi |
| Build errors sulit di-debug di Compose | Velocity | Build setiap task; incremental approach |
| Lost business logic saat rewrite UI | Regression | Reuse Repository & ViewModel; hanya rewrite UI layer |
| Firebase + Compose interop issues | Crash | Tidak ada interop layer; Compose call ViewModel call Repository call Firebase |

---

## 15. Out of Scope

Yang **TIDAK** dilakukan dalam redesign ini (bisa di-spec terpisah):

- Push notification (FCM) — saat ini pakai in-app notification only
- PDF generation/download untuk hasil lab
- Image upload untuk foto profile (saat ini default avatar)
- Biometric login (fingerprint/face)
- Offline-first dengan Room caching
- Unit test coverage besar (hanya smoke test manual)
- Multi-bahasa (i18n) — saat ini bahasa Indonesia saja
- Tablet/landscape layout optimization
- Accessibility audit lengkap (WCAG AAA)

---

## 16. File yang Akan Diubah / Dihapus / Ditambah

### Dihapus
- `app/src/main/java/com/example/medilab/MainActivity.kt` (di-replace)
- `app/src/main/java/com/example/medilab/auth/{Login,RegisterPatient}Activity.kt`
- `app/src/main/java/com/example/medilab/auth/AuthViewModel.kt` (pindah ke `ui/screen/auth/`)
- `app/src/main/java/com/example/medilab/staff/StaffActivity.kt`
- `app/src/main/java/com/example/medilab/patient/PatientActivity.kt`
- Semua 45+ Fragment di `staff/` dan `patient/`
- Semua ViewModel di `staff/` dan `patient/` (akan di-recreate di `ui/screen/`)
- `app/src/main/java/com/example/medilab/adapter/*.kt`
- `app/src/main/res/layout/*.xml` (semua)
- `app/src/main/res/navigation/*.xml`
- `app/src/main/res/menu/*.xml`
- `viewBinding` di `app/build.gradle.kts`

### Diubah
- `app/build.gradle.kts` (Compose setup, hapus viewBinding/fragment nav)
- `gradle/libs.versions.toml` (tambah Compose deps)
- `app/src/main/AndroidManifest.xml` (1 Activity, theme)
- `app/src/main/java/com/example/medilab/MediLabApp.kt` (tambah init dark mode datastore)
- `app/src/main/res/values/themes.xml` (splash theme)
- `app/src/main/res/values/colors.xml` (minimalis, untuk splash)
- `app/src/main/res/values/strings.xml` (string resources lengkap)

### Ditambah
- 40+ file Composable screen di `ui/screen/`
- 19 file Component library di `ui/component/`
- 5 file Navigation di `ui/navigation/`
- 5 file Theme di `ui/theme/`
- 4 file Illustration di `ui/illustration/`
- 2 file util di `ui/util/`
- 1 `DarkModeViewModel`
- 1 `MainActivity` baru
- 8 vector drawable untuk ilustrasi
- 7 font TTF di `res/font/`

---

## 17. Definisi of Done

Redesign dianggap selesai ketika:

1. ✅ Build `assembleDebug` sukses tanpa error
2. ✅ Semua screen dalam §7 terimplementasi dan render dengan benar
3. ✅ Onboarding flow bisa dilalui (3 pages + skip)
4. ✅ Login berhasil navigate ke portal yang sesuai role
5. ✅ Bottom nav berfungsi (5 items staff, 4 items patient)
6. ✅ Laporan flow end-to-end: dokter buat → petugas proses → admin verifikasi → selesai
7. ✅ Edit Profile & Ganti Password berfungsi
8. ✅ Dark mode toggle berfungsi dan persist
9. ✅ Empty/Loading/Error state muncul sesuai kondisi
10. ✅ Tidak ada Fragment/Activity/XML layout/navigation lama yang tertinggal
11. ✅ Smoke test manual lulus untuk semua screen utama
12. ✅ Code review: setiap screen Composable < 300 baris; component terpisah untuk bagian reusable

---

**Status:** ✅ Disetujui — siap untuk implementation plan
**Next step:** Invoke `writing-plans` skill untuk membuat implementation plan detail
