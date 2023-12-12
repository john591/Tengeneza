package com.example.tengeneza.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.tengeneza.R
import com.example.tengeneza.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val userInfoSharePref = requireContext().getSharedPreferences("UserInfoSharePref", Context.MODE_PRIVATE)
        val getEmail = userInfoSharePref.getString("Email", "")
        val getUserID = userInfoSharePref.getString("ID","")

        if (getEmail != "" && getUserID != ""){
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        binding.goToLogin.setOnClickListener {
            it.findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }

        binding.goToSignup.setOnClickListener {
            it.findNavController().navigate(R.id.action_welcomeFragment_to_signupFragment)
        }


        return binding.root
    }
}