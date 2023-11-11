package com.example.tengeneza

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // onClickListener here
        return binding.root
    }
}