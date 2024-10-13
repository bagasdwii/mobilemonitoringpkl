package com.example.mobilemonitoringbankbpr.adapter

import android.app.Dialog
import java.text.NumberFormat
import java.util.Locale
import android.app.DownloadManager
import android.app.Activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.databinding.ItemNasabahBinding
import com.example.mobilemonitoringbankbpr.viewmodel.MonitoringViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import com.bumptech.glide.request.target.Target

import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.data.Kunjungan
import com.example.mobilemonitoringbankbpr.databinding.DialogSeacrhSpinnerBinding
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import com.example.mobilemonitoringbankbpr.viewmodel.KunjunganViewModel
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class MonitoringAdapter(
    private val viewModel: MonitoringViewModel,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val coroutineScope: CoroutineScope,
    private val viewModelKunjungan: KunjunganViewModel

) : ListAdapter<Nasabah, MonitoringAdapter.NasabahViewHolder>(NasabahDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NasabahViewHolder {
        val binding = ItemNasabahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NasabahViewHolder(binding, viewModel, context, lifecycleOwner, coroutineScope, viewModelKunjungan)
    }

    override fun onBindViewHolder(holder: NasabahViewHolder, position: Int) {
        val nasabah = getItem(position)
        holder.bind(nasabah)
    }
    private val numberFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    inner class NasabahViewHolder(
        private val binding: ItemNasabahBinding,
        private val viewModel: MonitoringViewModel,
        private val context: Context,
        private val lifecycleOwner: LifecycleOwner,
        private val coroutineScope: CoroutineScope,
        private val kunjunganViewModel: KunjunganViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        var isSS02Shown = false
        var isSPE03Shown = false
        fun bind(nasabah: Nasabah) {
            isSS02Shown = false
            isSPE03Shown = false
            // Set nama dan cabang nasabah

            binding.NamaNasabah.text = nasabah.nama
            binding.CabangNasabah.text = nasabah.cabang
            binding.detailNasabah.setOnClickListener {
                showDetailNasabahDialog(nasabah)
            }

            // Handling Peringatan
            // Filter surat berdasarkan kategori
            val peringatanList = nasabah.suratPeringatan.filter { it.kategori == "Peringatan" }
            val somasiList = nasabah.suratPeringatan.filter { it.kategori == "Somasi" }
            val pendampinganList = nasabah.suratPeringatan.filter { it.kategori == "Pendampingan" }

            // Ambil tingkat tertinggi dari masing-masing kategori
            val highestPeringatanTingkat = peringatanList.maxByOrNull { it.tingkat }?.tingkat ?: 0
            val highestSomasiTingkat = somasiList.maxByOrNull { it.tingkat }?.tingkat ?: 0
            val highestPendampinganTingkat = pendampinganList.maxByOrNull { it.tingkat }?.tingkat ?: 0

            // Log data untuk debugging
            Log.d("MonitoringAdapter", "Peringatan: $peringatanList, Somasi: $somasiList, Pendampingan: $pendampinganList")


            // Tampilkan tombol sesuai kategori dan tingkat tertinggi
            // 1. Untuk kategori Peringatan
            showPeringatanButtons(highestPeringatanTingkat)

            // 2. Untuk kategori Somasi
            showSomasiButtons(highestSomasiTingkat)

            // 3. Untuk kategori Pendampingan
            showPendampinganButtons(highestPendampinganTingkat)

            setupPeringatanButtonListeners(peringatanList)
            setupSomasiButtonListeners(somasiList)
            setupPendampinganButtonListeners(pendampinganList)
        }
        // Fungsi untuk menampilkan tombol berdasarkan tingkat peringatan tertinggi
        private fun showPeringatanButtons(highestTingkat: Int) {
            when (highestTingkat) {
                3 -> binding.btnSP3.visibility = View.VISIBLE
                2 -> binding.btnSP2.visibility = View.VISIBLE
                1 -> binding.btnSP1.visibility = View.VISIBLE
                else -> binding.btnSP01.visibility = View.VISIBLE
            }
            // Tampilkan SS02 jika belum ditampilkan oleh kategori lain
            if (!isSS02Shown) {
                binding.btnSS02.visibility = View.VISIBLE
                isSS02Shown = true // Set flag agar SS02 tidak ditampilkan lagi
            }

            // Tampilkan SPE03 jika belum ditampilkan oleh kategori lain
            if (!isSPE03Shown) {
                binding.btnSPE03.visibility = View.VISIBLE
                isSPE03Shown = true // Set flag agar SPE03 tidak ditampilkan lagi
            }

        }

        // Fungsi untuk menampilkan tombol berdasarkan tingkat somasi tertinggi
        // Fungsi untuk menampilkan tombol berdasarkan tingkat somasi tertinggi
        private fun showSomasiButtons(highestTingkat: Int) {
            when (highestTingkat) {
                3 -> {
                    binding.btnSS3.visibility = View.VISIBLE
                    binding.btnSS02.visibility = View.GONE // Sembunyikan SS02 jika SS3 ditampilkan
                }
                2 -> {
                    binding.btnSS2.visibility = View.VISIBLE
                    binding.btnSS02.visibility = View.GONE // Sembunyikan SS02 jika SS2 ditampilkan
                }
                1 -> {
                    binding.btnSS1.visibility = View.VISIBLE
                    binding.btnSS02.visibility = View.GONE // Sembunyikan SS02 jika SS1 ditampilkan
                }
                else -> {
                    if (!isSS02Shown) {
                        binding.btnSS02.visibility = View.VISIBLE
                        isSS02Shown = true // Set flag agar SS02 tidak ditampilkan lagi
                    }
                }
            }

            // Tampilkan SPE03 jika belum ditampilkan oleh kategori lain
            if (!isSPE03Shown) {
                binding.btnSPE03.visibility = View.VISIBLE
                isSPE03Shown = true // Set flag agar SPE03 tidak ditampilkan lagi
            }
        }


        // Fungsi untuk menampilkan tombol berdasarkan tingkat pendampingan tertinggi
        private fun showPendampinganButtons(highestTingkat: Int) {
            when (highestTingkat) {
                3 -> {
                    binding.btnSPE3.visibility = View.VISIBLE
                    binding.btnSPE03.visibility = View.GONE // Sembunyikan SS02 jika SS3 ditampilkan
                }
                2 -> {
                    binding.btnSPE2.visibility = View.VISIBLE
                    binding.btnSPE03.visibility = View.GONE // Sembunyikan SS02 jika SS2 ditampilkan
                }
                1 -> {
                    binding.btnSPE1.visibility = View.VISIBLE
                    binding.btnSPE03.visibility = View.GONE // Sembunyikan SS02 jika SS1 ditampilkan
                }
                else -> {
                    if (!isSPE03Shown) {
                        binding.btnSPE03.visibility = View.VISIBLE
                        isSPE03Shown = true // Set flag agar SPE03 tidak ditampilkan lagi
                    }
                }
            }

            // Tampilkan SS02 jika belum ditampilkan oleh kategori lain
            if (!isSS02Shown) {
                binding.btnSS02.visibility = View.VISIBLE
                isSS02Shown = true // Set flag agar SS02 tidak ditampilkan lagi
            }
        }
        // Fungsi untuk mengatur OnClickListener untuk tombol-tombol Peringatan
        private fun setupPeringatanButtonListeners(peringatanList: List<SuratPeringatan>) {
            binding.btnSP1.setOnClickListener {
                Log.d("MonitoringAdapter", "btnSP1 clicked")
                setupSuratDropdown(peringatanList)
            }
            binding.btnSP2.setOnClickListener {
                Log.d("MonitoringAdapter", "btnSP2 clicked")
                setupSuratDropdown(peringatanList)
            }
            binding.btnSP3.setOnClickListener {
                Log.d("MonitoringAdapter", "btnSP3 clicked")
                setupSuratDropdown(peringatanList)
            }
        }

        // Fungsi untuk mengatur OnClickListener untuk tombol-tombol Somasi
        private fun setupSomasiButtonListeners(somasiList: List<SuratPeringatan>) {
            binding.btnSS1.setOnClickListener {
                Log.d("MonitoringAdapter", "btnSS1 clicked")
                setupSuratDropdown(somasiList)
            }
            binding.btnSS2.setOnClickListener {
                Log.d("MonitoringAdapter", "btnSS2 clicked")
                setupSuratDropdown(somasiList)
            }
            binding.btnSS3.setOnClickListener {
                Log.d("MonitoringAdapter", "btnSS3 clicked")
                setupSuratDropdown(somasiList)
            }
        }

        // Fungsi untuk mengatur OnClickListener untuk tombol-tombol Pendampingan
        private fun setupPendampinganButtonListeners(pendampinganList: List<SuratPeringatan>) {
            binding.btnSPE1.setOnClickListener {
                Log.d("MonitoringAdapter", "btnSPE1 clicked")
                setupSuratDropdown(pendampinganList)
            }
            binding.btnSPE2.setOnClickListener {
                Log.d("MonitoringAdapter", "btnSPE2 clicked")
                setupSuratDropdown(pendampinganList)
            }
            binding.btnSPE3.setOnClickListener {
                Log.d("MonitoringAdapter", "btnSPE3 clicked")
                setupSuratDropdown(pendampinganList)
            }
        }
        fun showDetailKunjunganDialog(kunjungan: Kunjungan) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_detail_kunjungan, null)

            val tanggalTextView = dialogView.findViewById<TextView>(R.id.tvtanggal)
            val noNasabahTextView = dialogView.findViewById<TextView>(R.id.tvnoNasabah)
            val userIdTextView = dialogView.findViewById<TextView>(R.id.tvaccountOfficer)
            val keteranganTextView = dialogView.findViewById<TextView>(R.id.tvketerangan)
            val gmapLinkTextView = dialogView.findViewById<TextView>(R.id.tvgmap)
            val buktiGambarImageView = dialogView.findViewById<ImageView>(R.id.ivBuktiGambar)

            // Set the values
            tanggalTextView.text = kunjungan.tanggal
            noNasabahTextView.text = kunjungan.no_nasabah.toString()
            userIdTextView.text = kunjungan.user_id.toString()
            keteranganTextView.text = kunjungan.keterangan
            gmapLinkTextView.text = kunjungan.bukti_gambar

            // Load image using Glide
            Glide.with(context)
                .load(kunjungan.bukti_gambar)
                .into(buktiGambarImageView)

            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            dialog.show()
        }

        private fun showDetailNasabahDialog(nasabah: Nasabah) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_detail_nasabah, null)
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)

            // Set RecyclerView Adapter
            val kunjunganAdapter = KunjunganAdapter(emptyList()) { kunjungan ->
                showDetailKunjunganDialog(kunjungan)
            }
            recyclerView.adapter = kunjunganAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)

            // Observe data
            viewModelKunjungan.kunjunganList.observe(lifecycleOwner, { kunjunganList ->
                kunjunganAdapter.updateData(kunjunganList)
            })

            viewModelKunjungan.fetchKunjungan(nasabah.no) // Ambil data kunjungan menggunakan no nasabah
            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            dialogView.findViewById<TextView>(R.id.tvnoNasabah).text = nasabah.no.toString()
            dialogView.findViewById<TextView>(R.id.tvnasabahName).text = nasabah.nama
            dialogView.findViewById<TextView>(R.id.tvpokok).text =
                numberFormat.format(nasabah.pokok.toDouble())
            dialogView.findViewById<TextView>(R.id.tvbunga).text =
                numberFormat.format(nasabah.bunga.toDouble())
            dialogView.findViewById<TextView>(R.id.tvdenda).text =
                numberFormat.format(nasabah.denda.toDouble())
            dialogView.findViewById<TextView>(R.id.tvtotal).text =
                numberFormat.format(nasabah.total.toDouble())
            dialogView.findViewById<TextView>(R.id.tvketerangan).text = nasabah.keterangan
            dialogView.findViewById<TextView>(R.id.tvtanggal_jtp).text = nasabah.tanggal_jtp
            dialogView.findViewById<TextView>(R.id.tvaccountOfficer).text = nasabah.accountOfficer

            dialogView.findViewById<Button>(R.id.closeButton).setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
        private fun setupSuratDropdown(peringatanList: List<SuratPeringatan>) {
            // Gabungkan nama dan tingkat untuk setiap surat
            val arrayList = ArrayList(peringatanList.map { "${it.kategori} - ${it.tingkat}" })

            Log.d("MonitoringAdapter", "Surat list updated setup: $peringatanList")

            Log.d("MonitoringAdapter", "Array list updated: $arrayList")

            showDialog(arrayList, peringatanList)
        }

        private fun showDialog(arrayList: ArrayList<String>, peringatanList: List<SuratPeringatan>) {
            // Pastikan nama class binding sesuai dengan layout XML yang digunakan
            val dialogBinding = DialogSeacrhSpinnerBinding.inflate(LayoutInflater.from(context))
            val dialog = Dialog(context).apply {
                setContentView(dialogBinding.root)
                window?.setLayout(900, 2000) // Ukuran dialog dapat disesuaikan
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()
            }


            val adapter = ArrayAdapter(context, R.layout.spinner_item_surat, arrayList)
            dialogBinding.listView.adapter = adapter

            // Filter pencarian
            dialogBinding.editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            // Aksi saat item di klik
            dialogBinding.listView.setOnItemClickListener { _, _, position, _ ->
                val selectedSurat = peringatanList[position]
                showDetailNasabahDialog(selectedSurat)
                dialog.dismiss()
            }
        }

        private fun showDetailNasabahDialog(suratPeringatan: SuratPeringatan) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_surat_peringatan, null)
            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()
            dialogView.findViewById<TextView>(R.id.tvKategori).text =
                "Kategori Surat : ${suratPeringatan.kategori}"
            dialogView.findViewById<TextView>(R.id.tvTingkat).text =
                "Tingkat Ke : ${suratPeringatan.tingkat}"
            dialogView.findViewById<TextView>(R.id.tvDibuat).text =
                "Tanggal: ${suratPeringatan.dibuat}"
            dialogView.findViewById<TextView>(R.id.tvKembali).text =
                "Tanggal: ${suratPeringatan.kembali}"
            dialogView.findViewById<TextView>(R.id.tvDiserahkan).text =
                "Tanggal: ${suratPeringatan.diserahkan}"


            val ivBuktiGambar = dialogView.findViewById<ImageView>(R.id.ivBuktiGambar)
            val tvPdfFileName = dialogView.findViewById<TextView>(R.id.tvPdfFileName)
            val btnDownloadPdf = dialogView.findViewById<Button>(R.id.btnDownloadPdf)

            suratPeringatan.bukti_gambar?.let { bukti_gambar ->
                loadGambar(bukti_gambar.replace("bukti_gambar/", ""), ivBuktiGambar)
            }

            suratPeringatan.scan_pdf?.let { pdfUrl ->
                val pdfFileName = pdfUrl.substringAfterLast("/")
                tvPdfFileName.text = pdfFileName
                btnDownloadPdf.setOnClickListener {
                    downloadPdf(pdfUrl.replace("scan_pdf/", ""))
                }
            }

            alertDialog.show()
        }

        private fun loadGambar(filename: String, imageView: ImageView) {
            val imageUrl = "${RetrofitClient.getBaseUrl()}api/surat-peringatan/gambar/$filename"
            Log.d("NasabahAdapter", "Memuat gambar dari URL: $imageUrl")

            val localStorage = LocalStorage(context)
            val token = localStorage.token

            val glideUrl = GlideUrl(imageUrl, LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $token")
                .build())

            Glide.with(imageView.context)
                .load(glideUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("NasabahAdapter", "Gagal memuat gambar", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(imageView)
        }

        private fun downloadPdf(filename: String) {
            val pdfUrl = "${RetrofitClient.getBaseUrl()}api/surat-peringatan/pdf/$filename"
            val localStorage = LocalStorage(context)
            val token = localStorage.token

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(pdfUrl)

            val request = DownloadManager.Request(uri)
            request.setTitle("Mengunduh $filename")
            request.setDescription("Sedang mengunduh file PDF...")
            request.addRequestHeader("Authorization", "Bearer $token")
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            downloadManager.enqueue(request)
        }

    }
    class NasabahDiffCallback : DiffUtil.ItemCallback<Nasabah>() {
        override fun areItemsTheSame(oldItem: Nasabah, newItem: Nasabah): Boolean {
            return oldItem.no == newItem.no
        }

        override fun areContentsTheSame(oldItem: Nasabah, newItem: Nasabah): Boolean {
            return oldItem == newItem
        }
    }
}

