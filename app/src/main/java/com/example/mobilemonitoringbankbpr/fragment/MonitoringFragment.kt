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
            monitoringViewModel.setPage(1)
            monitoringViewModel.getNasabahs(query)
            updateButtonVisibility()
        }

        binding.previousButton.setOnClickListener {
            val currentPage = monitoringViewModel.getCurrentPage()
            if (currentPage > 1) {
                monitoringViewModel.setPage(currentPage - 1)
                monitoringViewModel.getNasabahs(binding.searchEditText.text.toString())
                Log.d("MonitoringFragment", "Previous button clicked, page: ${monitoringViewModel.getCurrentPage()}")
            }
            updateButtonVisibility()
        }

        binding.nextButton.setOnClickListener {
            monitoringViewModel.setPage(monitoringViewModel.getCurrentPage() + 1)
            monitoringViewModel.getNasabahs(binding.searchEditText.text.toString())
            Log.d("MonitoringFragment", "Next button clicked, page: ${monitoringViewModel.getCurrentPage()}")
            updateButtonVisibility()
        }

        monitoringViewModel.getNasabahs("")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dismissLoadingDialog()
    }
}







