package com.example.medilab.staff.laporan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.databinding.FragmentLaporanDetailBinding

class LaporanDetailFragment : Fragment() {
    private var _binding: FragmentLaporanDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LaporanViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLaporanDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LaporanViewModel::class.java]

        val laporanId = arguments?.getString("laporanId") ?: return
        viewModel.loadById(laporanId)

        viewModel.currentLaporan.observe(viewLifecycleOwner) { laporan ->
            if (laporan == null) return@observe
            binding.tvLaporanId.text = "Laporan #${laporan.id}"
            binding.tvStatus.text = "Status: ${laporan.status.uppercase()}"
            binding.tvPasien.text = "ID Pasien: ${laporan.pasienId}"
            binding.tvDokter.text = "ID Dokter: ${laporan.dokterId}"
            binding.tvHasil.text = laporan.hasilParameter.joinToString("\n") { "${it.parameterNama}: ${it.nilai} ${it.satuan} (${it.keterangan})" }
            binding.tvDiagnosa.text = laporan.diagnosa
            binding.tvResep.text = laporan.resepObat.joinToString("\n") { "${it.namaObat} ${it.dosis}${it.satuan} - ${it.aturanPakai}" }
            binding.tvRS.text = "${laporan.rumahSakit.nama}\n${laporan.rumahSakit.alamat}, ${laporan.rumahSakit.kota}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
