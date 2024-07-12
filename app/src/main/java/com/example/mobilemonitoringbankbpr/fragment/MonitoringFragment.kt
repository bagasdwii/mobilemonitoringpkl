package com.example.mobilemonitoringbankbpr.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.adapter.MonitoringAdapter
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.databinding.FragmentMonitoringBinding
import com.example.mobilemonitoringbankbpr.viewmodel.MonitoringViewModel


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

        // Observe isLoading to show/hide loading dialog
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
            monitoringViewModel.setPage(1) // Reset to the first page
            monitoringViewModel.getNasabahs(query, requireContext())

            updateButtonVisibility()
        }

        binding.previousButton.setOnClickListener {
            val currentPage = monitoringViewModel.getCurrentPage()
            if (currentPage > 1) {
                monitoringViewModel.setPage(currentPage - 1)
                monitoringViewModel.getNasabahs(binding.searchEditText.text.toString(), requireContext())
                Log.d("MonitoringFragment", "Previous button clicked, page: ${monitoringViewModel.getCurrentPage()}")
            }
            updateButtonVisibility()
        }

        binding.nextButton.setOnClickListener {
            monitoringViewModel.setPage(monitoringViewModel.getCurrentPage() + 1)
            monitoringViewModel.getNasabahs(binding.searchEditText.text.toString(), requireContext())
            Log.d("MonitoringFragment", "Next button clicked, page: ${monitoringViewModel.getCurrentPage()}")
            updateButtonVisibility()
        }

        // Fetch initial data
        monitoringViewModel.getNasabahs("", requireContext())
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
    }

    private fun showSuratPeringatanDialog(suratPeringatan: SuratPeringatan) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_surat_peringatan, null)
        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<TextView>(R.id.tvTingkat).text = "Tingkat: ${suratPeringatan.tingkat}"
        dialogView.findViewById<TextView>(R.id.tvTanggal).text = "Tanggal: ${suratPeringatan.tanggal}"
        dialogView.findViewById<TextView>(R.id.tvKeterangan).text = "Keterangan: ${suratPeringatan.keterangan}"

        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dismissLoadingDialog() // Ensure dialog is dismissed when fragment is destroyed
    }
}






