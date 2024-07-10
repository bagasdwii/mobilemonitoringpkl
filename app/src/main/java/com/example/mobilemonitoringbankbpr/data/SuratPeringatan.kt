package com.example.mobilemonitoringbankbpr.data

data class SuratPeringatan(
    val no: Long,
    val tingkat: Int?,
    val tanggal: String,
    val keterangan: String,
    val bukti_gambar: String? = null,  // Nilai default null
    val scan_pdf: String? = null,       // Nilai default null
    val id_account_officer: Long
)
