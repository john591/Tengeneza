package com.example.tengeneza.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tengeneza.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    // Get the current Firebase user
    //private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        //val userEmail: String = currentUser?.email.toString()
        //val userID: String = currentUser?.uid.toString()

        //binding.userInfo.text = "Email: $userEmail  User ID: $userID"
        // Inflate the layout for this fragment
        return binding.root
    }
}