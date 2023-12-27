package com.example.tengeneza.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tengeneza.adapters.PotholeItemClickListener
import com.example.tengeneza.adapters.PotholesAdapterClass
import com.example.tengeneza.databinding.FragmentExploreBinding
import com.example.tengeneza.models.PotholeData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class ExploreFragment : Fragment(), PotholeItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var potholesAdapter: PotholesAdapterClass
    private lateinit var dataList: ArrayList<PotholeData>
    lateinit var timestampList: Array<String>
    lateinit var potholeImageList: Array<String>
    lateinit var geoPointList:Array<String>
    lateinit var streetAddressList: Array<String>
    lateinit var cityList:Array<String>
    private lateinit var binding: FragmentExploreBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 123

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf<PotholeData>()
        potholesAdapter = PotholesAdapterClass(dataList, this)
        recyclerView.adapter = potholesAdapter
        // Set layout manager according to your requirement
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )

        getData()
        return binding.root
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can now use the location API
                    // Call the openMapWithGeoPoint function here
                } else {
                    // Permission denied, handle accordingly
                }
            }
            // Handle other permission requests if needed
            // ...
        }
    }

    override fun onOpenMapClicked(geoPoint: String) {
        openMapWithGeoPoint(geoPoint)
    }
    private fun openMapWithGeoPoint(geoPoint: String) {
        // Parse the geoPoint to extract latitude and longitude
        val destinationGeoPointArray = geoPoint.split(",")
        val destinationLatitude = destinationGeoPointArray[0].toDouble()
        val destinationLongitude = destinationGeoPointArray[1].toDouble()

        // Get the user's current location
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                val currentLatitude = location.latitude
                val currentLongitude = location.longitude

                // Create a Uri with the current location and destination GeoPoint data
                val mapUri: Uri = Uri.parse("geo:$currentLatitude,$currentLongitude?q=$destinationLatitude,$destinationLongitude")

                // Create an Intent to open the map app
                val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                mapIntent.setPackage("com.google.android.apps.maps") // Specify the map app package

                // Check if there is a map app available to handle the intent
                if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Log.e("ExploreFragment", "No map app is installed")
                    // Handle the case where no map app is installed
                    // You can show a message or prompt the user to install a map app
                }
            } else {
                Log.e("ExploreFragment", "Unable to get current location")
            }
        } else {
            // Handle the case where location permission is not granted
            Log.e("ExploreFragment", "Location permission not granted")
        }
    }


    private fun getData(){
        val firestore = FirebaseFirestore.getInstance()
        val storageReference = FirebaseStorage.getInstance().reference
        val collectionReference = firestore.collection("potholesDataCollection")

        collectionReference.addSnapshotListener{ snapshot, exception ->
            if (exception != null){
                return@addSnapshotListener
            }
            if (snapshot != null){
                // Clear existing data before adding new data
                dataList.clear()

                // Initialize imagesToDownload
                var imagesToDownload = snapshot.documents.size

                for (potholeSnapshot in snapshot.documents) {
                    val pothole = potholeSnapshot.toObject(PotholeData::class.java)
                    if (pothole != null){
                        pothole.id = potholeSnapshot.id

                        // Access latitude and longitude from GeoPoint
                        val lat = pothole.geoPoint?.latitude
                        val lon = pothole.geoPoint?.longitude
                        val latitude: String = lat.toString()
                        val longitude: String = lon.toString()

                        val displayPotholeData = PotholeData(
                            pothole.id,
                            pothole.currentUser,
                            pothole.name,
                            pothole.countryCode,
                            pothole.countryName,
                            pothole.postalCode,
                            pothole.city,
                            pothole.streetAddress,
                            pothole.currentDateTimeString,
                            pothole.geoPoint,
                            pothole.potholeImage
                        )
                        storageReference.child("potholesImages/${pothole.potholeImage}")
                            .downloadUrl
                            .addOnSuccessListener { uri ->
                                // Update the data list with the image URL
                                displayPotholeData.potholeImage = uri.toString()
                                // Add data to the temporary list
                                dataList.add(displayPotholeData)
                                // Check if all images are downloaded
                                if (--imagesToDownload == 0) {
                                    // Notify the adapter that the data set has changed
                                    potholesAdapter.notifyDataSetChanged()
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Handle failures in downloading image URL
                                if (--imagesToDownload == 0) {
                                    // Add data to the main list even if image download fails
                                    potholesAdapter.notifyDataSetChanged()
                                }
                            }

                        //dataList.add(displayPotholeData)
                    }
                }

            }

        }
    }

}