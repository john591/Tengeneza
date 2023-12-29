package com.example.tengeneza.ui.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tengeneza.databinding.FragmentReportBinding
import com.example.tengeneza.models.TengenezaData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit
class ReportFragment : Fragment() {
    // Initialize Firebase Firestore reference
    private val dB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    // Use a constant to identify the camera permission request
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    // Get the current Firebase user
    //private val currentUser = FirebaseAuth.getInstance().currentUser

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var binding: FragmentReportBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        // Check location permissions
        if (checkLocationPermissions()) {

            getCurrentLocation()
        } else {
            requestLocationPermissions()
        }

        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false)

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){

        }
        startCamera()
        // Request camera permissions
        /*if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }*/
        initVars()
        registerClickEvents()

        //binding.currentuserIdView.text = currentUser?.uid
        //binding.currentuserEmailView.text = currentUser?.email

        // Set up the listeners for take photo and video capture buttons
        binding.imageCaptureButton.setOnClickListener {
            takePhoto()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()


        return binding.root
    }
    private fun initVars(){
        storageReference = FirebaseStorage.getInstance().reference.child("potholesImages")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }
    private fun registerClickEvents(){
        binding.imageCaptureButton.setOnClickListener{
            takePhoto()
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun getCurrentLocation(){
        if(checkPermissions()){
            if (isLocationEnabled()){
                //final latitude and longitude here
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity()) { task->
                    val location: Location?=task.result
                    if (location==null){
                        Toast.makeText(requireContext(), "Non localiser", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "L'endroit localiser'", Toast.LENGTH_SHORT)
                            .show()
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val maxResult = 1

                        geocoder.getFromLocation(latitude, longitude, maxResult,
                            object: Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: MutableList<Address>) {
                                    val address = addresses[0]
                                    val streetAddress = address.getAddressLine(0)
                                    val city = address.adminArea
                                    val countryCode = address.countryCode
                                    val countryName = address.countryName
                                    val postalCode = address.postalCode

                                    /*val sharedPreferences = requireContext().getSharedPreferences("LocationPotholesData", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    // Save data
                                    editor.putString("streetAdress", streetAddress)
                                    editor.putString("city", city)
                                    editor.putString("countryName", countryName)
                                    editor.putFloat("latitude", latitude.toFloat())
                                    editor.putFloat("longitude", longitude.toFloat())
                                    editor.putString("countryCode", countryCode)
                                    editor.putString("postalCode", postalCode)
                                    // Apply changes
                                    editor.apply()*/

                                    /*binding.countryCodeView.text= countryCode
                                    binding.postalCodeView.text= postalCode
                                    binding.countryNameView.text= countryName
                                    binding.cityView.text= city
                                    binding.streetView.text= streetAddress*/
                                }

                                override fun onError(errorMessage: String?) {
                                    super.onError(errorMessage)
                                }
                            })
                        /*binding.latitudeView.text = latitude.toString()
                        binding.longitudeView.text= longitude.toString()*/
                    }

                }

            }else{
                //setting open here
                Toast.makeText(requireContext(), "Accordez l'access au GPS", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            //request permission here
            requestPermissions()
        }
    }

    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception){
            e.printStackTrace()
        }
        return false
        /*return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )*/
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermissions():Boolean{
        return (ActivityCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Accordé", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Non accordé", Toast.LENGTH_SHORT).show()
            }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted
            Toast.makeText(requireContext(), "Accordé", Toast.LENGTH_SHORT).show()
            getCurrentLocation()
        } else {
            // Permission not granted, request it
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Check if the camera permission is granted
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request camera permission
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    @Suppress("DEPRECATION")
    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== PERMISSION_REQUEST_ACCESS_LOCATION){
            if (grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireContext(), "Accorder", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Non Accorder", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // Check if the user granted the camera permission
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with camera-related operations
                startCamera()
            } else {
                // Permission denied, handle accordingly (e.g., show a message or take alternative actions)
            }
        }
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
    fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss") // Define the format you want

        return currentDateTime.format(formatter)
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        // Initialize Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance()
        storageRef = firebaseStorage.reference

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val timestamp = getCurrentTimestamp()
        val currentDateTimeString = getCurrentDateTime()

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            //put(MediaStore.Images.Media.RELATIVE_PATH, "/Tengeneza/")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Tengeneza")
        }

        // Create output options object which contains file + metadata
        val contentResolver = requireActivity().contentResolver
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()
            //.Builder( contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Image capturée avec succès: ${output.savedUri}"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    val imageUri = output.savedUri

                    // Upload the captured image to Firebase Storage
                    if (imageUri != null) {
                        uploadImage(imageUri)
                    } else {
                        Log.e(TAG, "Saved image URI is null.")
                        // Handle the case where the saved image URI is null
                    }

                    val sharedPreferences = requireContext().getSharedPreferences("LocationPotholesData", Context.MODE_PRIVATE)
                    val PotholeImageData = requireContext().getSharedPreferences("PotholeImageData", Context.MODE_PRIVATE)
                    // Retrieve data (provide default values if not found)
                    val countryName = sharedPreferences.getString("countryName", "DefaultCountry")
                    val latitude = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
                    val longitude = sharedPreferences.getFloat("longitude", 0.0f).toDouble()
                    val streetAddress = sharedPreferences.getString("streetAdress","DefaultStreet")
                    val city = sharedPreferences.getString("city","DefaultCity")
                    val countryCode = sharedPreferences.getString("countryCode","CD")
                    val postalCode = sharedPreferences.getString("postalCode","243")
                    val geoPoint = GeoPoint(latitude,longitude)
                    val potholeImage = PotholeImageData.getString("potholeImage","jpg")

                    //val user = currentUser?.uid
                    val locationPothole = TengenezaData(currentDateTimeString,potholeImage.toString(), name, geoPoint, streetAddress.toString(),city.toString(), countryCode.toString(), countryName.toString(), postalCode.toString())
                    val collectionReference = dB.collection("potholesDataCollection") // Here I have to change the collection. "potholesReports"
                    // Add the data to Firestore
                    collectionReference.add(locationPothole)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Merci de nous avoir signaler", Toast.LENGTH_SHORT)
                                .show()
                            // Create an editor to modify the SharedPreferences
                            val editor = sharedPreferences.edit()
                            val editor2 = PotholeImageData.edit()

                            // Remove the value associated with the "countryName" key
                            editor.remove("latitude")
                            editor.remove("longitude")
                            editor.remove("streetAdress")
                            editor.remove("city")
                            editor.remove("countryCode")
                            editor.remove("countryName")
                            editor.remove("postalCode")
                            editor2.remove("potholeImage")

                            // Apply the changes
                            editor.apply()
                            editor2.apply()

                            val intent = Intent(requireActivity(), HomeActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Erreur au moment d'envoie", Toast.LENGTH_SHORT)
                                .show()
                        }

                }
            }
        )
    }
    private fun uploadImage(imageUri: Uri) {
        // Create a unique filename for the image
        val filename = "${UUID.randomUUID()}.jpg"
        //val name = "${UUID.randomUUID()}.jpg"

        val sharedPreferencesImage = requireContext().getSharedPreferences("PotholeImageData", Context.MODE_PRIVATE)
        val editor = sharedPreferencesImage.edit()
        // Save data
        editor.putString("potholeImage", filename)
        // Apply changes
        editor.apply()

        // Reference to the image in Firebase Storage
        val storageRef: StorageReference = storageRef.child("potholesImages/$filename")

        // Upload the image
        val uploadTask: UploadTask = storageRef.putFile(imageUri)

        // Listen for the success or failure of the upload
        uploadTask.addOnCompleteListener {
            if (uploadTask.isSuccessful) {
                // Image uploaded successfully
                println("Image uploaded successfully. Path: ${storageRef.path}")
            } else {
                // Handle failure
                println("Failed to upload image.")
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

           // val recorder = Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HIGHEST))
             //   .build()

            imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                //cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture);

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

   private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
   }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
        private const val TAG = "ReportFragment"
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss" //yyyy-MM-dd-HH-mm-ss-SSS
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                //Manifest.permission.RECORD_AUDIO
            ).apply {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            }.toTypedArray()
    }

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    requireContext(),
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startCamera()
            }
    }

    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }

}