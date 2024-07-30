package com.example.mobilemonitoringbankbpr.fragment

import CustomTypefaceSpan
import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.databinding.FragmentMainBinding
import com.example.mobilemonitoringbankbpr.viewmodel.AccountViewModel
import com.google.android.material.navigation.NavigationView

//class MainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
//
//    private var _binding: FragmentMainBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var accountViewModel: AccountViewModel
//    private lateinit var localStorage: LocalStorage
//    private var loadingDialog: AlertDialog? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentMainBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        Log.d("MainFragment", "View created")
//        drawerLayout = binding.drawerLayout
//
//        val toolbar = binding.toolbar
//        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
//
//        // Ubah warna ikon navigasi menjadi hitam
//        val navigationIcon = toolbar.navigationIcon
//        navigationIcon?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//
//        val navigationView = binding.navView
//        navigationView.setNavigationItemSelectedListener(this)
//        setMenuFont(navigationView.menu)
//        val toggle = ActionBarDrawerToggle(
//            requireActivity(), drawerLayout, toolbar,
//            R.string.open_nav, R.string.close_nav
//        )
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        // Inisialisasi LocalStorage
//        localStorage = LocalStorage(requireContext())
//
//        // Ambil jabatan user dari LocalStorage
//        val jabatan = localStorage.jabatan
//
//        // Sesuaikan menu berdasarkan jabatan user
//        val menu = navigationView.menu
//        if (jabatan != 5) {
//            menu.findItem(R.id.nav_surat).isVisible = false
//        }
//
//        if (savedInstanceState == null) {
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, AccountFragment()).commit()
//            navigationView.setCheckedItem(R.id.nav_account)
//            binding.toolbar.title = getString(R.string.judul_account) // Set initial title
//        }
//
//        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
//
//        accountViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
//            if (isLoading) {
//                showLoadingDialog()
//            } else {
//                dismissLoadingDialog()
//            }
//        })
//    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.nav_account -> {
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, AccountFragment()).commit()
//                binding.toolbar.title = getString(R.string.judul_account)
//                Log.d("MainFragment", "Navigasi ke Akun")
//            }
//            R.id.nav_monitoring -> {
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, MonitoringFragment()).commit()
//                binding.toolbar.title = getString(R.string.judul_monitoring)
//                Log.d("MainFragment", "Navigasi ke Monitoring")
//            }
//            R.id.nav_surat -> {
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, SuratFragment()).commit()
//                binding.toolbar.title = getString(R.string.judul_surat)
//                Log.d("MainFragment", "Navigasi ke Surat")
//            }
//            R.id.nav_logout -> {
//                logout()
//                return true
//            }
//        }
//        drawerLayout.closeDrawer(GravityCompat.START)
//        return true
//    }
//
//    private fun logout() {
//        accountViewModel.logout { success ->
//            if (success) {
//                val navOptions = NavOptions.Builder()
//                    .setPopUpTo(R.id.mainFragment, true)
//                    .build()
//                findNavController().navigate(R.id.action_mainFragment_to_loginFragment, null, navOptions)
//            } else {
//                Toast.makeText(requireContext(), "Logout gagal", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//    private fun showLoadingDialog() {
//        if (loadingDialog == null) {
//            loadingDialog = AlertDialog.Builder(requireContext())
//                .setView(R.layout.dialog_loading)
//                .setCancelable(false)
//                .create()
//        }
//        loadingDialog?.show()
//    }
//
//    private fun dismissLoadingDialog() {
//        loadingDialog?.dismiss()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//    private fun setMenuItemFont(menuItem: MenuItem, font: Typeface?) {
//        val spanString = SpannableString(menuItem.title)
//        spanString.setSpan(CustomTypefaceSpan("", font), 0, spanString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//        menuItem.title = spanString
//    }
//    private fun setMenuFont(menu: Menu) {
//        val font = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold) // Ganti dengan font Anda
//
//        for (i in 0 until menu.size()) {
//            val menuItem = menu.getItem(i)
//            setMenuItemFont(menuItem, font)
//
//            // Safe call pada subMenu
//            menuItem.subMenu?.let { subMenu ->
//                for (j in 0 until subMenu.size()) {
//                    setMenuItemFont(subMenu.getItem(j), font)
//                }
//            }
//        }
//    }
//
//}
class MainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var localStorage: LocalStorage
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

        // Inisialisasi LocalStorage
        localStorage = LocalStorage(requireContext())

        // Ambil jabatan user dari LocalStorage
        val jabatan = localStorage.jabatan

        // Sesuaikan menu berdasarkan jabatan user
        val menu = navigationView.menu
        if (jabatan != 5) {
            menu.findItem(R.id.nav_surat).isVisible = false
        }

        // Set font untuk menu
        setMenuFont(menu)

        // Tampilkan nama user di header navigation view
        val headerView = navigationView.getHeaderView(0)
        val namaUserTextView = headerView.findViewById<TextView>(R.id.namaUser)
        val namaUser = localStorage.name
        namaUserTextView.text = namaUser

        if (savedInstanceState == null) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AccountFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_account)
//            binding.toolbar.title = getString(R.string.judul_account)
            setToolbarTitle(getString(R.string.judul_account))
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

    private fun setMenuFont(menu: Menu) {
        val font = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold) // Ganti dengan font Anda
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            setMenuItemFont(menuItem, font)

            // Safe call pada subMenu
            menuItem.subMenu?.let { subMenu ->
                for (j in 0 until subMenu.size()) {
                    setMenuItemFont(subMenu.getItem(j), font)
                }
            }
        }
    }
    private fun setToolbarTitle(title: String) {
        val font = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold) // Ganti dengan font Anda
        val spannableString = SpannableString(title)
        font?.let {
            spannableString.setSpan(CustomTypefaceSpan("", it), 0, spannableString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.title = spannableString
    }
    private fun setMenuItemFont(menuItem: MenuItem, font: Typeface?) {
        val spannableString = SpannableString(menuItem.title)
        font?.let {
            spannableString.setSpan(CustomTypefaceSpan("", it), 0, spannableString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        menuItem.title = spannableString
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_user -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AdminFragment()).commit()
//                binding.toolbar.title = getString(R.string.judul_account)
                setToolbarTitle(getString(R.string.judul_account))

                Log.d("MainFragment", "Navigasi ke Akun")
            }
            R.id.nav_account -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AccountFragment()).commit()
//                binding.toolbar.title = getString(R.string.judul_account)
                setToolbarTitle(getString(R.string.judul_account))

                Log.d("MainFragment", "Navigasi ke Akun")
            }
            R.id.nav_monitoring -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MonitoringFragment()).commit()
//                binding.toolbar.title = getString(R.string.judul_monitoring)
                setToolbarTitle(getString(R.string.judul_monitoring))

                Log.d("MainFragment", "Navigasi ke Monitoring")
            }
            R.id.nav_surat -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SuratFragment()).commit()
//                binding.toolbar.title = getString(R.string.judul_surat)
                setToolbarTitle(getString(R.string.judul_surat))

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

    private fun logout() {
        accountViewModel.logout { success ->
            if (success) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.mainFragment, true)
                    .build()
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment, null, navOptions)
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







