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
import com.example.mobilemonitoringbankbpr.data.AdminKas
import com.example.mobilemonitoringbankbpr.data.Cabang
import com.example.mobilemonitoringbankbpr.data.Direksi
import com.example.mobilemonitoringbankbpr.data.KepalaCabang
import com.example.mobilemonitoringbankbpr.data.Supervisor
import com.example.mobilemonitoringbankbpr.data.UpdateUser
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.data.Wilayah
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
    private var loadingDialog: AlertDialog? = null
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
            tvCabang.text = if (user.cabang.isNullOrEmpty()) "Tidak diketahui" else user.cabang
            tvWilayah.text = if (user.wilayah.isNullOrEmpty()) "Tidak diketahui" else user.wilayah

//            tvDireksi.text = user.id_direksi
//            tvKepalaCabang.text = user.id_kepala_cabang
//            tvSupervisor.text = user.id_supervisor
//            tvAdminKas.text = user.id_admin_kas
            tvStatus.text = if (user.status.isNullOrEmpty()) "Tidak aktif" else user.status

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
                    Direksi.visibility = View.GONE
                    tvDireksi.visibility = View.GONE
                }
                "supervisor" -> {
                    Direksi.visibility = View.GONE
                    tvDireksi.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    tvSupervisor.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    tvAdminKas.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    tvKepalaCabang.visibility = View.GONE
                }
                "admin kas" -> {
                    Direksi.visibility = View.GONE
                    tvDireksi.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    tvKepalaCabang.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    tvAdminKas.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    tvSupervisor.visibility = View.GONE
                }
                "account officer" -> {
                    Direksi.visibility = View.GONE
                    tvDireksi.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    tvKepalaCabang.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    tvSupervisor.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    tvAdminKas.visibility = View.GONE
                }
            }

            alertDialog.show()
        }


        private fun showUserEditDialog(user: User) {
            Log.d("UserEditDialog", "Updating user with editJabatan: $user")

            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null)
            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            val tvUserName = dialogView.findViewById<TextView>(R.id.tvUserName)
            val tvGmail = dialogView.findViewById<TextView>(R.id.tvgmail)
            val Cabang = dialogView.findViewById<TextView>(R.id.Cabang)
            val editCabang = dialogView.findViewById<TextView>(R.id.editCabang)
            val Wilayah = dialogView.findViewById<TextView>(R.id.Wilayah)
            val editWilayah = dialogView.findViewById<TextView>(R.id.editWilayah)
            val editJabatan = dialogView.findViewById<TextView>(R.id.editJabatan)
            val Direksi = dialogView.findViewById<TextView>(R.id.Direksi)
            val editDireksi = dialogView.findViewById<TextView>(R.id.editDireksi)
            val KepalaCabang = dialogView.findViewById<TextView>(R.id.KepalaCabang)
            val editKepalaCabang = dialogView.findViewById<TextView>(R.id.editKepalaCabang)
            val Supervisor = dialogView.findViewById<TextView>(R.id.Supervisor)
            val editSupervisor = dialogView.findViewById<TextView>(R.id.editSupervisor)
            val AdminKas = dialogView.findViewById<TextView>(R.id.AdminKas)
            val editAdminKas = dialogView.findViewById<TextView>(R.id.editAdminKas)
            val editStatus = dialogView.findViewById<TextView>(R.id.editStatus)

            tvUserName.text = user.name
            tvGmail.text = user.email

            // Variables to store IDs
            var selectedCabangId: Int? = user.cabang?.toIntOrNull()
            var selectedWilayahId: Int? = user.wilayah?.toIntOrNull()
            var selectedJabatanId: Int? = user.jabatan?.toIntOrNull()
//            var selectedDireksiId: Int? = user.id_direksi?.toIntOrNull()
//            var selectedKepalaCabangId: Int? = user.id_kepala_cabang?.toIntOrNull()
//            var selectedSupervisorId: Int? = user.id_supervisor?.toIntOrNull()
//            var selectedAdminKasId: Int? = user.id_admin_kas?.toIntOrNull()
            var selectedStatusId: Int? = user.status?.toIntOrNull()

            // Set initial user data
            editCabang.text = user.cabang
            editWilayah.text = user.wilayah
            editJabatan.text = user.jabatan
//            editDireksi.text = user.id_direksi
//            editKepalaCabang.text = user.id_kepala_cabang
//            editSupervisor.text = user.id_supervisor
//            editAdminKas.text = user.id_admin_kas
            editStatus.text = user.status
            when (user.jabatan.toLowerCase()) {
                "direksi" -> {
                    Cabang.visibility = View.GONE
                    editCabang.visibility = View.GONE
                    Wilayah.visibility = View.GONE
                    editWilayah.visibility = View.GONE
                    Direksi.visibility = View.GONE
                    editDireksi.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    editKepalaCabang.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    editSupervisor.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    editAdminKas.visibility = View.GONE
                }
                "kepala cabang" -> {
                    Wilayah.visibility = View.GONE
                    editWilayah.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    editKepalaCabang.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    editSupervisor.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    editAdminKas.visibility = View.GONE
                    Direksi.visibility = View.GONE
                    editDireksi.visibility = View.GONE
                }
                "supervisor" -> {
                    Direksi.visibility = View.GONE
                    editDireksi.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    editSupervisor.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    editAdminKas.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    editKepalaCabang.visibility = View.GONE
                }
                "admin kas" -> {
                    Direksi.visibility = View.GONE
                    editDireksi.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    editKepalaCabang.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    editAdminKas.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    editSupervisor.visibility = View.GONE
                }
                "account officer" -> {
                    Direksi.visibility = View.GONE
                    editDireksi.visibility = View.GONE
                    KepalaCabang.visibility = View.GONE
                    editKepalaCabang.visibility = View.GONE
                    Supervisor.visibility = View.GONE
                    editSupervisor.visibility = View.GONE
                    AdminKas.visibility = View.GONE
                    editAdminKas.visibility = View.GONE
                }
            }

            viewModel.allData.observe(lifecycleOwner, { allDataResponse ->
                editCabang.setOnClickListener {
                    val cabangMap = allDataResponse.cabang.associate { it.nama_cabang to it.id_cabang }
                    showDialog(ArrayList(cabangMap.keys), editCabang) { selectedName ->
                        editCabang.text = selectedName
                        selectedCabangId = cabangMap[selectedName]
                    }
                }
                editWilayah.setOnClickListener {
                    val wilayahMap = allDataResponse.wilayah.associate { it.nama_wilayah to it.id_wilayah }
                    showDialog(ArrayList(wilayahMap.keys), editWilayah) { selectedName ->
                        editWilayah.text = selectedName
                        selectedWilayahId = wilayahMap[selectedName]
                    }
                }
                editJabatan.setOnClickListener {
                    val jabatanMap = allDataResponse.jabatan.associate { it.nama_jabatan to it.id_jabatan }
                    showDialog(ArrayList(jabatanMap.keys), editJabatan) { selectedName ->
                        editJabatan.text = selectedName
                        selectedJabatanId = jabatanMap[selectedName]
                    }
                }
//                editDireksi.setOnClickListener {
//                    val direksiMap = allDataResponse.direksi.associate { it.nama to it.id_direksi }
//                    showDialog(ArrayList(direksiMap.keys), editDireksi) { selectedName ->
//                        editDireksi.text = selectedName
//                        selectedDireksiId = direksiMap[selectedName]
//                    }
//                }
//                editKepalaCabang.setOnClickListener {
//                    val kepalaCabangMap = allDataResponse.kepala_cabang.associate { it.nama_kepala_cabang to it.id_kepala_cabang }
//                    showDialog(ArrayList(kepalaCabangMap.keys), editKepalaCabang) { selectedName ->
//                        editKepalaCabang.text = selectedName
//                        selectedKepalaCabangId = kepalaCabangMap[selectedName]
//                    }
//                }
//                editSupervisor.setOnClickListener {
//                    val supervisorMap = allDataResponse.supervisor.associate { it.nama_supervisor to it.id_supervisor }
//                    showDialog(ArrayList(supervisorMap.keys), editSupervisor) { selectedName ->
//                        editSupervisor.text = selectedName
//                        selectedSupervisorId = supervisorMap[selectedName]
//                    }
//                }
//                editAdminKas.setOnClickListener {
//                    val adminKasMap = allDataResponse.admin_kas.associate { it.nama_admin_kas to it.id_admin_kas }
//                    showDialog(ArrayList(adminKasMap.keys), editAdminKas) { selectedName ->
//                        editAdminKas.text = selectedName
//                        selectedAdminKasId = adminKasMap[selectedName]
//                    }
//                }
                editStatus.setOnClickListener {
                    val statusMap = allDataResponse.status.associate { it.nama_status to it.id }
                    showDialog(ArrayList(statusMap.keys), editStatus) { selectedName ->
                        editStatus.text = selectedName
                        selectedStatusId = statusMap[selectedName]
                    }
                }
            })

            dialogView.findViewById<Button>(R.id.saveButton).setOnClickListener {
                // Ensure all required fields are set
                val updatedUser = UpdateUser(
                    name = user.name,
                    email = user.email,
                    jabatan = selectedJabatanId ?: user.id_jabatan,
                    cabang = selectedCabangId ?: user.id_cabang,
                    wilayah = selectedWilayahId ?: user.id_wilayah,
//                    id_direksi = selectedDireksiId ?: user.direksi_id,
//                    id_kepala_cabang = selectedKepalaCabangId ?: user.kepalacabang_id,
//                    id_supervisor = selectedSupervisorId ?: user.supervisor_id,
//                    id_admin_kas = selectedAdminKasId ?: user.adminkas_id,
                    status = selectedStatusId ?: user.status_id
                )
                Log.d("UserEditDialog", "Updating user with data: $updatedUser")
                showConfirmationDialog { confirmed ->
                    if (confirmed) {
                        submitUpdateUser(user.id, updatedUser)
                        alertDialog.dismiss()

                    }
                }

            }

            alertDialog.show()
        }
        private fun submitUpdateUser(userId: Int, updatedUser: UpdateUser) {
            viewModel.updateUser(userId, updatedUser)
        }
        private fun showConfirmationDialog(onConfirmed: (Boolean) -> Unit) {
            val alertDialog = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.custom_dialog, null)

            val title = dialogView.findViewById<TextView>(R.id.alertTitle)
            val alertMessage = dialogView.findViewById<TextView>(R.id.alertMessage)

            title.text = "Peringatan"
            alertMessage.text = "Apakah Anda yakin ingin mengirimkan data pengguna ini?"

            alertDialog.setView(dialogView)
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "IYA") { dialog, _ ->
                onConfirmed(true)
                dialog.dismiss()
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "TIDAK") { dialog, _ ->
                onConfirmed(false)
                dialog.dismiss()
            }
            alertDialog.show()
        }


        private fun showDialog(arrayList: ArrayList<String>, editTextView: TextView, onItemSelected: (String) -> Unit) {
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
                val selectedName = adapter.getItem(position)
                editTextView.text = selectedName
                onItemSelected(selectedName!!)
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