package com.example.mobilemonitoringbankbpr.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Kunjungan

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

    class KunjunganViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tanggalTextView: TextView = itemView.findViewById(R.id.Kunjungan)

        fun bind(kunjungan: Kunjungan, onKunjunganClick: (Kunjungan) -> Unit) {
            tanggalTextView.text = kunjungan.tanggal
            itemView.setOnClickListener { onKunjunganClick(kunjungan) }
        }

    }
    fun updateData(newKunjunganList: List<Kunjungan>) {
        // Metode ini untuk memperbarui data dalam adapter
        kunjunganList = newKunjunganList
        notifyDataSetChanged() // Notifikasi adapter untuk memperbarui tampilan
    }
}
