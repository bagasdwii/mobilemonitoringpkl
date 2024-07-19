package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import android.util.Log
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.server.ApiService
import org.json.JSONObject
import java.net.URLEncoder

class MonitoringRepository(private val context: Context) {

    suspend fun getNasabahs(searchQuery: String, page: Int): Result<List<Nasabah>> {
        return try {
            val url = context.getString(R.string.api_server) + "/nasabahs?search=" + URLEncoder.encode(searchQuery, "UTF-8") + "&page=$page"
            val http = Http(context, url)
            http.setMethod("GET")
            http.setToken(true)
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                val response = JSONObject(http.getResponse()!!)
                val nasabahsList = mutableListOf<Nasabah>()

                val nasabahsArray = response.getJSONArray("data")
                for (i in 0 until nasabahsArray.length()) {
                    val nasabahJson = nasabahsArray.getJSONObject(i)
                    val suratPeringatanArray = nasabahJson.optJSONArray("surat_peringatan")

                    val suratPeringatanList = mutableListOf<SuratPeringatan>()
                    suratPeringatanArray?.let {
                        for (j in 0 until it.length()) {
                            val suratPeringatanJson = it.getJSONObject(j)
                            val suratPeringatan = SuratPeringatan(
                                no = suratPeringatanJson.getLong("no"),
                                tingkat = suratPeringatanJson.getInt("tingkat"),
                                tanggal = suratPeringatanJson.getString("tanggal"),
                                keterangan = suratPeringatanJson.getString("keterangan"),
                                bukti_gambar = suratPeringatanJson.getString("bukti_gambar"),
                                scan_pdf = suratPeringatanJson.getString("scan_pdf"),
                                id_account_officer = suratPeringatanJson.getLong("id_account_officer")
                            )
                            suratPeringatanList.add(suratPeringatan)
                        }
                    }

                    val nasabah = Nasabah(
                        no = nasabahJson.getLong("no"),
                        nama = nasabahJson.getString("nama"),
                        pokok = nasabahJson.getString("pokok"),
                        bunga = nasabahJson.getString("bunga"),
                        denda = nasabahJson.getString("denda"),
                        total = nasabahJson.getInt("total"),
                        keterangan = nasabahJson.getString("keterangan"),
                        ttd = nasabahJson.getString("ttd"),
                        kembali = nasabahJson.getString("kembali"),
                        cabang = nasabahJson.getString("nama_cabang"),
                        wilayah = nasabahJson.getString("nama_wilayah"),
                        adminkas = nasabahJson.getString("adminKas"),
                        accountOfficer = nasabahJson.getString("accountOfficer"),
                        suratPeringatan = suratPeringatanList
                    )
                    nasabahsList.add(nasabah)
                }
                Result.success(nasabahsList)
            } else {
                Result.failure(Exception("Error fetching nasabahs, status code: $code"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


