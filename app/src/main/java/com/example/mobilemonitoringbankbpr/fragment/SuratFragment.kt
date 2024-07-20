package com.example.mobilemonitoringbankbpr.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog.show
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.FileUtilsNasabah
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.databinding.DialogSeacrhSpinnerBinding
import com.example.mobilemonitoringbankbpr.databinding.FragmentSuratBinding
import com.example.mobilemonitoringbankbpr.viewmodel.SuratViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


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
        updateDateInView()
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

    private fun setupPdfPicker() {
        binding.btnPilihPdf.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, PICK_PDF_REQUEST)
            Log.d("SuratFragment", "PDF picker intent launched")
        }
    }

    private fun setupImagePicker() {
        binding.btnPilihGambar.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            } else {
                openCamera()
            }
            Log.d("SuratFragment", "Image picker intent launched")
        }
    }

    private fun getCaptureImageOutputUri(): Uri? {
        var outputFileUri: Uri? = null
        val getImage = requireActivity().externalCacheDir
        if (getImage != null) {
            val file = File(getImage.path, "captured_image.jpg")
            outputFileUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )
        }
        Log.d("SuratFragment", "Image output URI: $outputFileUri")
        return outputFileUri
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureImageOutputUri())
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            try {
                startActivityForResult(intent, CAMERA_REQUEST)
                Log.d("SuratFragment", "Camera intent launched")
            } catch (e: ActivityNotFoundException) {
                Log.e("SuratFragment", "Camera intent could not be launched", e)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("SuratFragment", "onActivityResult called with requestCode: $requestCode, resultCode: $resultCode")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    val photoUri = getCaptureImageOutputUri()
                    Log.d("SuratFragment", "Photo URI: $photoUri")
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, photoUri)
                        Log.d("SuratFragment", "Bitmap captured: $bitmap")
                        bitmap?.let {
                            val watermarkedBitmap = addWatermark(it)
                            val file = saveBitmapToFile(watermarkedBitmap)
                            selectedImageUri = Uri.fromFile(file)
                            binding.ivPreviewGambar.setImageBitmap(watermarkedBitmap)
                            binding.ivPreviewGambar.visibility = View.VISIBLE
                            Log.d("SuratFragment", "Image captured and saved: $selectedImageUri")
                        }
                    } catch (e: IOException) {
                        Log.e("SuratFragment", "Error processing captured image", e)
                    }
                }

                PICK_PDF_REQUEST -> {
                    selectedPdfUri = data?.data
                    Log.d("SuratFragment", "PDF URI: $selectedPdfUri")
                    val fileName = selectedPdfUri?.let { getFileNameFromUri(it) }
                    binding.tvPdfName.text = fileName
                    binding.tvPdfName.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        fileName = it.getString(index)
                    }
                }
            }
        }
        if (fileName == null) {
            fileName = uri.path
            val cut = fileName?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                fileName = fileName?.substring(cut + 1)
            }
        }
        return fileName
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File? {
        val fileName = "captured_image_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().cacheDir, fileName)
        return try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
            out.close()
            Log.d("SuratFragment", "Bitmap saved to file: $file")
            file
        } catch (e: IOException) {
            Log.e("SuratFragment", "Error saving bitmap to file", e)
            null
        }
    }

    private fun submitSuratPeringatan() {
        val namaNasabah = binding.autoCompleteNasabah.text.toString()
        val tingkatSP = binding.spinnerTingkatSP.selectedItem?.toString()?.toIntOrNull()
        val tanggal = binding.etTanggal.text.toString()
        val keterangan = binding.etKeterangan.text.toString()

        if (namaNasabah.isEmpty() || tingkatSP == null || tanggal.isEmpty() || keterangan.isEmpty()) {
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
            bukti_gambar = selectedImageUri.toString(),
            scan_pdf = selectedPdfUri.toString(),
            id_account_officer = nasabah.id_account_officer
        )

        val imageFile = selectedImageUri?.let { getFileFromUri(it) }
        val pdfFile = selectedPdfUri?.let { getFileFromUri(it) }
        nasabahViewModel.submitSuratPeringatan(suratPeringatan, imageFile, pdfFile)
        Log.d("SuratFragment", "Surat peringatan submitted: $suratPeringatan")
        Log.d("SuratFragment", "Gambar URI: $selectedImageUri")
        Log.d("SuratFragment", "Gambar File Path: ${imageFile?.absolutePath}")
        Log.d("SuratFragment", "PDF URI: $selectedPdfUri")
    }

    private fun getFileFromUri(uri: Uri?): File? {
        uri ?: return null
        return context?.let { FileUtilsNasabah.getFileFromUri(it, uri) }
    }
    private fun addWatermark(bitmap: Bitmap): Bitmap {
        val watermarkText = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val result = bitmap.copy(bitmap.config, true)
        val canvas = Canvas(result)

        // Paint untuk stroke
        val strokePaint = Paint().apply {
            color = Color.BLACK
            textSize = 130f
            isAntiAlias = true
            alpha = 255
            style = Paint.Style.STROKE
            strokeWidth = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Paint untuk isi
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 130f
            isAntiAlias = true
            alpha = 255
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val bounds = Rect()
        textPaint.getTextBounds(watermarkText, 0, watermarkText.length, bounds)

        val x = bitmap.width - bounds.width() - 20f
        val y = bitmap.height - 20f

        // Gambar stroke terlebih dahulu
        canvas.drawText(watermarkText, x, y, strokePaint)
        // Gambar isi teks di atas stroke
        canvas.drawText(watermarkText, x, y, textPaint)

        return result
    }


    private fun resetForm() {
        binding.autoCompleteNasabah.setText("")
        binding.spinnerTingkatSP.setSelection(0)
        updateDateInView()
        binding.etKeterangan.setText("")
        binding.ivPreviewGambar.visibility = View.GONE
        binding.tvPdfName.visibility = View.GONE
        selectedImageUri = null
        selectedPdfUri = null
        Log.d("SuratFragment", "Form reset completed")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        hideLoadingDialog()
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

    companion object {
        private const val CAMERA_REQUEST = 1001
        private const val PICK_PDF_REQUEST = 1002
        private const val REQUEST_CAMERA_PERMISSION = 1003
        private const val PICK_IMAGE_REQUEST = 1004
    }
}







