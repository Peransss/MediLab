package com.example.medilab.staff.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.medilab.databinding.FragmentManageContainerBinding
import com.example.medilab.staff.manage.dokter.ManageDokterFragment
import com.example.medilab.staff.manage.obat.ManageObatFragment
import com.example.medilab.staff.manage.pasien.ManagePasienFragment
import com.example.medilab.staff.manage.pemeriksaan.ManagePemeriksaanFragment
import com.example.medilab.staff.manage.petugas.ManagePetugasFragment
import com.google.android.material.tabs.TabLayoutMediator

class ManageContainerFragment : Fragment() {
    private var _binding: FragmentManageContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabTitles = listOf("Pasien", "Dokter", "Petugas", "Master Tes", "Obat")
        val fragments = listOf(
            ManagePasienFragment(),
            ManageDokterFragment(),
            ManagePetugasFragment(),
            ManagePemeriksaanFragment(),
            ManageObatFragment()
        )

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
