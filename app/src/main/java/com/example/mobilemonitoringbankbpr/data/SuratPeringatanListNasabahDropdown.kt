package com.example.mobilemonitoringbankbpr.data

data class SuratPeringatanListNasabahDropdown(
    val no: Long,
    val nama: String,
    val kategori: String,
    val tingkat: Int, // Tambahkan tingkat
    val id_account_officer: Long
)
