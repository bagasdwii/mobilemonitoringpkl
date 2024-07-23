package com.example.mobilemonitoringbankbpr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.mobilemonitoringbankbpr.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var localStorage: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000) // Untuk menampilkan splash screen
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi NavController setelah layout dan NavHostFragment diinisialisasi
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        localStorage = LocalStorage(this)

        // Periksa apakah token tersedia
        val token = localStorage.token

        if (token.isNullOrEmpty()) {
            // Token tidak ada, arahkan ke LoginFragment melalui NavController
            navController.navigate(R.id.loginFragment)
        } else {
            // Token ada, arahkan ke MainFragment melalui NavController dengan setPopUpTo
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build()
            navController.navigate(R.id.mainFragment, null, navOptions)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.mainFragment) {
            // Jika saat ini di MainFragment, keluar aplikasi
            finish()
        } else {
            // Jika tidak, navigasi kembali ke fragment sebelumnya
            super.onBackPressed()
        }
    }

}