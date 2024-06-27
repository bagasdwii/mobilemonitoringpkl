package com.example.mobilemonitoringbankbpr.data

data class Supervisor( val id: Int,
                       val name: String){
    override fun toString(): String {
        return name
    }
}
