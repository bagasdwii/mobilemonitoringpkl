package com.example.mobilemonitoringbankbpr.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
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
    private var key: String? = null
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

//        registerViewModel.jabatanList.observe(viewLifecycleOwner, Observer { jabatanList ->
//            Log.d("RegisterFragment", "Received Jabatan list: $jabatanList")
//            val jabatanNames = jabatanList.map { it.nama_jabatan ?: "Unknown" }
//            Log.d("RegisterFragment", "Jabatan names: $jabatanNames")
//
////            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jabatanNames)
////            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, jabatanNames)
//            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
//            binding.jabatanSpinnerRegis.adapter = adapter
//
//            binding.jabatanSpinnerRegis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                    jabatanId = jabatanList[position].id_jabatan
//                    Log.d("RegisterFragment", "Selected Jabatan ID: $jabatanId")
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>) {
//                    // Tidak melakukan apapun
//                }
//            }
//            hideLoadingDialog()
//        })
//        showLoadingDialog()
//        registerViewModel.fetchJabatanData()

        binding.register.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun checkRegister() {
        name = binding.registerName.text.toString()
        email = binding.registerEmail.text.toString()
        password = binding.registerPassword.text.toString()
        key = binding.registerKey.text.toString()

        if (name.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty() || key.isNullOrEmpty()){
            alertFail("Nama, Email, Password, dan Key.")
        } else {
            val registerRequest = Register(name!!, email!!, password!!, key!!.toInt())
            showLoadingDialog()
            registerViewModel.registerUser(registerRequest,
                onSuccess = { requireActivity().runOnUiThread {
                    hideLoadingDialog()
                    alertSuccess("Registrasi Berhasil.")
                }},
                onError = { msg -> requireActivity().runOnUiThread {
                    hideLoadingDialog()
                    alertFail(msg)
                }}
            )
        }
    }

    private fun showConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog, null)

        val title = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMessage = dialogView.findViewById<TextView>(R.id.alertMessage)

        title.text = "Peringatan"
        alertMessage.text = "Apakah data yang akan dikirimkan sudah benar ?"

        alertDialog.setView(dialogView)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "iYA") { dialog, _ ->
            checkRegister()
            dialog.dismiss()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"TIDAK"){ dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
    private fun alertSuccess(message: String) {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_alert_succes, null)

        val title = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMessage = dialogView.findViewById<TextView>(R.id.alertMessage)

        title.text = "Berhasil"
        alertMessage.text = message

        alertDialog.setView(dialogView)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ ->
            dialog.dismiss()
            navigateToLogin()
        }
        alertDialog.show()
    }

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


