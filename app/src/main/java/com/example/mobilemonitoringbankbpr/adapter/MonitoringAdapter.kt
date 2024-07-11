package com.example.mobilemonitoringbankbpr.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.bumptech.glide.request.RequestListener
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

            val suratPeringatan = nasabah.suratPeringatan
            if (suratPeringatan != null) {
                when (suratPeringatan.tingkat) {
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
                }
            } else {
                binding.btnSP01.visibility = View.VISIBLE
                binding.btnSP02.visibility = View.VISIBLE
                binding.btnSP03.visibility = View.VISIBLE
            }

            binding.btnSP1.setOnClickListener {
                Log.d("NasabahAdapter", "btnSP1 clicked for Nasabah No: ${nasabah.no}")
                showSuratPeringatanDialog(suratPeringatan)
            }
            binding.btnSP2.setOnClickListener {
                Log.d("NasabahAdapter", "btnSP2 clicked for Nasabah No: ${nasabah.no}")
                showSuratPeringatanDialog(suratPeringatan)
            }
            binding.btnSP3.setOnClickListener {
                Log.d("NasabahAdapter", "btnSP3 clicked for Nasabah No: ${nasabah.no}")
                showSuratPeringatanDialog(suratPeringatan)
            }
        }

        private fun showSuratPeringatanDialog(suratPeringatan: SuratPeringatan?) {
            if (suratPeringatan == null) {
                Log.e("NasabahAdapter", "Surat Peringatan is null")
                return
            }

            Log.d(
                "NasabahAdapter",
                "Showing Surat Peringatan dialog for Surat Peringatan No: ${suratPeringatan.no}"
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
            val pdfContainer = dialogView.findViewById<FrameLayout>(R.id.pdfContainer)

            suratPeringatan.bukti_gambar?.let { bukti_gambar ->
                Log.d("NasabahAdapter", "Loading gambar: $bukti_gambar")
                loadGambar(bukti_gambar, ivBuktiGambar)

            }

            suratPeringatan.scan_pdf?.let { pdfUrl ->
                Log.d("NasabahAdapter", "Loading PDF: $pdfUrl")
                loadPdf(pdfUrl, pdfContainer)
            }

            alertDialog.show()
        }
        private fun loadGambar(filename: String, imageView: ImageView) {
            val imageUrl = "http://192.168.1.9:8000/api/surat-peringatan/gambar/$filename"
            Log.d("NasabahAdapter", "Loading image from URL: $imageUrl")
            Glide.with(imageView.context)
                .load(imageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("NasabahAdapter", "Failed to load image", e)
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


        private fun loadPdf(filename: String, pdfContainer: FrameLayout) {
            val pdfUrl = context.getString(R.string.api_server) + "/surat-peringatan/pdf/$filename"
            val pdfFile = File(context.cacheDir, "temp.pdf")

            coroutineScope.launch(Dispatchers.IO) {
                try {
                    Log.d("NasabahAdapter", "Downloading PDF from URL: $pdfUrl")
                    val response = downloadPdf(pdfUrl)
                    response?.let {
                        pdfFile.writeBytes(it)
                        Log.d("NasabahAdapter", "PDF downloaded: $filename")

                        withContext(Dispatchers.Main) {
                            displayPdf(pdfFile, pdfContainer)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("NasabahAdapter", "Error downloading PDF: $pdfUrl", e)
                }
            }
        }

        private fun downloadPdf(urlString: String): ByteArray? {
            return try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.doInput = true
                connection.connect()

                val inputStream: InputStream = connection.inputStream
                val buffer = ByteArrayOutputStream()
                val data = ByteArray(1024)
                var nRead: Int
                while (inputStream.read(data, 0, 1024).also { nRead = it } != -1) {
                    buffer.write(data, 0, nRead)
                }
                buffer.flush()
                buffer.toByteArray()
            } catch (e: Exception) {
                Log.e("NasabahAdapter", "Error in downloadPdf: ${e.message}")
                null
            }
        }



        private fun displayPdf(file: File, pdfContainer: FrameLayout) {
            try {
                val fileDescriptor =
                    ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(fileDescriptor)

                // Display the first page of the PDF
                val page = renderer.openPage(0)
                // Add your code to display the page in the pdfContainer
                Log.d("NasabahAdapter", "Displaying page 0 of the PDF")
                page.close()
                renderer.close()
                fileDescriptor.close()
            } catch (e: IOException) {
                Log.e("NasabahAdapter", "Error loading PDF: ${e.message}", e)
            }
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
