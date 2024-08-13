package com.example.mobilemonitoringbankbpr.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.adapter.MonitoringAdapter
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.databinding.DialogSeacrhSpinnerBinding
import com.example.mobilemonitoringbankbpr.databinding.FragmentMonitoringBinding
import com.example.mobilemonitoringbankbpr.viewmodel.MonitoringViewModel
import java.util.ArrayList


class MonitoringFragment : Fragment() {
    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!
    private lateinit var monitoringViewModel: MonitoringViewModel
    private var loadingDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        monitoringViewModel = ViewModelProvider(this).get(MonitoringViewModel::class.java)

        val adapter = MonitoringAdapter(monitoringViewModel, requireContext(), viewLifecycleOwner, viewLifecycleOwner.lifecycleScope)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        monitoringViewModel.isLoading.observe(viewLifecycleOwner, {
            if (it) {
                showLoadingDialog()
            } else {
                dismissLoadingDialog()
            }
        })

        monitoringViewModel.nasabahs.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            val selectedCabang = binding.editCabang.text.toString()

            // Tetapkan halaman ke 1 saat melakukan pencarian baru
            monitoringViewModel.setPage(1)

            // Kirim query dan cabang sebagai parameter terpisah
            monitoringViewModel.getNasabahs(query, selectedCabang)
            updateButtonVisibility()

            // Reset input jika diinginkan setelah pencarian
            // resetSearchFields()
        }
//        private fun resetSearchFields() {
//            binding.searchEditText.text.clear()
//            binding.editCabang.text.clear()
//        }
        binding.previousButton.setOnClickListener {
            val currentPage = monitoringViewModel.getCurrentPage()
            if (currentPage > 1) {
                monitoringViewModel.setPage(currentPage - 1)
                monitoringViewModel.getNasabahs(binding.searchEditText.text.toString(), binding.editCabang.text.toString())
                Log.d("MonitoringFragment", "Previous button clicked, page: ${monitoringViewModel.getCurrentPage()}")
            }
            updateButtonVisibility()
        }

        binding.nextButton.setOnClickListener {
            monitoringViewModel.setPage(monitoringViewModel.getCurrentPage() + 1)
            monitoringViewModel.getNasabahs(binding.searchEditText.text.toString(), binding.editCabang.text.toString())
            Log.d("MonitoringFragment", "Next button clicked, page: ${monitoringViewModel.getCurrentPage()}")
            updateButtonVisibility()
        }
        setupCabangDropdown()
        monitoringViewModel.getCabangList()

        monitoringViewModel.getNasabahs("","")
    }

    private fun setupCabangDropdown() {
        val sharedPreferences = requireContext().getSharedPreferences("STORAGE_LOGIN_API", Context.MODE_PRIVATE)
        val jabatanId = sharedPreferences.getInt("jabatan", -1)

        // Check if jabatanId is 99 or 1
        if (jabatanId == 99 || jabatanId == 1) {
            binding.editCabang.visibility = View.VISIBLE
            monitoringViewModel.cabang.observe(viewLifecycleOwner, { cabang ->
                val arrayList = ArrayList(cabang.map { it.nama_cabang })
                Log.d("MonitoringFragment", "Cabang list updated: $cabang")
                binding.editCabang.setOnClickListener {
                    showDialog(arrayList)
                }
            })
        } else {
            // Optionally, handle the case where the user does not have the required jabatan_id
            Log.d("MonitoringFragment", "User does not have the required permissions to view the dropdown.")
        }
    }

    private fun showDialog(arrayList: ArrayList<String>) {
        val dialogBinding = DialogSeacrhSpinnerBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext()).apply {
            setContentView(dialogBinding.root)
            window?.setLayout(900, 2000)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item_surat, arrayList)

        dialogBinding.listView.adapter = adapter

        dialogBinding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        dialogBinding.listView.setOnItemClickListener { _, _, position, _ ->
            binding.editCabang.setText(adapter.getItem(position))
            dialog.dismiss()
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

    private fun updateButtonVisibility() {
        val currentPage = monitoringViewModel.getCurrentPage()
        binding.previousButton.visibility = if (currentPage > 1) View.VISIBLE else View.GONE
        binding.buttonSpacer.visibility = if (currentPage > 1) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dismissLoadingDialog()
    }
}







