package com.example.mobilemonitoringbankbpr.data

data class Direksi(
    val id: Int,
    val name: String){
    override fun toString(): String {
        return name
    }
}
