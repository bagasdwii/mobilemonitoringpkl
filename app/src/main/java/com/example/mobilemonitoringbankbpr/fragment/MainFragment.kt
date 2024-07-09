package com.example.mobilemonitoringbankbpr.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.databinding.FragmentMainBinding
import com.example.mobilemonitoringbankbpr.viewmodel.AccountViewModel
import com.google.android.material.navigation.NavigationView

class MainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var accountViewModel: AccountViewModel
    private var loadingDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("MainFragment", "View created")
        drawerLayout = binding.drawerLayout

        val toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        // Ubah warna ikon navigasi menjadi hitam
        val navigationIcon = toolbar.navigationIcon
        navigationIcon?.setTint(ContextCompat.getColor(requireContext(), R.color.black))

        val navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            requireActivity(), drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AccountFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_account)
            binding.toolbar.title = getString(R.string.judul_account) // Set initial title
        }

        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        accountViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                showLoadingDialog()
            } else {
                dismissLoadingDialog()
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_account -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AccountFragment()).commit()
                binding.toolbar.title = getString(R.string.judul_account)
                Log.d("MainFragment", "Navigasi ke Akun")
            }
            R.id.nav_monitoring -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MonitoringFragment()).commit()
                binding.toolbar.title = getString(R.string.judul_monitoring)
                Log.d("MainFragment", "Navigasi ke Monitoring")
            }
            R.id.nav_surat -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SuratFragment()).commit()
                binding.toolbar.title = getString(R.string.judul_surat)
                Log.d("MainFragment", "Navigasi ke Surat")
            }
            R.id.nav_logout -> {
                logout()
                return true
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

//    private fun logout() {
//        accountViewModel.isLoading.value = true
//        val url = getString(R.string.api_server) + "/logoutmobile"
//        Log.d("MainFragment", "Mulai proses logout")
//        Thread {
//            val http = Http(requireContext(), url)
//            http.setMethod("POST")
//            http.setToken(true)
//            http.send()
//
//            requireActivity().runOnUiThread {
//                val code = http.getStatusCode()
//                if (code == 200) {
//                    Log.d("MainFragment", "Logout berhasil")
//                    val localStorage = LocalStorage(requireContext())
//                    localStorage.token = null
//                    localStorage.userId = -1
//                    findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
//                } else {
//                    Log.e("MainFragment", "Logout gagal dengan kode: $code")
//                    Toast.makeText(requireContext(), "Error $code", Toast.LENGTH_SHORT).show()
//                }
//                accountViewModel.isLoading.value = false
//            }
//        }.start()
//    }
    private fun logout() {
        accountViewModel.logout { success ->
            if (success) {
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
            } else {
                Toast.makeText(requireContext(), "Logout gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_loading)
                .setCancelable(false)
                .create()
        }
        loadingDialog?.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}







