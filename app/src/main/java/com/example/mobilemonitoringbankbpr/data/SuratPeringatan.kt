package com.example.mobilemonitoringbankbpr.data

data class SuratPeringatan(
    val no: Long,
    val tingkat: Int?,
    val tanggal: String,
    val keterangan: String,
    val buktiGambar: String? = null,  // Nilai default null
    val scanPdf: String? = null,       // Nilai default null
    val idAccountOfficer: Long
)
