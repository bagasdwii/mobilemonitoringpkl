package com.example.mobilemonitoringbankbpr.paging

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import org.json.JSONObject

class NasabahPagingSource(
    private val context: Context,
    private val query: String
) : PagingSource<Int, Nasabah>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Nasabah> {
        val page = params.key ?: 1
        return try {
            val url = "${context.getString(R.string.api_server)}/nasabahs?page=$page&search=$query"
            val http = Http(context, url)
            http.setMethod("GET")
            http.setToken(true)
            http.send()

            if (http.getStatusCode() == 200) {
                val response = JSONObject(http.getResponse()!!)
                val nasabahsArray = response.getJSONArray("data")
                val nasabahsList = mutableListOf<Nasabah>()

                for (i in 0 until nasabahsArray.length()) {
                    val nasabahJson = nasabahsArray.getJSONObject(i)
                    val suratPeringatanJson = nasabahJson.optJSONObject("surat_peringatan")

                    val nasabah = Nasabah(
                        no = nasabahJson.getLong("no"),
                        nama = nasabahJson.getString("nama"),
                        cabang = nasabahJson.getString("nama_cabang"),
                        suratPeringatan = suratPeringatanJson?.let {
                            SuratPeringatan(
                                no = it.getLong("no"),
                                tingkat = it.getInt("tingkat"),
                                tanggal = it.getString("tanggal"),
                                keterangan = it.getString("keterangan"),
                                bukti_gambar = it.getString("bukti_gambar"),
                                scan_pdf = it.getString("scan_pdf"),
                                id_account_officer = it.getLong("id_account_officer")
                            )
                        }
                    )
                    nasabahsList.add(nasabah)
                }

                LoadResult.Page(
                    data = nasabahsList,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (nasabahsArray.length() < 15) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("Error fetching data"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Nasabah>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
