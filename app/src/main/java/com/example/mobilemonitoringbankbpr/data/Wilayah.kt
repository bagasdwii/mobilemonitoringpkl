package com.example.mobilemonitoringbankbpr.data

data class Wilayah(
    val id: Int,
    val name: String){
    override fun toString(): String {
        return name
    }
}
