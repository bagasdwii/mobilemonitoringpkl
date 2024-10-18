package com.example.mobilemonitoringbankbpr.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.lifecycle.ViewModelProvider
import com.example.mobilemonitoringbankbpr.FileUtilsNasabah
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Kunjungan
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.databinding.DialogSeacrhSpinnerBinding
import com.example.mobilemonitoringbankbpr.databinding.FragmentKunjunganBinding
import com.example.mobilemonitoringbankbpr.databinding.FragmentSuratBinding
import com.example.mobilemonitoringbankbpr.viewmodel.KunjunganViewModel
import com.example.mobilemonitoringbankbpr.viewmodel.SuratViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

class KunjunganFragment : Fragment() {
    private var _binding: FragmentKunjunganBinding? = null
    private val binding get() = _binding!!

    private lateinit var nasabahViewModel: KunjunganViewModel
    private var loadingDialog: AlertDialog? = null

    private val calendar = Calendar.getInstance()
    private var selectedImageUri: Uri? = null
//    private var selectedPdfUri: Uri? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKunjunganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        nasabahViewModel = ViewModelProvider(this).get(KunjunganViewModel::class.java)

        setupNasabahDropdown()
        setupDatePicker()
        setupImagePicker()
        observeViewModel()
        binding.btnSubmit.setOnClickListener {
            showConfirmationDialog()
        }

        nasabahViewModel.isSubmitting.observe(viewLifecycleOwner, { isSubmitting ->
            binding.btnSubmit.isEnabled = !isSubmitting
            if (isSubmitting) {
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }
            Log.d("KunjunganFragment", "isSubmitting: $isSubmitting")
        })



        nasabahViewModel.fetchNasabahList()
        nasabahViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }
            Log.d("KunjunganFragment", "isLoading: $isLoading")
        })
        updateDateInView()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("kunjungan_prefs", Context.MODE_PRIVATE)
        // Inisialisasi FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Membuat LocationRequest untuk mengambil update secara real-time
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // setiap 10 detik
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // LocationCallback untuk menangani lokasi baru
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateCurrentLocation(location)
                }
            }
        }
    }

    // Registrasi callback untuk meminta izin lokasi
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                // Izin diberikan, mulai update lokasi
                startLocationUpdates()
            } else {
                // Izin ditolak, tampilkan pesan kesalahan
                showPermissionDeniedDialog()
            }
        }
    private fun showPermissionDeniedDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_alert_fail, null)

        val title = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMessage = dialogView.findViewById<TextView>(R.id.alertMessage)

        title.text = "Izin Diperlukan"
        alertMessage.text = "Kunjungan memerlukan izin lokasi dan GPS. Aktifkan izin lokasi untuk menggunakan fitur ini."

        alertDialog.setView(dialogView)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ ->
            // Masuk ke pengaturan aplikasi untuk mengaktifkan izin
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                data = uri
            }
            startActivity(intent)
            sharedPreferences.edit().remove("dialog_shown").apply()
            requireActivity().finish()
            dialog.dismiss()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Batal") { dialog, _ ->
            dialog.dismiss()
            // Tutup aplikasi jika user tidak ingin memberikan izin
            sharedPreferences.edit().remove("dialog_shown").apply()
            requireActivity().finish()
        }

        alertDialog.show()

        // Simpan status bahwa dialog sudah pernah ditampilkan
        sharedPreferences.edit().putBoolean("dialog_shown", true).apply()
    }
    override fun onResume() {
        super.onResume()

        // Cek apakah izin telah diberikan
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Jika izin diberikan, mulai update lokasi
            startLocationUpdates()
        } else {
            // Cek apakah dialog sudah pernah ditampilkan
            if (!sharedPreferences.getBoolean("dialog_shown", false)) {
                // Jika belum, tampilkan dialog dan simpan statusnya
                showPermissionDeniedDialog()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        // Hentikan update lokasi saat fragment tidak aktif
        stopLocationUpdates()
    }

    private fun requestLocationPermission() {
        // Minta izin jika belum diberikan
        requestPermissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jika izin tidak tersedia, minta izin lagi
            requestLocationPermission()
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // Fungsi untuk update lokasi yang diterima
    private fun updateCurrentLocation(location: Location) {
        currentLatitude = location.latitude
        currentLongitude = location.longitude
        Log.d("KunjunganFragment", "Lokasi diperbarui: $currentLatitude, $currentLongitude")
    }

    // Fungsi untuk mendapatkan lokasi terbaru atau menampilkan pesan error
    private fun getCurrentLocation(onLocationReceived: (Double, Double) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        // Cek apakah lokasi sudah diterima dari LocationCallback
        if (currentLatitude != null && currentLongitude != null) {
            onLocationReceived(currentLatitude!!, currentLongitude!!)
        } else {
            Toast.makeText(requireContext(), "Tidak dapat menemukan lokasi, pastikan GPS aktif.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog, null)

        val title = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMessage = dialogView.findViewById<TextView>(R.id.alertMessage)

        title.text = "Peringatan"
        alertMessage.text = "Apakah Anda yakin ingin mengirimkan data nasabah ini ?"

        alertDialog.setView(dialogView)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "IYA") { dialog, _ ->
            submitKunjungan()
            dialog.dismiss()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"TIDAK"){ dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun observeViewModel() {
        nasabahViewModel.submissionError.observe(viewLifecycleOwner, { errorMessage ->
            errorMessage?.let {
                alertFail(it)
                Log.w("KunjunganFragment", "Form submission failed: $it")
            }
        })

        nasabahViewModel.isSubmissionSuccessful.observe(viewLifecycleOwner, { isSuccessful ->
            if (isSuccessful) {
                resetForm()
                alertSuccess("Kunjungan berhasil dikirim.")
            }
        })
    }
    private fun setupNasabahDropdown() {
        nasabahViewModel.nasabahList.observe(viewLifecycleOwner, { nasabahList ->
            // Gabungkan nama dan tingkat untuk setiap nasabah
            val arrayList = ArrayList(nasabahList.map { "${it.nama}" })
            Log.d("KunjunganFragment", "Nasabah list updated: $nasabahList")

            binding.autoCompleteNasabah.setOnClickListener {
                showDialog(arrayList)
            }
        })
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
            binding.autoCompleteNasabah.setText(adapter.getItem(position))
            dialog.dismiss()
        }
    }


    private fun setupDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Pastikan waktu saat ini juga diatur
            calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND))

            updateDateInView()
            Log.d("KunjunganFragment", "Date selected: ${calendar.time}")
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
        val format = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(format, Locale.US)

        // Pastikan waktu saat ini diatur saat updateDateInView() dipanggil
        calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND))

        val currentTime = calendar.time
        Log.d("KunjunganFragment", "Current calendar time: $currentTime")
        val formattedDate = sdf.format(currentTime)
        binding.etTanggal.setText(formattedDate)
        Log.d("KunjunganFragment", "Date updated in view: $formattedDate")
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
            Log.d("KunjunganFragment", "Image picker intent launched")
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
        Log.d("KunjunganFragment", "Image output URI: $outputFileUri")
        return outputFileUri
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureImageOutputUri())
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            try {
                startActivityForResult(intent, CAMERA_REQUEST)
                Log.d("KunjunganFragment", "Camera intent launched")
            } catch (e: ActivityNotFoundException) {
                Log.e("KunjunganFragment", "Camera intent could not be launched", e)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("KunjunganFragment", "onActivityResult called with requestCode: $requestCode, resultCode: $resultCode")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    val photoUri = getCaptureImageOutputUri()
                    Log.d("KunjunganFragment", "Photo URI: $photoUri")
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, photoUri)
                        Log.d("KunjunganFragment", "Bitmap captured: $bitmap")
                        bitmap?.let {
                            val watermarkedBitmap = addWatermark(it)
                            val file = saveBitmapToFile(watermarkedBitmap)
                            selectedImageUri = Uri.fromFile(file)
                            binding.ivPreviewGambar.setImageBitmap(watermarkedBitmap)
                            binding.ivPreviewGambar.visibility = View.VISIBLE
                            Log.d("KunjunganFragment", "Image captured and saved: $selectedImageUri")
                        }
                    } catch (e: IOException) {
                        Log.e("KunjunganFragment", "Error processing captured image", e)
                    }
                }
            }
        }
    }


    private fun saveBitmapToFile(bitmap: Bitmap): File? {
        val fileName = "captured_image_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().cacheDir, fileName)
        return try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
            out.close()
            Log.d("KunjunganFragment", "Bitmap saved to file: $file")
            file
        } catch (e: IOException) {
            Log.e("KunjunganFragment", "Error saving bitmap to file", e)
            null
        }
    }
//    private fun getCurrentLocation(onLocationReceived: (Double, Double) -> Unit) {
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
//            return
//        }
//
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            if (location != null) {
//                val latitude = location.latitude
//                val longitude = location.longitude
//                onLocationReceived(latitude, longitude)
//            } else {
//                alertFail("Tidak dapat menemukan lokasi, pastikan GPS aktif.")
//            }
//        }.addOnFailureListener {
//            alertFail("Gagal mengambil lokasi: ${it.message}")
//        }
//    }

    private fun submitKunjungan() {
        // Cek dan ambil koordinat sebelum melanjutkan
        getCurrentLocation { latitude, longitude ->
            val namaNasabah = binding.autoCompleteNasabah.text.toString()
            val keterangan = binding.etKeterangan.text.toString()
            val tanggal = binding.etTanggal.text.toString()
            val koordinat = "$latitude, $longitude"

            if (namaNasabah.isNullOrEmpty() || keterangan.isEmpty() || tanggal.isEmpty() || selectedImageUri == null) {
                alertFail("Gagal mengirim pastikan semua field diisi")
                Log.w("KunjunganFragment", "Form submission failed: empty fields")
                return@getCurrentLocation
            }

            val nasabah = nasabahViewModel.nasabahList.value?.find { it.nama == namaNasabah }

            if (nasabah == null) {
                Toast.makeText(requireContext(), "Nasabah tidak valid", Toast.LENGTH_SHORT).show()
                Log.w("KunjunganFragment", "Form submission failed: invalid nasabah")
                return@getCurrentLocation
            }

            val kunjungan = Kunjungan(
                no = nasabah.no,
                keterangan = keterangan,
                tanggal = tanggal,
                koordinat = koordinat,
                bukti_gambar = selectedImageUri.toString(),
            )

            val imageFile = selectedImageUri?.let { getFileFromUri(it) }
            nasabahViewModel.submitKunjungan(kunjungan, imageFile)
            Log.d("KunjunganFragment", "Kunjungan submitted: $kunjungan")
            Log.d("KunjunganFragment", "Gambar URI: $selectedImageUri")
            Log.d("KunjunganFragment", "Gambar File Path: ${imageFile?.absolutePath}")
        }
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
            nasabahViewModel.fetchNasabahList()
            dialog.dismiss()
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
        binding.etKeterangan.setText("")
        binding.etKoordinat.setText("")
        updateDateInView()
        binding.ivPreviewGambar.visibility = View.GONE
        selectedImageUri = null
//        selectedPdfUri = null
        Log.d("KunjunganFragment", "Form reset completed")
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
        //        private const val PICK_PDF_REQUEST = 1002
        private const val REQUEST_CAMERA_PERMISSION = 1003
    }
    override fun onDestroy() {
        super.onDestroy()
        // Hapus status SharedPreferences ketika aplikasi ditutup
        sharedPreferences.edit().remove("dialog_shown").apply()
    }
}