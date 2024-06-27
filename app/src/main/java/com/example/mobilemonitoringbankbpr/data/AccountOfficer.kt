package com.example.mobilemonitoringbankbpr.data

data class AccountOfficer( val id: Int,
                           val name: String){
    override fun toString(): String {
        return name
    }
}
