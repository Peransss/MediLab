# MediLab – Design Documentation

> **Re-Design UI MediLab** · Healthcare Design System · Modern, Friendly, and Easy to Use
> Platform: Android · Theme: Light + Manual Dark Mode · Language: Bahasa Indonesia

---

## Table of Contents

1. [Design System](#1-design-system)
2. [Screens Overview](#2-screens-overview)
3. [Screen Specifications](#3-screen-specifications)
   - 3.1 Splash Screen
   - 3.2 Login Screen
   - 3.3 Home Screen
   - 3.4 Hasil Pemeriksaan (Empty State)
   - 3.5 Riwayat Rekam Medis (Empty State)
   - 3.6 Profile Screen
   - 3.7 Empty State Component
4. [Component Library](#4-component-library)
5. [Navigation](#5-navigation)
6. [Folder Structure](#6-folder-structure)
7. [Code Reference – EmptyState.kt](#7-code-reference--emptystatekt)

---

## 1. Design System

### 1.1 Color Palette — Healthcare Teal/Cyan

| Token | Hex | Usage |
|-------|-----|-------|
| `Primary` | `#0891B2` | Buttons, active nav, accents (teal) |
| `Primary Container` | `#CFFAFE` | Light teal backgrounds |
| `Secondary` | `#475569` | Secondary text, muted elements (slate) |
| `Tertiary` | `#059669` | CTA green buttons, success accents |
| `Background` | `#F0FDFA` | App background (light mint) |
| `Surface` | `#FFFFFF` | Card / bottom nav background |
| `Surface Variant` | `#E0F2FE` | Input field, secondary card (sky tint) |
| `On Background` | `#1E293B` | Primary text (dark slate) |
| `On Surface Variant` | `#64748B` | Secondary / helper text |
| `Success` | `#059669` | "Selesai" status badge, green accents |
| `Warning` | `#F59E0B` | Pending state |
| `Error` | `#EF4444` | Logout button, destructive actions |
| `CTA Green` | `#059669` | Primary action buttons (register, login, save) |

### 1.2 Typography

| Style | Font | Size | Weight | Usage |
|-------|------|------|--------|-------|
| `displayLarge` | Figtree | 36 sp | Bold | Hero display text |
| `headlineLarge` | Figtree | 24 sp | Bold | Screen title |
| `headlineMedium` | Figtree | 20 sp | SemiBold | Section heading |
| `titleLarge` | Figtree | 22 sp | Bold | Card heading |
| `titleMedium` | Noto Sans | 16 sp | SemiBold | Card subtitle |
| `bodyLarge` | Noto Sans | 16 sp | Regular | Body text |
| `bodyMedium` | Noto Sans | 14 sp | Regular | Subtitles, descriptions |
| `bodySmall` | Noto Sans | 12 sp | Regular | Timestamps, hints |
| `labelLarge` | Noto Sans | 14 sp | Medium | Button labels |
| `labelMedium` | Noto Sans | 12 sp | Medium | Badges, nav labels |
| `labelSmall` | Noto Sans | 11 sp | Medium | Small badges |

### 1.3 Shape & Corner Radius

| Element | Radius |
|---------|--------|
| Card / Container | 16 dp |
| Button (full-width) | 12 dp |
| Input field | 12 dp |
| Chip / Badge | 50 dp (pill) |
| Icon container (circle) | 50% |
| Bottom nav | 0 dp (flat top) |

### 1.4 Spacing Scale

| Name | Value |
|------|-------|
| `space-xs` | 4 dp |
| `space-sm` | 8 dp |
| `space-md` | 16 dp |
| `space-lg` | 24 dp |
| `space-xl` | 32 dp |
| `screen-padding` | 24 dp (horizontal) |

### 1.5 Elevation & Shadows

- Cards use `elevation = 1.dp` with a soft shadow.
- Bottom navigation uses `elevation = 8.dp`.
- No hard drop shadows — use translucent surface colors instead.

### 1.6 Iconography

- Icon set: **Material Symbols Outlined** (outlined variant for inactive, filled for active)
- Size: 24 dp
- Active color: `Primary (#0891B2)` (teal)
- Inactive color: `On Surface Variant (#64748B)` (slate)

---

## 2. Screens Overview

| # | Screen Name | State | Description |
|---|-------------|-------|-------------|
| 1 | Splash Screen | — | Branding intro, app logo + tagline |
| 2 | Login Screen | Default | Email & password form, register CTA |
| 3 | Home Screen | Filled | Dashboard with health summary + notifications |
| 4 | Hasil Pemeriksaan | Empty State | No results yet, CTA to start examination |
| 5 | Riwayat Rekam Medis | Empty State | No medical records, informational empty state |
| 6 | Profile Screen | Default | User info, settings list, logout |
| 7 | Empty State Component | Example | Reusable `EmptyState` component usage demo |

---

## 3. Screen Specifications

---

### 3.1 Splash Screen

**Purpose:** App entry / branding screen shown while initializing.

**Layout:**
- Background: `Background (#F0FDFA)` full screen
- Center content (vertical + horizontal):
  - 3D Illustration: lab flask / beaker (160 dp)
  - App name: **MediLab** — `titleLarge`, Bold, White
  - Tagline: *"Sistem informasi laboratorium medis yang modern dan mudah digunakan."* — `bodyMedium`, `On Surface Variant`
- Bottom: Page indicator dots (3 dots, active dot = Primary color)

**Behavior:**
- Auto-navigates to Login Screen after 2–3 seconds or on splash animation complete.

---

### 3.2 Login Screen

**Purpose:** Authenticate existing users or direct to registration.

**Layout:**
- Top section:
  - 3D Illustration: doctor character (centered, ~180 dp)
  - Heading: *"Selamat Datang Kembali 👋"* — `titleLarge`, Bold
  - Subtext: *"Masuk untuk melanjutkan"* — `bodyMedium`, `On Surface Variant`
- Form section:
  - `TextField` — Email (leading icon: envelope)
  - `TextField` — Password (leading icon: lock, trailing icon: visibility toggle)
  - Forgot password link: *"Lupa password?"* — `bodySmall`, `Primary`, right-aligned
  - Primary CTA:
  - `Button` full-width: **"Masuk"** — `CTA Green` background, White text
- Secondary CTA:
  - Text: *"Belum punya akun?"* + `TextButton` **"Daftar sebagai Pasien"** — `Primary` color

**States:**
- Default, Loading (button shows progress indicator), Error (field highlighted red with message).

---

### 3.3 Home Screen

**Purpose:** Main dashboard showing health summary, recent examinations, and notifications.

**Layout:**

```
TopAppBar
  Title: "Beranda"
  Trailing: Notification bell icon

Body (scrollable)
  Greeting
    "Halo, Khrisna Adi 👋"
    "Semoga harimu menyenangkan!"

  Summary Card (Primary gradient background)
    Title: "Ringkasan Info Kesehatan"
    Stats row (3 columns):
      • Pemeriksaan Selesai: 5
      • Menunggu Hasil:       1
      • Total Riwayat:       12

  Section: "Pemeriksaan Terbaru"
    Header + "Lihat Semua" link
    ExaminationListItem:
      - Icon (avatar/test type)
      - Title: "Hematologi Lengkap"
      - Date: "12 Juni 2026 · 09.30"
      - Status Badge: "Selesai" (green)
      - Chevron right

  Section: "Notifikasi"
    Header + "Lihat Semua" link
    NotificationListItem:
      - Icon
      - Title: "Hasil pemeriksaan Anda sudah tersedia"
      - Time: "2 jam yang lalu"
      - Chevron right

BottomNavigationBar
  Home | Hasil | Riwayat | Profile
```

**Summary Card Specs:**
- Background: `Primary (#0891B2)` with subtle gradient overlay
- Corner radius: 16 dp
- Padding: 16 dp
- Stats: Each stat in a `Column`, number in `titleMedium` Bold white, label in `bodySmall` white 80% alpha

---

### 3.4 Hasil Pemeriksaan (Empty State)

**Purpose:** Shown when the user has no examination results yet.

**Layout:**
```
TopAppBar
  Title: "Hasil Pemeriksaan"

Body (centered vertically)
  EmptyState(
    illustration = { /* 3D lab flask illustration */ },
    title = "Belum ada hasil",
    description = "Hasil pemeriksaan yang sudah selesai akan tampil di sini.",
    buttonText = "Lakukan Pemeriksaan",
    onButtonClick = { /* navigate to book examination */ }
  )

BottomNavigationBar
  Active tab: Hasil
```

**Empty State Visual:**
- Illustration size: 160 dp
- Background circle: `Primary.copy(alpha=0.1f)` (#0891B2), 160 dp circle
- Title: `titleLarge`, centered, white
- Description: `bodyMedium`, centered, `On Surface Variant`
- Button: full-width (60% screen width), `Primary` fill

---

### 3.5 Riwayat Rekam Medis (Empty State)

**Purpose:** Shown when the user has no saved medical records.

**Layout:**
```
TopAppBar
  Title: "Riwayat Rekam Medis"

Body (centered vertically)
  EmptyState(
    illustration = { /* 3D hourglass / folder illustration */ },
    title = "Belum ada riwayat",
    description = "Riwayat rekam medis Anda akan tampil di sini.",
    buttonText = null,      // No action button
    onButtonClick = null
  )

BottomNavigationBar
  Active tab: Riwayat
```

**Note:** This empty state has **no CTA button** — the user can only wait for records to appear.

---

### 3.6 Profile Screen

**Purpose:** User profile details and app settings.

**Layout:**
```
TopAppBar
  Title: "Profile"

Body (scrollable)
  ProfileHeader Card
    Avatar: 3D character illustration (64 dp circle)
    Name: "Khrisna Adi"    — titleMedium, Bold
    Role Badge: "PASIEN"   — pill chip, Primary color
    Member since: "Member sejak Juni 2026" — bodySmall, muted

  Settings List (grouped cards)
    SettingsItem: Edit Profile        (icon: person-edit)
    SettingsItem: Ganti Password      (icon: lock)
    SettingsItem: Dark Mode           (icon: moon, trailing: Toggle Switch — active)
    SettingsItem: Tentang Aplikasi    (icon: info-circle)
    SettingsItem: Bantuan             (icon: help-circle)

  Logout Button
    Full-width outlined button
    Label: "Logout"
    Color: Error (#EF4444)
    Border: Error color

BottomNavigationBar
  Active tab: Profile
```

**Settings Item Anatomy:**
- Leading: icon in 40 dp rounded container (`Surface Variant` background)
- Middle: label text (`bodyMedium`, white)
- Trailing: chevron right (`On Surface Variant`) — OR toggle switch for Dark Mode
- Divider: none; spacing between items = 2 dp

---

### 3.7 Empty State Component (Example Usage)

**Purpose:** Demonstrates the reusable `EmptyState` composable with custom content.

**Layout:**
```
Body (centered)
  EmptyState(
    illustration = { /* 3D microscope illustration */ },
    title = "Tidak ada data",
    description = "Data yang Anda cari tidak tersedia saat ini.",
    buttonText = "Coba Lagi",
    onButtonClick = { /* retry action */ }
  )
```

This screen serves as a **storybook/preview** for the `EmptyState` component.

---

## 4. Component Library

### 4.1 AppButton

**File:** `ui/components/AppButton.kt`

| Prop | Type | Description |
|------|------|-------------|
| `text` | String | Button label |
| `onClick` | () -> Unit | Click handler |
| `modifier` | Modifier | Layout modifier |
| `enabled` | Boolean | Enabled/disabled state |
| `isLoading` | Boolean | Shows CircularProgressIndicator when true |
| `variant` | ButtonVariant | `Primary`, `Outlined`, `Text`, `CTA` |

**Variants:**

- **Primary:** `Primary` fill (#0891B2), white text, 12 dp radius
- **CTA:** `CTA Green` fill (#059669), white text, 12 dp radius — for primary actions
- **Outlined:** Transparent fill, `Primary` border + text
- **Text:** No background, `Primary` text only (used for links)

---

### 4.2 AppTextField

**File:** `ui/components/AppTextField.kt`

| Prop | Type | Description |
|------|------|-------------|
| `value` | String | Current input value |
| `onValueChange` | (String) -> Unit | Change handler |
| `label` | String | Floating label text |
| `leadingIcon` | @Composable (() -> Unit)? | Optional leading icon |
| `trailingIcon` | @Composable (() -> Unit)? | Optional trailing icon |
| `isPassword` | Boolean | Toggles password visibility |
| `isError` | Boolean | Shows error styling |
| `errorMessage` | String? | Error text below field |

**Style:** `OutlinedTextField` with `Surface Variant` background, `Primary` focused border color.

---

### 4.3 TopBar

**File:** `ui/components/TopBar.kt`

| Prop | Type | Description |
|------|------|-------------|
| `title` | String | Screen title |
| `showBackButton` | Boolean | Shows navigation back arrow |
| `onBackClick` | () -> Unit | Back navigation handler |
| `trailingContent` | @Composable (() -> Unit)? | Trailing icon slot |

**Style:** `TopAppBar` with `Surface` background, `titleMedium` title, icons in `Primary` color.

---

### 4.4 EmptyState

**File:** `ui/components/EmptyState.kt`

| Prop | Type | Description |
|------|------|-------------|
| `illustration` | @Composable () -> Unit | Illustration composable (image/lottie) |
| `title` | String | Bold heading |
| `description` | String | Helper description text |
| `buttonText` | String? | Optional CTA label (null = no button) |
| `onButtonClick` | (() -> Unit)? | CTA click handler |
| `modifier` | Modifier | Layout modifier |

**Layout Logic:**
```
Column (fillMaxSize, centerHorizontally, centerVertically, padding 24dp)
  Box (160dp circle, Primary alpha 0.1f background)
    illustration()
  Spacer(8dp)
  Text(title, titleLarge, centerAlign, onBackground)
  Spacer(8dp)
  Text(description, bodyMedium, centerAlign, onSurfaceVariant)
  if (buttonText != null && onButtonClick != null)
    Spacer(24dp)
    Button(onClick = onButtonClick, fillMaxWidth(0.6f))
      Text(buttonText)
```

---

### 4.5 SummaryCard

Used on the Home Screen to display health stats.

| Prop | Type | Description |
|------|------|-------------|
| `pemeriksaanSelesai` | Int | Completed examinations count |
| `menungguHasil` | Int | Pending results count |
| `totalRiwayat` | Int | Total medical record count |

**Style:** Gradient card (`Primary (#0891B2)` → `Primary Container`), white text throughout.

---

### 4.6 ExaminationListItem

Used in "Pemeriksaan Terbaru" section.

| Prop | Type | Description |
|------|------|-------------|
| `icon` | @Composable () -> Unit | Test type icon |
| `title` | String | Examination name |
| `dateTime` | String | Formatted date & time |
| `status` | ExaminationStatus | `SELESAI`, `MENUNGGU`, `DIPROSES` |
| `onClick` | () -> Unit | Row click handler |

**Status Badge Colors:**
- `SELESAI` → `#059669` (green)
- `MENUNGGU` → `#F59E0B` (amber)
- `DIPROSES` → `Primary (#0891B2)` (teal)

---

## 5. Navigation

### 5.1 Bottom Navigation Bar

| Index | Label | Icon (Inactive) | Icon (Active) |
|-------|-------|-----------------|---------------|
| 0 | Home | `home_outlined` | `home_filled` |
| 1 | Hasil | `assignment_outlined` | `assignment_filled` |
| 2 | Riwayat | `history` | `history` (filled tint) |
| 3 | Profile | `person_outline` | `person_filled` |

Active item uses `Primary (#0891B2)` color for both icon and label.

### 5.2 Navigation Graph

```
SplashScreen
  └─► LoginScreen
        ├─► RegisterScreen (not in mockup)
        └─► MainScreen (BottomNav host)
              ├─ HomeScreen
              │    └─► ExaminationDetailScreen
              ├─ HasilPemeriksaanScreen (EmptyState)
              ├─ RiwayatRekamMedisScreen (EmptyState)
              └─ ProfileScreen
                    ├─► EditProfileScreen
                    ├─► ChangePasswordScreen
                    └─►[Logout → LoginScreen]
```

---

## 6. Folder Structure

```
app/
└── src/main/
    ├── java/.../medilab/
    │   ├── ui/
    │   │   ├── components/
    │   │   │   ├── EmptyState.kt        ← Reusable empty state
    │   │   │   ├── AppButton.kt         ← Custom button variants
    │   │   │   ├── AppTextField.kt      ← Custom input field
    │   │   │   ├── TopBar.kt            ← App top bar
    │   │   │   └── ...
    │   │   ├── screens/
    │   │   │   ├── SplashScreen.kt
    │   │   │   ├── LoginScreen.kt
    │   │   │   ├── HomeScreen.kt
    │   │   │   ├── HasilPemeriksaanScreen.kt
    │   │   │   ├── RiwayatScreen.kt
    │   │   │   └── ProfileScreen.kt
    │   │   └── theme/
    │   │       ├── Color.kt
    │   │       ├── Type.kt
    │   │       ├── Shape.kt
    │   │       └── Theme.kt
    │   ├── viewmodel/
    │   │   ├── HomeViewModel.kt
    │   │   ├── AuthViewModel.kt
    │   │   └── ProfileViewModel.kt
    │   ├── data/
    │   │   ├── repository/
    │   │   └── model/
    │   └── navigation/
    │       └── NavGraph.kt
    └── res/
        ├── drawable/          ← Icons, illustrations (SVG/PNG)
        └── values/
            └── strings.xml
```

---

## 7. Code Reference – EmptyState.kt

```kotlin
@Composable
fun EmptyState(
    illustration: @Composable () -> Unit,
    title: String,
    description: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            illustration()
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(buttonText)
            }
        }
    }
}
```

### Usage Examples

**With button (Hasil Pemeriksaan):**
```kotlin
EmptyState(
    illustration = { /* your illustration composable */ },
    title = "Belum ada hasil",
    description = "Hasil pemeriksaan yang sudah selesai akan tampil di sini.",
    buttonText = "Lakukan Pemeriksaan",
    onButtonClick = { navController.navigate("book_examination") }
)
```

**Without button (Riwayat Rekam Medis):**
```kotlin
EmptyState(
    illustration = { /* your illustration composable */ },
    title = "Belum ada riwayat",
    description = "Riwayat rekam medis Anda akan tampil di sini."
)
```

---

*Document generated from MediLab UI Re-Design mockup — June 2026*
*Updated: Healthcare Teal/Cyan design system — June 2026*
