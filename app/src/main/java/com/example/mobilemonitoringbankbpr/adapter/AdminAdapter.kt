package com.example.mobilemonitoringbankbpr.adapter

import android.app.Dialog
import java.text.NumberFormat
import java.util.Locale
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
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
import com.example.mobilemonitoringbankbpr.databinding.DialogSeacrhSpinnerBinding
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

            val editCabang = dialogView.findViewById<TextView>(R.id.editCabang)
            val editWilayah = dialogView.findViewById<TextView>(R.id.editWilayah)
            val editJabatan = dialogView.findViewById<TextView>(R.id.editJabatan)
            val editDireksi = dialogView.findViewById<TextView>(R.id.editDireksi)
            val editKepalaCabang = dialogView.findViewById<TextView>(R.id.editKepalaCabang)
            val editSupervisor = dialogView.findViewById<TextView>(R.id.editSupervisor)
            val editAdminKas = dialogView.findViewById<TextView>(R.id.editAdminKas)
            val editStatus = dialogView.findViewById<TextView>(R.id.editStatus)

            // Set initial user data
            editCabang.text = user.cabang
            editWilayah.text = user.wilayah
            editJabatan.text = user.jabatan
            editDireksi.text = user.id_direksi
            editKepalaCabang.text = user.id_kepala_cabang
            editSupervisor.text = user.id_supervisor
            editAdminKas.text = user.id_admin_kas
            editStatus.text = user.status


            // Observe cabang data and show dialog when editCabang is clicked
            editCabang.setOnClickListener {
                viewModel.cabang.observe(lifecycleOwner, { cabangList ->
                    val arrayList = ArrayList(cabangList.map { it.nama_cabang })
                    showDialog(arrayList, editCabang)
                })
            }
            editWilayah.setOnClickListener {
                viewModel.wilayah.observe(lifecycleOwner, { wilayahList ->
                    val arrayList = ArrayList(wilayahList.map { it.nama_wilayah })
                    showDialog(arrayList, editWilayah)
                })
            }
            editJabatan.setOnClickListener {
                viewModel.jabatan.observe(lifecycleOwner, { jabatanList ->
                    val arrayList = ArrayList(jabatanList.map { it.nama_jabatan })
                    showDialog(arrayList, editJabatan)
                })
            }
            editDireksi.setOnClickListener {
                viewModel.direksi.observe(lifecycleOwner, { direksiList ->
                    val arrayList = ArrayList(direksiList.map { it.nama })
                    showDialog(arrayList, editDireksi)
                })
            }
            editKepalaCabang.setOnClickListener {
                viewModel.kepalacabang.observe(lifecycleOwner, { kepalacabangList ->
                    val arrayList = ArrayList(kepalacabangList.map { it.nama_kepala_cabang })
                    showDialog(arrayList, editKepalaCabang)
                })
            }
            editSupervisor.setOnClickListener {
                viewModel.supervisor.observe(lifecycleOwner, { supervisorList ->
                    val arrayList = ArrayList(supervisorList.map { it.nama_supervisor })
                    showDialog(arrayList, editSupervisor)
                })
            }
            editAdminKas.setOnClickListener {
                viewModel.adminkas.observe(lifecycleOwner, { adminkasList ->
                    val arrayList = ArrayList(adminkasList.map { it.nama_admin_kas })
                    showDialog(arrayList, editAdminKas)
                })
            }
//            editStatus.setOnClickListener {
//                viewModel.status.observe(lifecycleOwner, { statusList ->
//                    val arrayList = ArrayList(statusList.map { it.nama_status })
//                    showDialog(arrayList, editStatus)
//                })
//            }





            alertDialog.show()
        }
        private fun showDialog(arrayList: ArrayList<String>, editTextView: TextView) {
            val dialogBinding = DialogSeacrhSpinnerBinding.inflate(LayoutInflater.from(context))
            val dialog = Dialog(context).apply {
                setContentView(dialogBinding.root)
                window?.setLayout(900, 2000)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()
            }

            val adapter = ArrayAdapter(context, R.layout.spinner_item_surat, arrayList)

            dialogBinding.listView.adapter = adapter

            dialogBinding.editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            dialogBinding.listView.setOnItemClickListener { _, _, position, _ ->
                editTextView.text = adapter.getItem(position)
                dialog.dismiss()
            }
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
