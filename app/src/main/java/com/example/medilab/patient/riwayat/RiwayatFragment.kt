package com.example.medilab.patient.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class RiwayatFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return android.widget.TextView(requireContext()).apply { text = "Riwayat Rekam Medis" }
    }
}
