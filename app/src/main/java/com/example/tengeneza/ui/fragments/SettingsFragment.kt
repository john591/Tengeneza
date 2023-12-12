package com.example.tengeneza.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tengeneza.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val userInfoSharePref = requireContext().getSharedPreferences("UserInfoSharePref", Context.MODE_PRIVATE)
        val getEmail = userInfoSharePref.getString("Email", "")
        val getUserID = userInfoSharePref.getString("ID","")
        binding.userInfo.text = "Email: $getEmail  User ID: $getUserID"
        // Inflate the layout for this fragment
        return binding.root
    }
}