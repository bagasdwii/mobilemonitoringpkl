package com.example.mobilemonitoringbankbpr.data

data class Cabang(
    val id: Int,
    val name: String) {
    override fun toString(): String {
        return name
    }
}