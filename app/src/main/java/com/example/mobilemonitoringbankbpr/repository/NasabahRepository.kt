package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.paging.NasabahPagingSource
import kotlinx.coroutines.flow.Flow

class NasabahRepository(private val context: Context) {

//    fun getNasabahsStream(query: String): Flow<PagingData<Nasabah>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 15,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = { NasabahPagingSource(context, query) }
//        ).flow
//    }
}
