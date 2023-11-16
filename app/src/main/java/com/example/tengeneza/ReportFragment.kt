package com.example.tengeneza

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.tengeneza.databinding.FragmentReportBinding
import com.example.tengeneza.databinding.FragmentWelcomeBinding

class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false)




        return binding.root
    }
}