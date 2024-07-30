package com.example.mobilemonitoringbankbpr.adapter

import java.text.NumberFormat
import java.util.Locale
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.databinding.ItemNasabahBinding
import com.example.mobilemonitoringbankbpr.viewmodel.MonitoringViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import com.bumptech.glide.request.target.Target

import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.databinding.ItemUserBinding
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import com.example.mobilemonitoringbankbpr.viewmodel.AdminViewModel
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
class AdminAdapter(
    private val viewModel: AdminViewModel,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val coroutineScope: CoroutineScope
) : ListAdapter<User, AdminAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, viewModel, context, lifecycleOwner, coroutineScope)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class UserViewHolder(
        private val binding: ItemUserBinding,
        private val viewModel: AdminViewModel,
        private val context: Context,
        private val lifecycleOwner: LifecycleOwner,
        private val coroutineScope: CoroutineScope
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.NamaUser.text = user.name
            binding.JabatanUser.text = user.jabatan
            binding.detailUser.setOnClickListener {
                showUserDialog(user)
            }
            binding.btnEdit.setOnClickListener {
                showUserEditDialog(user)
            }
        }

        private fun showUserDialog(user: User) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_detail_user, null)
            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            dialogView.findViewById<TextView>(R.id.tvUserName).text = user.name
            dialogView.findViewById<TextView>(R.id.tvgmail).text = user.email
            dialogView.findViewById<TextView>(R.id.tvjabatannn).text = user.jabatan
            dialogView.findViewById<TextView>(R.id.tvCabang).text = user.cabang
            dialogView.findViewById<TextView>(R.id.tvWilayah).text = user.wilayah
            dialogView.findViewById<TextView>(R.id.tvDireksi).text = user.id_direksi
            dialogView.findViewById<TextView>(R.id.tvKepalaCabang).text = user.id_kepala_cabang
            dialogView.findViewById<TextView>(R.id.tvSupervisor).text = user.id_supervisor
            dialogView.findViewById<TextView>(R.id.tvAdminKas).text = user.id_admin_kas
            dialogView.findViewById<TextView>(R.id.tvStatus).text = user.status

            alertDialog.show()
        }

        private fun showUserEditDialog(user: User) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null)
            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            dialogView.findViewById<TextView>(R.id.editJabatan).text = user.jabatan
            dialogView.findViewById<TextView>(R.id.editCabang).text = user.cabang
            dialogView.findViewById<TextView>(R.id.editWilayah).text = user.wilayah
            dialogView.findViewById<TextView>(R.id.editDireksi).text = user.id_direksi
            dialogView.findViewById<TextView>(R.id.editKepalaCabang).text = user.id_kepala_cabang
            dialogView.findViewById<TextView>(R.id.editSupervisor).text = user.id_supervisor
            dialogView.findViewById<TextView>(R.id.editAdminKas).text = user.id_admin_kas
            dialogView.findViewById<TextView>(R.id.editStatus).text = user.status

            alertDialog.show()
        }
    }


    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id_user == newItem.id_user
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
