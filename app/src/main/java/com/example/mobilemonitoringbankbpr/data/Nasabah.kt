package com.example.mobilemonitoringbankbpr.data

data class Nasabah(val no: Long,
                   val nama: String,
                   val cabang: String,
                   val suratPeringatan: List<SuratPeringatan>)
