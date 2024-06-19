package com.example.mobilemonitoringbankbpr.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container, false)

        binding.login.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build()
            // Navigate to MainFragment with NavOptions
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment, null, navOptions)
//            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
//            findNavController().popBackStack(R.id.loginFragment, true)
        }
        binding.btnRegis.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root

    }


}