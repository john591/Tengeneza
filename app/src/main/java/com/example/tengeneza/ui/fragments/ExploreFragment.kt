package com.example.tengeneza.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tengeneza.adapters.PotholesAdapterClass
import com.example.tengeneza.databinding.FragmentExploreBinding
import com.example.tengeneza.models.PotholeData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class ExploreFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var potholesAdapter: PotholesAdapterClass
    private lateinit var dataList: ArrayList<PotholeData>
    lateinit var timestampList: Array<String>
    lateinit var potholeImageList: Array<String>
    lateinit var geoPointList:Array<String>
    lateinit var streetAddressList: Array<String>
    lateinit var cityList:Array<String>
    private lateinit var binding: FragmentExploreBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf<PotholeData>()
        potholesAdapter = PotholesAdapterClass(dataList)
        recyclerView.adapter = potholesAdapter
        // Set layout manager according to your requirement
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        getData()
        return binding.root
    }

    private fun getData(){
        val firestore = FirebaseFirestore.getInstance()
        val storageReference = FirebaseStorage.getInstance().reference
        val collectionReference = firestore.collection("johnkalume0@gmail.com")

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