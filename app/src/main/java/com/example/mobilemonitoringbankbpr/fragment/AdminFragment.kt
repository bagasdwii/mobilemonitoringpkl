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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
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

        adminViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                showLoadingDialog()
            } else {
                dismissLoadingDialog()
            }
        })

        adminViewModel.updateUserResult.observe(viewLifecycleOwner, { result ->
            result.onSuccess { response ->
                alertSuccess("Data User berhasil di Update")
            }.onFailure { exception ->
                alertFail("Data User Gagal di Update")
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

    private fun navigateToAdminFragment() {
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager

        // Remove AdminFragment from the back stack if it exists
        fragmentManager.popBackStack("AdminFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)

        // Start a new transaction
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, AdminFragment())

        // Add the transaction to the back stack with a unique name
        fragmentTransaction.addToBackStack("AdminFragment")
        fragmentTransaction.commit()
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
            adminViewModel.getUser("")
            adminViewModel.fetchAllData()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dismissLoadingDialog()
    }
}





