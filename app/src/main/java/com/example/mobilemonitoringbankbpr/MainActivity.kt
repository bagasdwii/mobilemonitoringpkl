package com.example.mobilemonitoringbankbpr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
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
        navController = navHostFragment.findNavController()

        localStorage = LocalStorage(this)

        // Periksa apakah token tersedia
        val token = localStorage.token

        if (token.isNullOrEmpty()) {
            // Token tidak ada, arahkan ke LoginFragment melalui NavController
            navController.navigate(R.id.loginFragment)
        } else {
            // Token ada, arahkan ke MainFragment melalui NavController
            navController.navigate(R.id.action_loginFragment_to_mainFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
