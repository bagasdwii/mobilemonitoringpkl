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

//        private fun showUserDialog(user: User) {
//            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_detail_user, null)
//            val alertDialog = AlertDialog.Builder(context)
//                .setView(dialogView)
//                .setCancelable(true)
//                .create()
//
//            dialogView.findViewById<TextView>(R.id.tvUserName).text = user.name
//            dialogView.findViewById<TextView>(R.id.tvgmail).text = user.email
//            dialogView.findViewById<TextView>(R.id.tvjabatannn).text = user.jabatan
//            dialogView.findViewById<TextView>(R.id.tvCabang).text = user.cabang
//            dialogView.findViewById<TextView>(R.id.tvWilayah).text = user.wilayah
//            dialogView.findViewById<TextView>(R.id.tvDireksi).text = user.id_direksi
//            dialogView.findViewById<TextView>(R.id.tvKepalaCabang).text = user.id_kepala_cabang
//            dialogView.findViewById<TextView>(R.id.tvSupervisor).text = user.id_supervisor
//            dialogView.findViewById<TextView>(R.id.tvAdminKas).text = user.id_admin_kas
//            dialogView.findViewById<TextView>(R.id.tvStatus).text = user.status
//
//            alertDialog.show()
//        }
        private fun showUserDialog(user: User) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_detail_user, null)
            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            val tvUserName = dialogView.findViewById<TextView>(R.id.tvUserName)
            val tvGmail = dialogView.findViewById<TextView>(R.id.tvgmail)
            val tvJabatan = dialogView.findViewById<TextView>(R.id.tvjabatannn)
            val Cabang = dialogView.findViewById<TextView>(R.id.Cabang)
            val tvCabang = dialogView.findViewById<TextView>(R.id.tvCabang)
            val Wilayah = dialogView.findViewById<TextView>(R.id.Wilayah)
            val tvWilayah = dialogView.findViewById<TextView>(R.id.tvWilayah)
            val Direksi = dialogView.findViewById<TextView>(R.id.Direksi)
            val tvDireksi = dialogView.findViewById<TextView>(R.id.tvDireksi)
            val KepalaCabang = dialogView.findViewById<TextView>(R.id.KepalaCabang)
            val tvKepalaCabang = dialogView.findViewById<TextView>(R.id.tvKepalaCabang)
            val Supervisor = dialogView.findViewById<TextView>(R.id.Supervisor)
            val tvSupervisor = dialogView.findViewById<TextView>(R.id.tvSupervisor)
            val AdminKas = dialogView.findViewById<TextView>(R.id.AdminKas)
            val tvAdminKas = dialogView.findViewById<TextView>(R.id.tvAdminKas)
            val tvStatus = dialogView.findViewById<TextView>(R.id.tvStatus)

            tvUserName.text = user.name
            tvGmail.text = user.email
            tvJabatan.text = user.jabatan
            tvCabang.text = user.cabang
            tvWilayah.text = user.wilayah
            tvDireksi.text = user.id_direksi
            tvKepalaCabang.text = user.id_kepala_cabang
            tvSupervisor.text = user.id_supervisor
            tvAdminKas.text = user.id_admin_kas
            tvStatus.text = user.status

            when (user.jabatan.toLowerCase()) {
                "direksi" -> {
                    Cabang.visibility = View.GONE
                    tvCabang.visibility = View.GONE
                    Wilayah.visibility = View.GONE
                    tvWilayah.visibility = View.GONE
                    Direksi.visibility = View.GONE
                    tvDireksi.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    tvKepalaCabang.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    tvSupervisor.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    tvAdminKas.visibility = View.GONE
                }
                "kepala cabang" -> {
                    Wilayah.visibility = View.GONE
                    tvWilayah.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    tvKepalaCabang.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    tvSupervisor.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    tvAdminKas.visibility = View.GONE
                }
                "supervisor" -> {
                    Direksi.visibility = View.GONE
                    tvDireksi.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    tvSupervisor.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    tvAdminKas.visibility = View.GONE
                }
                "admin kas" -> {
                    Direksi.visibility = View.GONE
                    tvDireksi.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    tvKepalaCabang.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    tvAdminKas.visibility = View.GONE
                }
                "account officer" -> {
                    Direksi.visibility = View.GONE
                    tvDireksi.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    tvKepalaCabang.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    tvSupervisor.visibility = View.GONE
                }
            }

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
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
