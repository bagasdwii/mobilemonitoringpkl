package com.example.mobilemonitoringbankbpr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.databinding.FragmentMainBinding

import com.example.mobilemonitoringbankbpr.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // Mengambil data jabatan terlebih dahulu
        mainViewModel.getJabatan(requireContext())

        // Mengamati perubahan pada jabatanLoaded
        mainViewModel.jabatanLoaded.observe(viewLifecycleOwner, { loaded ->
            if (loaded) {
                // Setelah data jabatan diambil, kita bisa mengambil data user
                mainViewModel.getUser(requireContext())
            }
        })

        mainViewModel.user.observe(viewLifecycleOwner, { user ->
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.tvJabatan.text = user.jabatan
        })

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val url = getString(R.string.api_server) + "/logoutmobile"
        Thread {
            val http = Http(requireContext(), url)
            http.setMethod("POST")
            http.setToken(true)
            http.send()

            requireActivity().runOnUiThread {
                val code = http.getStatusCode()
                if (code == 200) {
                    val localStorage = LocalStorage(requireContext())
                    localStorage.token = null
                    findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                } else {
                    Toast.makeText(requireContext(), "Error $code", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



