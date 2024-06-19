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
import com.example.mobilemonitoringbankbpr.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {
    private lateinit var binding:FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentRegisterBinding.inflate(inflater,container, false)

        binding.register.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.registerFragment, true)
                .build()
            // Navigate to MainFragment with NavOptions
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment, null, navOptions)



        }

        return binding.root
    }


}