package com.example.mobilemonitoringbankbpr.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Cabang
import com.example.mobilemonitoringbankbpr.data.Direksi
import com.example.mobilemonitoringbankbpr.databinding.FragmentAccountBinding
import com.example.mobilemonitoringbankbpr.databinding.FragmentMainBinding

import com.example.mobilemonitoringbankbpr.viewmodel.AccountViewModel

//class AccountFragment : Fragment() {
//
//    private var _binding: FragmentAccountBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var accountViewModel: AccountViewModel
//    private var loadingDialog: AlertDialog? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentAccountBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
//
//        // Observe ViewModel data
//        observeViewModel()
//
//        binding.btnSubmit.setOnClickListener {
//            try {
//                val selectedCabang = binding.spinnerCabang.selectedItem as Cabang
//                val selectedCabangId = selectedCabang.id
//                val selectedDireksi = binding.spinnerDireksi.selectedItem as Direksi
//                val selectedDireksiId = selectedDireksi.id
//                Log.d("AccountFragment", "Selected Cabang ID: $selectedCabangId")
//                accountViewModel.updatePegawaiKepalaCabang(requireContext(), selectedCabangId, selectedDireksiId)
//            } catch (e: Exception) {
//                Log.e("AccountFragment", "Error on btnSubmit click: ${e.message}")
//            }
//        }
//
//    }
//
//    private fun observeViewModel() {
//        accountViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
//            if (isLoading) {
//                showLoadingDialog()
//            } else {
//                dismissLoadingDialog()
//            }
//        })
//
//        accountViewModel.user.observe(viewLifecycleOwner, { user ->
//            binding.tvName.text = user.name
//            binding.tvEmail.text = user.email
//            binding.tvJabatan.text = user.jabatan
//            Log.d("AccountFragment", "User cabang: ${user.cabang}, ${user.id_direksi}")
//            if (user.jabatan == "Kepala Cabang"&& user.cabang == null){
//                binding.tvCabangLabel.visibility = View.VISIBLE
//                binding.spinnerCabang.visibility = View.VISIBLE
//                binding.tvDireksiLabel.visibility = View.VISIBLE
//                binding.spinnerDireksi.visibility = View.VISIBLE
//                binding.tvCabang.visibility = View.GONE
//                binding.tvDireksi.visibility = View.GONE
//                binding.btnSubmit.visibility = View.VISIBLE
//
//            } else if (user.jabatan == "Kepala Cabang" && user.cabang != null) {
//                Log.d("AccountFragment", "User cabang: ${user.cabang}, ${user.jabatan}")
//                Log.d("AccountFragment", "User direksi: ${user.id_direksi}")
//
//                binding.tvCabangLabel.visibility = View.VISIBLE
//                binding.tvCabang.visibility = View.VISIBLE
//                binding.tvCabang.text = user.cabang
//                binding.tvDireksiLabel.visibility = View.VISIBLE
//                binding.tvDireksi.visibility = View.VISIBLE
//                binding.tvDireksi.text = user.id_direksi
//                binding.spinnerCabang.visibility = View.GONE
//                binding.btnSubmit.visibility = View.GONE
//            } else if (user.jabatan == "Supervisor") {
//                binding.tvCabangLabel.visibility = View.VISIBLE
//                binding.spinnerCabang.visibility = View.VISIBLE
//                binding.tvWilayahLabel.visibility = View.VISIBLE
//                binding.spinnerWilayah.visibility = View.VISIBLE
//                binding.btnSubmit.visibility = View.VISIBLE
//            } else {
//                binding.tvCabangLabel.visibility = View.GONE
//                binding.spinnerCabang.visibility = View.GONE
//                binding.tvWilayahLabel.visibility = View.GONE
//                binding.spinnerWilayah.visibility = View.GONE
//                binding.btnSubmit.visibility = View.GONE
//            }
//        })
//
//
//
//        accountViewModel.cabangList.observe(viewLifecycleOwner, { cabangList ->
//            val adapter = ArrayAdapter(
//                requireContext(),
//                android.R.layout.simple_spinner_item,
//                cabangList
//            )
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            binding.spinnerCabang.adapter = adapter
//        })
//
//
//        accountViewModel.wilayahList.observe(viewLifecycleOwner, { wilayahList ->
//            val adapter = ArrayAdapter(
//                requireContext(),
//                android.R.layout.simple_spinner_item,
////                wilayahList.map { it.name }
//                wilayahList
//            )
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            binding.spinnerWilayah.adapter = adapter
//        })
//        accountViewModel.direksiList.observe(viewLifecycleOwner, { direksiList ->
//            val adapter = ArrayAdapter(
//                requireContext(),
//                android.R.layout.simple_spinner_item,
//                direksiList
//            )
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            binding.spinnerDireksi.adapter = adapter
//        })
//        accountViewModel.getJabatan(requireContext())
//        accountViewModel.getUserKepalaCabang(requireContext())
//        accountViewModel.getCabang(requireContext())
//        accountViewModel.getWilayah(requireContext())
//        accountViewModel.getDireksi(requireContext())
//    }
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
//    private fun alertSuccess(message: String) {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Sukses")
//            .setIcon(R.drawable.ic_check)
//            .setMessage(message)
//            .setPositiveButton("Ok") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }
//
//    private fun alertFail(message: String) {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Gagal")
//            .setIcon(R.drawable.ic_warning)
//            .setMessage(message)
//            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//            .show()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
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

        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        // Observe ViewModel data
        observeViewModel()

        binding.btnSubmit.setOnClickListener {
            showConfirmationDialog()
        }

    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin mengirim data?")
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                sendData()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun sendData() {
        try {
            val selectedCabang = binding.spinnerCabang.selectedItem as Cabang
            val selectedCabangId = selectedCabang.id
            val selectedDireksi = binding.spinnerDireksi.selectedItem as Direksi
            val selectedDireksiId = selectedDireksi.id
            Log.d("AccountFragment", "Selected Cabang ID: $selectedCabangId")
            accountViewModel.updatePegawaiKepalaCabang(requireContext(), selectedCabangId, selectedDireksiId)
        } catch (e: Exception) {
            Log.e("AccountFragment", "Error on btnSubmit click: ${e.message}")
        }
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
            Log.d("AccountFragment", "User cabang: ${user.cabang}, ${user.id_direksi}")
            if (user.jabatan == "Kepala Cabang" && user.cabang == null) {
                binding.tvCabangLabel.visibility = View.VISIBLE
                binding.spinnerCabang.visibility = View.VISIBLE
                binding.tvDireksiLabel.visibility = View.VISIBLE
                binding.spinnerDireksi.visibility = View.VISIBLE
                binding.tvCabang.visibility = View.GONE
                binding.tvDireksi.visibility = View.GONE
                binding.btnSubmit.visibility = View.VISIBLE

            } else if (user.jabatan == "Kepala Cabang" && user.cabang != null) {
                Log.d("AccountFragment", "User cabang: ${user.cabang}, ${user.jabatan}")
                Log.d("AccountFragment", "User direksi: ${user.id_direksi}")

                binding.tvCabangLabel.visibility = View.VISIBLE
                binding.tvCabang.visibility = View.VISIBLE
                binding.tvCabang.text = user.cabang
                binding.tvDireksiLabel.visibility = View.VISIBLE
                binding.tvDireksi.visibility = View.VISIBLE
                binding.tvDireksi.text = user.id_direksi
                binding.spinnerCabang.visibility = View.GONE
                binding.btnSubmit.visibility = View.GONE
            } else if (user.jabatan == "Supervisor") {
                binding.tvCabangLabel.visibility = View.VISIBLE
                binding.spinnerCabang.visibility = View.VISIBLE
                binding.tvWilayahLabel.visibility = View.VISIBLE
                binding.spinnerWilayah.visibility = View.VISIBLE
                binding.btnSubmit.visibility = View.VISIBLE
            } else {
                binding.tvCabangLabel.visibility = View.GONE
                binding.spinnerCabang.visibility = View.GONE
                binding.tvWilayahLabel.visibility = View.GONE
                binding.spinnerWilayah.visibility = View.GONE
                binding.btnSubmit.visibility = View.GONE
            }
        })

        accountViewModel.cabangList.observe(viewLifecycleOwner, { cabangList ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                cabangList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCabang.adapter = adapter
        })

        accountViewModel.wilayahList.observe(viewLifecycleOwner, { wilayahList ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                wilayahList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerWilayah.adapter = adapter
        })

        accountViewModel.direksiList.observe(viewLifecycleOwner, { direksiList ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                direksiList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDireksi.adapter = adapter
        })

        accountViewModel.updateStatus.observe(viewLifecycleOwner, { status ->
            if (status == "success") {
                alertSuccess("Data berhasil dikirim.")
            } else {
                alertFail("Gagal mengirim data.")
            }
        })

        accountViewModel.getJabatan(requireContext())
        accountViewModel.getUserKepalaCabang(requireContext())
        accountViewModel.getCabang(requireContext())
        accountViewModel.getWilayah(requireContext())
        accountViewModel.getDireksi(requireContext())
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
                reloadFragment()
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

    private fun reloadFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.accountFragment, true)
            .build()
        findNavController().navigate(R.id.accountFragment, null, navOptions)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






