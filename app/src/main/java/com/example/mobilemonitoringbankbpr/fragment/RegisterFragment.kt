package com.example.mobilemonitoringbankbpr.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.databinding.FragmentRegisterBinding
import com.example.mobilemonitoringbankbpr.data.Register
import com.example.mobilemonitoringbankbpr.viewmodel.RegisterViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels()
    private var loadingDialog: AlertDialog? = null
    private var name: String? = null
    private var email: String? = null
    private var password: String? = null
    private var nip: String? = null
    private var jabatanId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerViewModel.jabatanList.observe(viewLifecycleOwner, Observer { jabatanList ->
            val jabatanNames = jabatanList.map { it.name }
            Log.d("RegisterFragment", "Jabatan names: $jabatanNames")

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jabatanNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.jabatanSpinnerRegis.adapter = adapter

            binding.jabatanSpinnerRegis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    jabatanId = jabatanList[position].id
                    Log.d("RegisterFragment", "Selected Jabatan ID: $jabatanId")
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Tidak melakukan apapun
                }
            }
            hideLoadingDialog()
        })
        showLoadingDialog()
        val url = getString(R.string.api_server) + "/jabatan"
        Log.d("RegisterFragment", "Fetching jabatan data from: $url")
        registerViewModel.fetchJabatanData(url)

        binding.register.setOnClickListener {
            checkRegister()
        }
    }

    private fun checkRegister() {
        name = binding.registerName.text.toString()
        email = binding.registerEmail.text.toString()
        password = binding.registerPassword.text.toString()
        nip = binding.registerNip.text.toString()

        if (name.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty() || nip.isNullOrEmpty() || jabatanId == null) {
            alertFail("Nama, Email, Password, NIP dan Jabatan wajib diisi.")
        } else {
            val registerRequest = Register(name!!, email!!, password!!, nip!!.toInt()!!, jabatanId!!)
            val url = getString(R.string.api_server) + "/registermobile"
            Log.d("RegisterFragment", "Registering user with URL: $url and data: $registerRequest")
            showLoadingDialog()
            registerViewModel.registerUser(url, registerRequest,
                onSuccess = { requireActivity().runOnUiThread {
                    hideLoadingDialog()
                    alertSuccess("Registrasi Berhasil.") } },
                onError = { msg -> requireActivity().runOnUiThread {
                    hideLoadingDialog()
                    alertFail(msg) } }
            )
        }
    }

    private fun alertSuccess(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Sukses")
            .setIcon(R.drawable.ic_check)
            .setMessage(message)
            .setPositiveButton("Login") { dialog, _ ->
                dialog.dismiss()
                navigateToLogin()
            }
            .show()
    }

    private fun alertFail(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Gagal")
            .setIcon(R.drawable.ic_warning)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun navigateToLogin() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.registerFragment, true)
            .build()
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment, null, navOptions)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
