package com.example.mobilemonitoringbankbpr.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Kunjungan
import java.text.SimpleDateFormat
import java.util.Locale

class KunjunganAdapter(
    private var kunjunganList: List<Kunjungan>,
    private val onKunjunganClick: (Kunjungan) -> Unit
) : RecyclerView.Adapter<KunjunganAdapter.KunjunganViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KunjunganViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kunjungan, parent, false)
        return KunjunganViewHolder(view)
    }

    override fun onBindViewHolder(holder: KunjunganViewHolder, position: Int) {
        val kunjungan = kunjunganList[position]
        holder.bind(kunjungan, onKunjunganClick)
    }

    override fun getItemCount() = kunjunganList.size

    // Use DiffUtil to update data efficiently
    fun updateData(newList: List<Kunjungan>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = kunjunganList.size
            override fun getNewListSize() = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return kunjunganList[oldItemPosition].id == newList[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return kunjunganList[oldItemPosition] == newList[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        kunjunganList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class KunjunganViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tanggalTextView: TextView = itemView.findViewById(R.id.Kunjungan)

        fun bind(kunjungan: Kunjungan, onKunjunganClick: (Kunjungan) -> Unit) {
            tanggalTextView.text = formatTanggal(kunjungan.tanggal)

            itemView.setOnClickListener { onKunjunganClick(kunjungan) }
        }
        fun formatTanggal(tanggalServer: String): String {
            // Format tanggal dari server (contoh: "2024-10-17 14:59:00")
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            // Format yang diinginkan (contoh: "17-10-2024 14:59:00")
            val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

            return try {
                // Mengonversi tanggal dari format server ke format yang diinginkan
                val date = inputFormat.parse(tanggalServer)
                date?.let { outputFormat.format(it) } ?: "Tanggal Tidak Valid"
            } catch (e: Exception) {
                // Log kesalahan jika terjadi masalah parsing
                Log.e("FormatTanggal", "Error parsing tanggal: ${e.message}")
                "Tanggal Tidak Valid"
            }
        }
    }


//    fun updateData(newKunjunganList: List<Kunjungan>) {
//        // Metode ini untuk memperbarui data dalam adapter
//        kunjunganList = newKunjunganList
//        notifyDataSetChanged() // Notifikasi adapter untuk memperbarui tampilan
//    }
}
