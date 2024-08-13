package com.example.mobilemonitoringbankbpr.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mobilemonitoringbankbpr.viewmodelfactory.AccountViewModelFactory
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.databinding.FragmentAccountBinding

import com.example.mobilemonitoringbankbpr.viewmodel.AccountViewModel

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var accountViewModel: AccountViewModel
    private var loadingDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = AccountViewModelFactory(requireActivity().application)
        accountViewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)

        // Observe ViewModel data
        observeViewModel()

        // Check connection and logout locally if necessary
        accountViewModel.isConnected.observe(viewLifecycleOwner, { isConnected ->
            if (!isConnected) {
                showAlertConnectionLost()
            }
        })
    }

    private fun observeViewModel() {
        accountViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                showLoadingDialog()
            } else {
                dismissLoadingDialog()
            }
        })
        accountViewModel.user.observe(viewLifecycleOwner, { user ->
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.tvJabatan.text = user.jabatan
            Log.d("AccountFragment", "User cabang: ${user.name}, ${user.jabatan}")

            when {
                user.jabatan == "Kepala Cabang" && user.cabang == null -> {
                    binding.tvCabangLabel.visibility = View.VISIBLE
//                    binding.tvDireksiLabel.visibility = View.VISIBLE
                    binding.tvCabang.visibility = View.VISIBLE
//                    binding.tvDireksi.visibility = View.VISIBLE
                }
                user.jabatan == "Kepala Cabang" && user.cabang != null -> {
                    Log.d("AccountFragment", "User cabang: ${user.cabang}, ${user.jabatan}")
                    binding.tvCabangLabel.visibility = View.VISIBLE
                    binding.tvCabang.visibility = View.VISIBLE
                    binding.tvCabang.text = user.cabang
//                    binding.tvDireksiLabel.visibility = View.VISIBLE
//                    binding.tvDireksi.visibility = View.VISIBLE
//                    binding.tvDireksi.text = user.id_direksi
                }
                user.jabatan == "Supervisor" && user.cabang == null -> {
                    binding.tvCabangLabel.visibility = View.VISIBLE
                    binding.tvWilayahLabel.visibility = View.VISIBLE
//                    binding.tvKepalaCabangLabel.visibility = View.VISIBLE
                    binding.tvCabang.visibility = View.VISIBLE
                    binding.tvWilayah.visibility = View.VISIBLE
//                    binding.tvKepalaCabang.visibility = View.VISIBLE
                }
                user.jabatan == "Supervisor" && user.cabang != null -> {
//                    Log.d("AccountFragment", "User supervisor: ${user.id_kepala_cabang}")
                    binding.tvCabangLabel.visibility = View.VISIBLE
                    binding.tvWilayahLabel.visibility = View.VISIBLE
//                    binding.tvKepalaCabangLabel.visibility = View.VISIBLE
                    binding.tvCabang.visibility = View.VISIBLE
                    binding.tvCabang.text = user.cabang
                    binding.tvWilayah.visibility = View.VISIBLE
                    binding.tvWilayah.text = user.wilayah
//                    binding.tvKepalaCabang.visibility = View.VISIBLE
//                    binding.tvKepalaCabang.text = user.id_kepala_cabang
                }
                user.jabatan == "Admin Kas" && user.cabang == null -> {
                    binding.tvCabangLabel.visibility = View.VISIBLE
                    binding.tvWilayahLabel.visibility = View.VISIBLE
//                    binding.tvSupervisorLabel.visibility = View.VISIBLE
                    binding.tvCabang.visibility = View.VISIBLE
                    binding.tvWilayah.visibility = View.VISIBLE
//                    binding.tvSupervisor.visibility = View.VISIBLE
                }
                user.jabatan == "Admin Kas" && user.cabang != null -> {
//                    Log.d("AccountFragment", "User supervisor: ${user.id_supervisor}")
                    binding.tvCabangLabel.visibility = View.VISIBLE
                    binding.tvWilayahLabel.visibility = View.VISIBLE
//                    binding.tvSupervisorLabel.visibility = View.VISIBLE
                    binding.tvCabang.visibility = View.VISIBLE
                    binding.tvCabang.text = user.cabang
                    binding.tvWilayah.visibility = View.VISIBLE
                    binding.tvWilayah.text = user.wilayah
//                    binding.tvSupervisor.visibility = View.VISIBLE
//                    binding.tvSupervisor.text = user.id_supervisor
                }
                user.jabatan == "Account Officer" && user.cabang == null -> {
                    binding.tvCabangLabel.visibility = View.VISIBLE
                    binding.tvWilayahLabel.visibility = View.VISIBLE
//                    binding.tvAdminKasLabel.visibility = View.VISIBLE
                    binding.tvCabang.visibility = View.VISIBLE
                    binding.tvWilayah.visibility = View.VISIBLE
//                    binding.tvAdminKas.visibility = View.VISIBLE
                }
                user.jabatan == "Account Officer" && user.cabang != null -> {
//                    Log.d("AccountFragment", "User supervisor: ${user.id_admin_kas}")
                    binding.tvCabangLabel.visibility = View.VISIBLE
                    binding.tvWilayahLabel.visibility = View.VISIBLE
//                    binding.tvAdminKasLabel.visibility = View.VISIBLE
                    binding.tvCabang.visibility = View.VISIBLE
                    binding.tvCabang.text = user.cabang
                    binding.tvWilayah.visibility = View.VISIBLE
                    binding.tvWilayah.text = user.wilayah
//                    binding.tvAdminKas.visibility = View.VISIBLE
//                    binding.tvAdminKas.text = user.id_admin_kas
                }
                else -> {
                    // Handle other cases
                }
            }
        })
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

    private fun alertSuccess(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Sukses")
            .setIcon(R.drawable.ic_check)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
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

    private fun showAlertConnectionLost() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_alert_fail, null)

        val title = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMessage = dialogView.findViewById<TextView>(R.id.alertMessage)

        title.text = "Gagal"
        alertMessage.text = "Koneksi dengan Server terputus. Aplikasi akan keluar."

        alertDialog.setView(dialogView)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ ->
            dialog.dismiss()
            requireActivity().finish()
        }
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}








