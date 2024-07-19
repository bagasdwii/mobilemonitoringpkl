package com.example.mobilemonitoringbankbpr.paging

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import org.json.JSONObject

class NasabahPagingSource{
//    (
//    private val context: Context,
//    private val query: String
//) : PagingSource<Int, Nasabah>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Nasabah> {
//        val page = params.key ?: 1
//        return try {
//            val url = "${context.getString(R.string.api_server)}/nasabahs?page=$page&search=$query"
//            val http = Http(context, url)
//            http.setMethod("GET")
//            http.setToken(true)
//            http.send()
//
//            if (http.getStatusCode() == 200) {
//                val response = JSONObject(http.getResponse()!!)
//                val nasabahsArray = response.getJSONArray("data")
//                val nasabahsList = mutableListOf<Nasabah>()
//
//                for (i in 0 until nasabahsArray.length()) {
//                    val nasabahJson = nasabahsArray.getJSONObject(i)
//                    val suratPeringatanArray = nasabahJson.optJSONArray("surat_peringatan")
//                    val suratPeringatanList = mutableListOf<SuratPeringatan>()
//
//                    suratPeringatanArray?.let {
//                        for (j in 0 until it.length()) {
//                            val spJson = it.getJSONObject(j)
//                            val suratPeringatan = SuratPeringatan(
//                                no = spJson.getLong("no"),
//                                tingkat = spJson.getInt("tingkat"),
//                                tanggal = spJson.getString("tanggal"),
//                                keterangan = spJson.getString("keterangan"),
//                                bukti_gambar = spJson.optString("bukti_gambar"),
//                                scan_pdf = spJson.optString("scan_pdf"),
//                                id_account_officer = spJson.getLong("id_account_officer")
//                            )
//                            suratPeringatanList.add(suratPeringatan)
//                        }
//                    }
//
//                    val nasabah = Nasabah(
//                        no = nasabahJson.getLong("no"),
//                        nama = nasabahJson.getString("nama"),
//                        pokok = nasabahJson.getString("pokok"),
//                        bunga = nasabahJson.getString("bunga"),
//                        denda = nasabahJson.getString("denda"),
//                        total = nasabahJson.getInt("total"),
//                        keterangan = nasabahJson.getString("keterangan"),
//                        ttd = nasabahJson.getString("ttd"),
//                        kembali = nasabahJson.getString("kembali"),
//                        cabang = nasabahJson.getString("nama_cabang"),
//                        wilayah = nasabahJson.getString("nama_wilayah"),
//                        adminkas = nasabahJson.getString("adminKas"),
//                        accountOfficer = nasabahJson.getString("accountOfficer"),
//                        suratPeringatan = suratPeringatanList
//                    )
//                    nasabahsList.add(nasabah)
//                }
//
//                LoadResult.Page(
//                    data = nasabahsList,
//                    prevKey = if (page == 1) null else page - 1,
//                    nextKey = if (nasabahsArray.length() < 15) null else page + 1
//                )
//            } else {
//                LoadResult.Error(Exception("Error fetching data"))
//            }
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Nasabah>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        }
//    }
}

