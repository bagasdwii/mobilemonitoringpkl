package com.example.mobilemonitoringbankbpr.data

data class UpdateUser( val name: String?,
                       val email: String?,
                       val jabatan: Int?= null,
                       val cabang: Int?= null,
                       val wilayah: Int?= null,
//                       val id_direksi: Int?= null,
//                       val id_kepala_cabang: Int?= null,
//                       val id_supervisor: Int?= null,
//                       val id_admin_kas: Int?= null,
                       val status: Int?= null)
