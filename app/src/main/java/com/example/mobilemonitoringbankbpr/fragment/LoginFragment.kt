package com.example.mobilemonitoringbankbpr.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.databinding.FragmentLoginBinding
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import com.example.mobilemonitoringbankbpr.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var localStorage: LocalStorage
    private var loadingDialog: AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        localStorage = LocalStorage(requireContext())

        loginViewModel.loginResult.observe(viewLifecycleOwner, { result ->
            result.onSuccess { token ->
                localStorage.token = token
                hideLoadingDialog()
                navigateToMain()
            }.onFailure { exception ->
                hideLoadingDialog()
                alertFail(exception.message ?: "Login failed")
                Log.e("LOGIN_FRAGMENT", "Login failed: ${exception.message}")
            }
        })

        binding.login.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                Log.d("LOGIN_FRAGMENT", "Starting login for email: $email")
                showLoadingDialog()
                loginViewModel.checkConnectionAndLogin(email, password)
            } else {
                alertFail("Email dan Password dibutuhkan")
                Log.w("LOGIN_FRAGMENT", "Email dan Password dibutuhkan")
            }
        }

        binding.btnRegis.setOnClickListener {
            showLoadingDialog()
            loginViewModel.checkConnection { result ->
                hideLoadingDialog()
                result.onSuccess {
                    findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                }.onFailure { exception ->
                    alertFail("Gagal Terkoneksi Dengan Server")
                }
            }
        }

        binding.btnForget.setOnClickListener {
            openBrowser("${RetrofitClient.getBaseUrl()}forgot-password")
        }
    }

//    private fun alertFail(message: String) {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Gagal")
//            .setIcon(R.drawable.ic_warning)
//            .setMessage(message)
//            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//            .show()
//    }
    private fun alertFail(message: String) {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_alert_fail, null)

        val title = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMessage = dialogView.findViewById<TextView>(R.id.alertMessage)

        title.text = "Gagal"
        alertMessage.text = message

        alertDialog.setView(dialogView)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private fun navigateToMain() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .build()
        findNavController().navigate(R.id.action_loginFragment_to_mainFragment, null, navOptions)
    }

    private fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
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

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



