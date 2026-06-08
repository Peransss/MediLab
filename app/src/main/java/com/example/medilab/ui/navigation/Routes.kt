package com.example.medilab.ui.navigation

sealed class Route(val path: String) {
    object Splash : Route("splash")

    object Onboarding1 : Route("onboarding/1")
    object Onboarding2 : Route("onboarding/2")
    object Onboarding3 : Route("onboarding/3")

    object Login : Route("auth/login")
    object Register : Route("auth/register")
    object ForgotPassword : Route("auth/forgot")

    object StaffRoot : Route("staff")
    object StaffHome : Route("staff/home")
    object StaffManage : Route("staff/manage")
    object StaffLaporan : Route("staff/laporan")
    object StaffNotifikasi : Route("staff/notifikasi")
    object StaffProfile : Route("staff/profile")
    object StaffLaporanDetail : Route("staff/laporan/{laporanId}") {
        fun build(id: String) = "staff/laporan/$id"
    }

    object PatientRoot : Route("patient")
    object PatientHome : Route("patient/home")
    object PatientHasil : Route("patient/hasil")
    object PatientBooking : Route("patient/booking") // ✅ baru
    object PatientRiwayat : Route("patient/riwayat")
    object PatientNotifikasi : Route("patient/notifikasi")
    object PatientProfile : Route("patient/profile")
    object LaporanDetail : Route("patient/laporan/{laporanId}") {
        fun build(id: String) = "patient/laporan/$id"
    }
}