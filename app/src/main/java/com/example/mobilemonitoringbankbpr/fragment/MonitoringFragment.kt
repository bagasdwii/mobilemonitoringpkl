package com.example.mobilemonitoringbankbpr.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.adapter.NasabahAdapter
import com.example.mobilemonitoringbankbpr.databinding.DialogDetailNasabahBinding
import com.example.mobilemonitoringbankbpr.databinding.FragmentMonitoringBinding
import com.example.mobilemonitoringbankbpr.viewmodel.MonitoringViewModel
import org.json.JSONObject


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

        val adapter = NasabahAdapter()
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
            monitoringViewModel.getNasahabs(query, requireContext())
        }

        // Fetch initial data
        monitoringViewModel.getNasahabs("", requireContext())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dismissLoadingDialog() // Ensure dialog is dismissed when fragment is destroyed
    }
}
//class MonitoringFragment : Fragment() {
//
//    private var _binding: FragmentMonitoringBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var recyclerView: RecyclerView
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        recyclerView = binding.recyclerView
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        // Set adapter for RecyclerView
//        val adapter = NasabahAdapter(listOf()) { idNasabah ->
//            showNasabahDetailDialog(idNasabah)
//        }
//        recyclerView.adapter = adapter
//
//        // Fetch and display nasabah list (implement as needed)
//        fetchNasabahList()
//    }
//
//    private fun fetchNasabahList() {
//        // Implement fetch logic here
//    }
//
//    private fun showNasabahDetailDialog(idNasabah: String) {
//        val dialogBinding = DialogDetailNasabahBinding.inflate(layoutInflater)
//
//        val url = getString(R.string.api_server) + "/nasabah/$idNasabah"
//        Thread {
//            val http = Http(requireContext(), url)
//            http.setMethod("GET")
//            http.send()
//
//            val code = http.getStatusCode()
//            val response = http.getResponse()
//
//            if (code == 200 && response != null) {
//                try {
//                    val nasabah = JSONObject(response)
//                    requireActivity().runOnUiThread {
//                        dialogBinding.tvnoNasabah.text = nasabah.getString("no")
//                        dialogBinding.tvnasabahName.text = nasabah.getString("nama")
//                        dialogBinding.tvpokok.text = "Rp. ${nasabah.getString("pokok")}"
//                        dialogBinding.tvbunga.text = "Rp. ${nasabah.getString("bunga")}"
//                        dialogBinding.tvdenda.text = "Rp. ${nasabah.getString("denda")}"
//                        dialogBinding.tvtotal.text = "Rp. ${nasabah.getString("total")}"
//                        dialogBinding.tvketerangan.text = "Rp. ${nasabah.getString("keterangan")}"
//                        dialogBinding.tvttd.text = "TTD: ${nasabah.getString("ttd")}"
//                        dialogBinding.tvkembali.text = nasabah.getString("kembali")
//                        dialogBinding.tvcabang.text = nasabah.getString("id_cabang")
//                        dialogBinding.tvwilayah.text = nasabah.getString("id_wilayah")
//                        dialogBinding.tvaccountOfficer.text = nasabah.getString("id_account_officer")
//                        dialogBinding.tvadminKas.text = nasabah.getString("id_admin_kas")
//
//                        val dialog = AlertDialog.Builder(requireContext())
//                            .setView(dialogBinding.root)
//                            .create()
//
//                        dialogBinding.closeButton.setOnClickListener {
//                            dialog.dismiss()
//                        }
//
//                        dialog.show()
//                    }
//                } catch (e: Exception) {
//                    requireActivity().runOnUiThread {
//                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } else {
//                requireActivity().runOnUiThread {
//                    Toast.makeText(requireContext(), "Error fetching nasabah details", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }.start()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
