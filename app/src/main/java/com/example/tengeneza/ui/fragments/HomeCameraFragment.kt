package com.example.tengeneza.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.tengeneza.R
import com.example.tengeneza.databinding.FragmentHomeCameraBinding

//It is like a welcome page for the HomeActivity
class HomeCameraFragment : Fragment() {

    private lateinit var binding: FragmentHomeCameraBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeCameraBinding.inflate(inflater, container, false)
        val btn_signaler = binding.buttonSignaler
        btn_signaler.setOnClickListener{
                it.findNavController().navigate(R.id.action_homeCameraFragment_to_reportFragment)
        }

        // onClickListener here
        return binding.root
    }
}