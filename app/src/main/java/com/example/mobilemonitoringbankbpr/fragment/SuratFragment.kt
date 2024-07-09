package com.example.mobilemonitoringbankbpr.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mobilemonitoringbankbpr.FileUtilsNasabah
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.databinding.DialogSeacrhSpinnerBinding
import com.example.mobilemonitoringbankbpr.databinding.FragmentSuratBinding
import com.example.mobilemonitoringbankbpr.viewmodel.SuratViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SuratFragment : Fragment() {
    private var _binding: FragmentSuratBinding? = null
    private val binding get() = _binding!!

    private lateinit var nasabahViewModel: SuratViewModel
    private var loadingDialog: AlertDialog? = null

    private val calendar = Calendar.getInstance()
    private var selectedImageUri: Uri? = null
    private var selectedPdfUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSuratBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nasabahViewModel = ViewModelProvider(this).get(SuratViewModel::class.java)

        setupNasabahDropdown()
        setupDatePicker()
        setupImagePicker()
        setupPdfPicker()

        binding.btnSubmit.setOnClickListener {
            submitSuratPeringatan()
        }

        nasabahViewModel.isSubmitting.observe(viewLifecycleOwner, { isSubmitting ->
            binding.btnSubmit.isEnabled = !isSubmitting
            if (isSubmitting) {
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }
            Log.d("SuratFragment", "isSubmitting: $isSubmitting")
        })

        nasabahViewModel.isSubmissionSuccessful.observe(viewLifecycleOwner, { isSuccessful ->
            if (isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    "Surat peringatan berhasil dikirim",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("SuratFragment", "Surat peringatan berhasil dikirim")
                // Reset form
                resetForm()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Gagal mengirim surat peringatan",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("SuratFragment", "Gagal mengirim surat peringatan")
            }
        })

        nasabahViewModel.fetchNasabahList()
        nasabahViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }
            Log.d("SuratFragment", "isLoading: $isLoading")
        })
    }

    private fun setupNasabahDropdown() {
        nasabahViewModel.nasabahList.observe(viewLifecycleOwner, { nasabahList ->
            val arrayList = ArrayList(nasabahList.map { it.nama })
            Log.d("SuratFragment", "Nasabah list updated: $nasabahList")
            binding.autoCompleteNasabah.setOnClickListener {
                showDialog(arrayList)
            }
        })
    }


    private fun showDialog(arrayList: ArrayList<String>) {
        val dialogBinding = DialogSeacrhSpinnerBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext()).apply {
            setContentView(dialogBinding.root)
            window?.setLayout(650, 800)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrayList)
        dialogBinding.listView.adapter = adapter

        dialogBinding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        dialogBinding.listView.setOnItemClickListener { _, _, position, _ ->
            binding.autoCompleteNasabah.setText(adapter.getItem(position))
            dialog.dismiss()
        }
    }

    private fun setupDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
                Log.d("SuratFragment", "Date selected: ${calendar.time}")
            }

        binding.etTanggal.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateInView() {
        val format = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(format, Locale.US)
        binding.etTanggal.setText(sdf.format(calendar.time))
        Log.d("SuratFragment", "Date updated in view: ${sdf.format(calendar.time)}")
    }

    private fun setupImagePicker() {
        binding.btnPilihGambar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
            Log.d("SuratFragment", "Image picker intent launched")
        }
    }

    private fun setupPdfPicker() {
        binding.btnPilihPdf.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, PICK_PDF_REQUEST)
            Log.d("SuratFragment", "PDF picker intent launched")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    selectedImageUri = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)
                    binding.ivPreviewGambar.setImageBitmap(bitmap)
                    binding.ivPreviewGambar.visibility = View.VISIBLE
                    Log.d("SuratFragment", "Image selected: $selectedImageUri")
                }
                PICK_PDF_REQUEST -> {
                    selectedPdfUri = data?.data
                    binding.tvPdfName.text = selectedPdfUri?.lastPathSegment
                    binding.tvPdfName.visibility = View.VISIBLE
                    Log.d("SuratFragment", "PDF selected: $selectedPdfUri")
                }
            }
        }
    }

    private fun submitSuratPeringatan() {
        val namaNasabah = binding.autoCompleteNasabah.text.toString()
        val tingkatSP = binding.spinnerTingkatSP.selectedItem?.toString()?.toIntOrNull()
        val tanggal = binding.etTanggal.text.toString()
        val keterangan = binding.etKeterangan.text.toString()

        if (namaNasabah.isEmpty() || tingkatSP==null || tanggal.isEmpty() || keterangan.isEmpty()) {
            Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            Log.w("SuratFragment", "Form submission failed: empty fields")
            return
        }

        val nasabah = nasabahViewModel.nasabahList.value?.find { it.nama == namaNasabah }

        if (nasabah == null) {
            Toast.makeText(requireContext(), "Nasabah tidak valid", Toast.LENGTH_SHORT).show()
            Log.w("SuratFragment", "Form submission failed: invalid nasabah")
            return
        }

        val suratPeringatan = SuratPeringatan(
            no = nasabah.no,
            tingkat = tingkatSP,
            tanggal = tanggal,
            keterangan = keterangan,
            buktiGambar = selectedImageUri.toString(),
            scanPdf = selectedPdfUri.toString(),
            idAccountOfficer = nasabah.idAccountOfficer
        )
        val imageFile = selectedImageUri?.let { getFileFromUri(it) }
        val pdfFile = selectedPdfUri?.let { getFileFromUri(it) }
        nasabahViewModel.submitSuratPeringatan(suratPeringatan,  imageFile, pdfFile)
        Log.d("SuratFragment", "Surat peringatan submitted: $suratPeringatan")
    }
    private fun getFileFromUri(uri: Uri?): File? {
        uri ?: return null
        return context?.let { FileUtilsNasabah.getFileFromUri(it, uri) }
    }

    private fun resetForm() {
        binding.autoCompleteNasabah.setText("")
        binding.spinnerTingkatSP.setSelection(0)
        binding.etTanggal.setText("")
        binding.etKeterangan.setText("")
        binding.ivPreviewGambar.visibility = View.GONE
        binding.tvPdfName.visibility = View.GONE
        selectedImageUri = null
        selectedPdfUri = null
        Log.d("SuratFragment", "Form reset completed")
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val PICK_PDF_REQUEST = 2
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        hideLoadingDialog() // Ensure the loading dialog is dismissed when the fragment is destroyed
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_loading) // You need to create dialog_loading.xml layout
                .setCancelable(false)
                .create()
        }
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }
}






