package com.example.mobilemonitoringbankbpr.data

data class AllDataResponse(
    val jabatan: List<Jabatan>,
    val cabang: List<Cabang>,
    val kantorkas: List<KantorKas>,
    val direksi: List<Direksi>,
    val kepala_cabang: List<KepalaCabang>,
    val supervisor: List<Supervisor>,
    val admin_kas: List<AdminKas>,
    val status: List<Status>,
)
