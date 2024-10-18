package com.example.mobilemonitoringbankbpr.data

data class Kunjungan(
    val id: Int?=null,
    val nama: String?=null,
    val tanggal:  String,
    val no: Long? = null,
    val keterangan: String? = null,
    val koordinat: String? = null,
    val bukti_gambar: String? = null
)

