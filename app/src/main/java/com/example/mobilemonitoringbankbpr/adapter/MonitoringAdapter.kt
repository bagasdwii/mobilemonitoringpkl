package com.example.mobilemonitoringbankbpr.adapter

import java.text.NumberFormat
import java.util.Locale
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
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
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
class MonitoringAdapter(
    private val viewModel: MonitoringViewModel,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val coroutineScope: CoroutineScope
) : ListAdapter<Nasabah, MonitoringAdapter.NasabahViewHolder>(NasabahDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NasabahViewHolder {
        val binding = ItemNasabahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NasabahViewHolder(binding, viewModel, context, lifecycleOwner, coroutineScope)
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
        private val coroutineScope: CoroutineScope
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(nasabah: Nasabah) {
            binding.btnSP1.visibility = View.GONE
            binding.btnSP2.visibility = View.GONE
            binding.btnSP3.visibility = View.GONE

            binding.btnSP01.visibility = View.GONE
            binding.btnSP02.visibility = View.GONE
            binding.btnSP03.visibility = View.GONE

            binding.NamaNasabah.text = nasabah.nama
            binding.CabangNasabah.text = nasabah.cabang
            binding.detailNasabah.setOnClickListener {
                showDetailNasabahDialog(nasabah)
            }


            val highestTingkat = nasabah.suratPeringatan.maxByOrNull { it.tingkat }?.tingkat ?: 0
            when (highestTingkat) {
                3 -> {
                    binding.btnSP1.visibility = View.VISIBLE
                    binding.btnSP2.visibility = View.VISIBLE
                    binding.btnSP3.visibility = View.VISIBLE
                }

                2 -> {
                    binding.btnSP1.visibility = View.VISIBLE
                    binding.btnSP2.visibility = View.VISIBLE
                    binding.btnSP03.visibility = View.VISIBLE
                }

                1 -> {
                    binding.btnSP1.visibility = View.VISIBLE
                    binding.btnSP02.visibility = View.VISIBLE
                    binding.btnSP03.visibility = View.VISIBLE
                }

                else -> {
                    binding.btnSP01.visibility = View.VISIBLE
                    binding.btnSP02.visibility = View.VISIBLE
                    binding.btnSP03.visibility = View.VISIBLE
                }
            }

            binding.btnSP1.setOnClickListener {
                showSuratPeringatanDialog(nasabah.suratPeringatan, 1)
            }
            binding.btnSP2.setOnClickListener {
                showSuratPeringatanDialog(nasabah.suratPeringatan, 2)
            }
            binding.btnSP3.setOnClickListener {
                showSuratPeringatanDialog(nasabah.suratPeringatan, 3)
            }


        }

        private fun showDetailNasabahDialog(nasabah: Nasabah) {
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_detail_nasabah, null)
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
            dialogView.findViewById<TextView>(R.id.tvttd).text = nasabah.ttd
            dialogView.findViewById<TextView>(R.id.tvkembali).text = nasabah.kembali
            dialogView.findViewById<TextView>(R.id.tvcabang).text = nasabah.cabang
            dialogView.findViewById<TextView>(R.id.tvwilayah).text = nasabah.wilayah
            dialogView.findViewById<TextView>(R.id.tvadminKas).text = nasabah.adminkas
            dialogView.findViewById<TextView>(R.id.tvaccountOfficer).text = nasabah.accountOfficer

            dialogView.findViewById<Button>(R.id.closeButton).setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        private fun showSuratPeringatanDialog(
            suratPeringatanList: List<SuratPeringatan>,
            tingkat: Int
        ) {
            val suratPeringatan = suratPeringatanList.find { it.tingkat == tingkat }
            if (suratPeringatan == null) {
                Log.e("NasabahAdapter", "Surat Peringatan tingkat $tingkat tidak ditemukan")
                return
            }

            Log.d(
                "NasabahAdapter",
                "Menampilkan dialog untuk Surat Peringatan No: ${suratPeringatan.no}"
            )

            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_surat_peringatan, null)
            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            dialogView.findViewById<TextView>(R.id.tvTingkat).text =
                "Tingkat: ${suratPeringatan.tingkat}"
            dialogView.findViewById<TextView>(R.id.tvTanggal).text =
                "Tanggal: ${suratPeringatan.tanggal}"
            dialogView.findViewById<TextView>(R.id.tvKeterangan).text =
                "Keterangan: ${suratPeringatan.keterangan}"

            val ivBuktiGambar = dialogView.findViewById<ImageView>(R.id.ivBuktiGambar)
            val tvPdfFileName = dialogView.findViewById<TextView>(R.id.tvPdfFileName)
            val btnDownloadPdf = dialogView.findViewById<Button>(R.id.btnDownloadPdf)

            suratPeringatan.bukti_gambar?.let { bukti_gambar ->
                loadGambar(bukti_gambar.replace("private/surat_peringatan/", ""), ivBuktiGambar)
            }

            suratPeringatan.scan_pdf?.let { pdfUrl ->
                val pdfFileName = pdfUrl.substringAfterLast("/")
                tvPdfFileName.text = pdfFileName
                btnDownloadPdf.setOnClickListener {
                    downloadPdf(pdfUrl.replace("private/surat_peringatan/", ""))
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

