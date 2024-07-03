package com.example.mobilemonitoringbankbpr

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtilsNasabah {
    fun getFileFromUri(context: Context, uri: Uri): File? {
        val documentFile = DocumentFile.fromSingleUri(context, uri) ?: return null
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, documentFile.name ?: "tempFile")

        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }
}