package com.example.mobilemonitoringbankbpr.data

data class Nasabah(val no: Long,
                   val nama: String,
                   val pokok: String,
                   val bunga: String,
                   val denda: String,
                   val total: Int,
                   val keterangan: String,
                   val ttd: String,
                   val kembali: String,
                   val cabang: String,
                   val wilayah: String,
                   val adminkas: String,
                   val accountOfficer: String,
                   val suratPeringatan: List<SuratPeringatan>)
