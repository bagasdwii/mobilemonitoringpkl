package com.example.mobilemonitoringbankbpr.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.databinding.FragmentLoginBinding
import com.example.mobilemonitoringbankbpr.viewmodel.LoginViewModel
import org.json.JSONException
import org.json.JSONObject

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var localStorage: LocalStorage

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
                navigateToMain()
            }.onFailure { exception ->
                alertFail(exception.message ?: "Login failed")
                Log.e("LOGIN_FRAGMENT", "Login failed: ${exception.message}")
            }
        })

        binding.login.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                Log.d("LOGIN_FRAGMENT", "Starting login for email: $email")
                loginViewModel.login(email, password)
            } else {
                alertFail("Email and Password are required.")
                Log.w("LOGIN_FRAGMENT", "Email and Password are required.")
            }
        }

        binding.btnRegis.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnForget.setOnClickListener {
            openBrowser(getString(R.string.api_forget))
        }
    }

    private fun alertFail(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Failed")
            .setIcon(R.drawable.ic_warning)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



