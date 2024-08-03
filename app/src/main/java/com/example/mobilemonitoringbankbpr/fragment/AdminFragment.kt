package com.example.mobilemonitoringbankbpr.fragment

import android.app.AlertDialog
import android.app.Dialog
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
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.adapter.AdminAdapter
import com.example.mobilemonitoringbankbpr.databinding.DialogSeacrhSpinnerBinding
import com.example.mobilemonitoringbankbpr.databinding.FragmentAdminBinding
import com.example.mobilemonitoringbankbpr.viewmodel.AdminViewModel
import com.example.mobilemonitoringbankbpr.viewmodel.SuratViewModel
import java.util.ArrayList


class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminViewModel: AdminViewModel
    private var loadingDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adminViewModel = ViewModelProvider(this).get(AdminViewModel::class.java)

        val adapter = AdminAdapter(adminViewModel, requireContext(), viewLifecycleOwner, viewLifecycleOwner.lifecycleScope)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
//        adminViewModel.updateUserResult.observe(viewLifecycleOwner, Observer { result ->
//            result.fold(
//                onSuccess = { updateUserResponse ->
//                    Toast.makeText(context, "User updated successfully: ${updateUserResponse}", Toast.LENGTH_SHORT).show()
//                },
//                onFailure = { exception ->
//                    Toast.makeText(context, "Update failed: ${exception.message}", Toast.LENGTH_SHORT).show()
//                }
//            )
//        })
        adminViewModel.updateUserResult.observe(viewLifecycleOwner, { result ->
            result.onSuccess { response ->
                // Handle success, update UI with response data
                Toast.makeText(context, "Update successful: ${response.message}", Toast.LENGTH_SHORT).show()
            }.onFailure { exception ->
                // Handle failure, show error message
                Toast.makeText(context, "Update failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        })

        adminViewModel.isLoading.observe(viewLifecycleOwner, {
            if (it) {
                showLoadingDialog()
            } else {
                dismissLoadingDialog()
            }
        })

        adminViewModel.user.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            adminViewModel.setPage(1)
            adminViewModel.getUser(query)
            updateButtonVisibility()
        }

        binding.previousButton.setOnClickListener {
            val currentPage = adminViewModel.getCurrentPage()
            if (currentPage > 1) {
                adminViewModel.setPage(currentPage - 1)
                adminViewModel.getUser(binding.searchEditText.text.toString())
                Log.d("AdminFragment", "Previous button clicked, page: ${adminViewModel.getCurrentPage()}")
            }
            updateButtonVisibility()
        }

        binding.nextButton.setOnClickListener {
            adminViewModel.setPage(adminViewModel.getCurrentPage() + 1)
            adminViewModel.getUser(binding.searchEditText.text.toString())
            Log.d("AdminFragment", "Next button clicked, page: ${adminViewModel.getCurrentPage()}")
            updateButtonVisibility()
        }

        adminViewModel.getUser("")
        adminViewModel.fetchAllData()


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
        val currentPage = adminViewModel.getCurrentPage()
        binding.previousButton.visibility = if (currentPage > 1) View.VISIBLE else View.GONE
        binding.buttonSpacer.visibility = if (currentPage > 1) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dismissLoadingDialog()
    }
}





