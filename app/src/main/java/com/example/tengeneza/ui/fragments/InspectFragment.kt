package com.example.tengeneza.ui.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tengeneza.R
import com.example.tengeneza.databinding.FragmentInspectBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.util.Locale


class InspectFragment : Fragment(), OnMapReadyCallback {

    private var gMap: GoogleMap? = null
    private lateinit var geocoder: Geocoder
    private val markers: MutableList<Marker> = mutableListOf()
    // Define your default zoom level here
    private val DEFAULT_ZOOM_LEVEL = 10.0F
    private lateinit var binding: FragmentInspectBinding
    // Reference to the Firestore database
    private val db = FirebaseFirestore.getInstance()
    // Reference to the collection and document you want to retrieve data from
    private val collectionReference = db.collection("johnkalume0@gmail.com") // Replace with your collection name
    // Query the collection (optional, you can also get all documents in the collection)
    // For example, getting documents where a specific field equals a certain value
    // val query = collectionReference.whereEqualTo("fieldName", "fieldValue")


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
        this.gMap = googleMap
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        collectionReference.get().addOnSuccessListener { querySnapshot ->
            var lastLatLng: LatLng? = null

            for (document in querySnapshot){
                // Access the data of each document
                val data = document.data
                // Access the fields using keys
                //val docID = document.getString("documentId")
                val geoPoint = document.getGeoPoint("geoPoint")
                //val name = document.getString("name")
                //val potholeImage = data["potholeImage"]
               /* val currentUser = document.getString("currentUser")
                val postalCode = document.getString("postalCode")
                val countryCode = document.getString("countryCode")
                val countryName = document.getString("countryName")*/
                val city = document.getString("city")
               /* val streetAddress = document.getString("streetAddress")
                val timestamp = document.getTimestamp("timestamp")*/
                // Add other fields as needed
                // Use the retrieved data as needed
                if (geoPoint != null && city != null){
                    // Call a function to handle the GeoPoint data
                    handleGeoPoint(geoPoint.latitude, geoPoint.longitude, city)

                    //Store the last LatLng for setting camera position
                    lastLatLng = LatLng(geoPoint.latitude, geoPoint.longitude)
                }
            }
            //Set  the camera position to focus on the last LatLng (if available)
            lastLatLng?.let{
                val cameraPosition = CameraPosition.Builder()
                    .target(it)
                    .zoom(DEFAULT_ZOOM_LEVEL)
                    .build()
                gMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
            .addOnFailureListener { e ->
                // Handle failure
                println("Error getting documents: $e")
            }
        // Enable my location button
        gMap?.uiSettings?.isMyLocationButtonEnabled = true

        // Enable zoom controls
        gMap!!.uiSettings.isZoomControlsEnabled = true

        // Enable compass
        gMap!!.uiSettings.isCompassEnabled = true

        // Enable my location button
        gMap!!.uiSettings.isMyLocationButtonEnabled = true

        // Set map type to hybrid: MAP_TYPE_NORMAL,MAP_TYPE_NONE, MAP_TYPE_SATELLITE, MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID
        gMap!!.mapType = GoogleMap. MAP_TYPE_NORMAL


    }

    private fun handleGeoPoint(latitude: Double, longitude: Double, city: String) {
        // Perform reverse geocoding to get an address from the LatLng
        val address = getAddressFromLatLng(LatLng(latitude,longitude))
        // Log the address
        Log.d("GeoPoint", "CityName: $city, Address: $address, Latitude: $latitude, Longitude: $longitude")

        // Or add a marker to the map
        val location = LatLng(latitude, longitude)
        val markerTitle = "$city - $address"
        val marker = gMap?.addMarker(MarkerOptions().position(location).title(markerTitle))

        // Add the marker to the list
        marker?.let{ markers.add(it) }

        // Move the camera to the GeoPoint location
        gMap?.moveCamera(CameraUpdateFactory.newLatLng(location))
    }

    private fun getAddressFromLatLng(latLng: LatLng): String {
        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val addressStringBuilder = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    addressStringBuilder.append(address.getAddressLine(i))
                    if (i < address.maxAddressLineIndex) {
                        addressStringBuilder.append(", ")
                    }
                }
                return addressStringBuilder.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Address not found"
    }

}