package com.example.mobilemonitoringbankbpr.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.databinding.ItemNasabahBinding
import com.example.mobilemonitoringbankbpr.viewmodel.MonitoringViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

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
            binding.NamaNasabah.text = nasabah.nama
            binding.CabangNasabah.text = nasabah.cabang

            viewModel.checkSuratPeringatan(nasabah.no, 3, context) { hasSuratPeringatan3 ->
                if (hasSuratPeringatan3) {
                    binding.btnSP1.visibility = View.VISIBLE
                    binding.btnSP2.visibility = View.VISIBLE
                    binding.btnSP3.visibility = View.VISIBLE
                }
                Log.d("NasabahAdapter", "Nasabah No: ${nasabah.no}, Tingkat 3, hasSuratPeringatan: $hasSuratPeringatan3")

                if (!hasSuratPeringatan3) {
                    viewModel.checkSuratPeringatan(nasabah.no, 2, context) { hasSuratPeringatan2 ->
                        if (hasSuratPeringatan2) {
                            binding.btnSP1.visibility = View.VISIBLE
                            binding.btnSP2.visibility = View.VISIBLE
                            binding.btnSP03.visibility = View.VISIBLE
                        }
                        Log.d("NasabahAdapter", "Nasabah No: ${nasabah.no}, Tingkat 2, hasSuratPeringatan: $hasSuratPeringatan2")

                        if (!hasSuratPeringatan2 && !hasSuratPeringatan3) {
                            viewModel.checkSuratPeringatan(nasabah.no, 1, context) { hasSuratPeringatan1 ->
                                if (hasSuratPeringatan1) {
                                    binding.btnSP1.visibility = View.VISIBLE
                                    binding.btnSP02.visibility = View.VISIBLE
                                    binding.btnSP03.visibility = View.VISIBLE
                                }else {
                                    binding.btnSP01.visibility = View.VISIBLE
                                    binding.btnSP02.visibility = View.VISIBLE
                                    binding.btnSP03.visibility = View.VISIBLE
                                }
                                Log.d("NasabahAdapter", "Nasabah No: ${nasabah.no}, Tingkat 1, hasSuratPeringatan: $hasSuratPeringatan1")
                            }
                        }
                    }
                }
            }

            binding.btnSP1.setOnClickListener {
                Log.d("NasabahAdapter", "btnSP1 clicked for Nasabah No: ${nasabah.no}")
                viewModel.getSuratPeringatan(nasabah.no, 1, context)
                showSuratPeringatanDialog()
            }
            binding.btnSP2.setOnClickListener {
                Log.d("NasabahAdapter", "btnSP2 clicked for Nasabah No: ${nasabah.no}")
                viewModel.getSuratPeringatan(nasabah.no, 2, context)
                showSuratPeringatanDialog()
            }
            binding.btnSP3.setOnClickListener {
                Log.d("NasabahAdapter", "btnSP3 clicked for Nasabah No: ${nasabah.no}")
                viewModel.getSuratPeringatan(nasabah.no, 3, context)
                showSuratPeringatanDialog()
            }
        }

        private fun showSuratPeringatanDialog() {
            Log.d("NasabahAdapter", "Showing Surat Peringatan dialog")
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_surat_peringatan, null)
            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            viewModel.suratPeringatan.observe(lifecycleOwner, Observer { suratPeringatan ->
                Log.d("NasabahAdapter", "Observing Surat Peringatan changes")
                dialogView.findViewById<TextView>(R.id.tvTingkat).text = "Tingkat: ${suratPeringatan?.tingkat}"
                dialogView.findViewById<TextView>(R.id.tvTanggal).text = "Tanggal: ${suratPeringatan?.tanggal}"
                dialogView.findViewById<TextView>(R.id.tvKeterangan).text = "Keterangan: ${suratPeringatan?.keterangan}"

                val ivBuktiGambar = dialogView.findViewById<ImageView>(R.id.ivBuktiGambar)
                val pdfContainer = dialogView.findViewById<FrameLayout>(R.id.pdfContainer)

                suratPeringatan?.buktiGambar?.let { buktiGambar ->
                    loadGambar(buktiGambar, ivBuktiGambar)
                }

                suratPeringatan?.scanPdf?.let { pdfUrl ->
                    loadPdf(pdfUrl, pdfContainer)
                }
            })

            alertDialog.show()
        }

        private fun loadGambar(filename: String, imageView: ImageView) {
            Log.d("NasabahAdapter", "Loading gambar: $filename")
            val imageUrl = context.getString(R.string.api_server) + "/surat-peringatan/gambar/$filename"
            Glide.with(context)
                .load(imageUrl)
                .into(imageView)
        }

        private fun loadPdf(filename: String, pdfContainer: FrameLayout) {
            Log.d("NasabahAdapter", "Loading PDF: $filename")
            val pdfUrl = context.getString(R.string.api_server) + "/surat-peringatan/pdf/$filename"
            val pdfFile = File(context.cacheDir, "temp.pdf")

            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val response = downloadFile(pdfUrl)
                    response?.let {
                        pdfFile.writeBytes(it)
                        Log.d("NasabahAdapter", "PDF downloaded: $filename")

                        withContext(Dispatchers.Main) {
                            displayPdf(pdfFile, pdfContainer)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("NasabahAdapter", "Error loading PDF: ${e.message}", e)
                }
            }
        }

        private fun downloadFile(url: String): ByteArray? {
            Log.d("NasabahAdapter", "Downloading file from URL: $url")
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            return response.body?.bytes()
        }

        private fun displayPdf(pdfFile: File, pdfContainer: FrameLayout) {
            Log.d("NasabahAdapter", "Displaying PDF")
            val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
            val page = pdfRenderer.openPage(0)

            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            val imageView = ImageView(context)
            imageView.setImageBitmap(bitmap)
            pdfContainer.addView(imageView)

            page.close()
            pdfRenderer.close()
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
