package com.example.tengeneza.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tengeneza.R
import com.example.tengeneza.databinding.FragmentInspectBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment


class InspectFragment : Fragment(), OnMapReadyCallback {

    private var gMap: GoogleMap? = null
    private lateinit var binding: FragmentInspectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInspectBinding.inflate(inflater, container, false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // Inflate the layout for this fragment
        return binding.root

    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
    }
}