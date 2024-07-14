package com.example.mobilemonitoringbankbpr

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object FileUtilsNasabah {
    fun getFileFromUri(context: Context, uri: Uri): File? {
        val documentFile = DocumentFile.fromSingleUri(context, uri) ?: return null
        val tempFile = File(context.cacheDir, documentFile.name ?: "tempFile.jpg")

        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}