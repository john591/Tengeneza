package com.example.tengeneza

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: Double) -> Unit

class ReportFragment : Fragment() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var tvlatitude: TextView
    private lateinit var tvLongitude: TextView

    private lateinit var binding: FragmentReportBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        getCurrentLocation();

        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false)

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){

        }
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        // Set up the listeners for take photo and video capture buttons
        binding.imageCaptureButton.setOnClickListener {
            takePhoto()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                    val location:Location?=task.result
                    if (location==null){
                        Toast.makeText(requireContext(), "Null", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Reussi", Toast.LENGTH_SHORT).show()
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val latitude = location?.latitude
                        val longitude = location?.longitude
                        val maxResult = 1

                        if (latitude != null) {
                            if (longitude != null) {
                                geocoder.getFromLocation(latitude, longitude, maxResult,
                                    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                                    object: Geocoder.GeocodeListener{
                                        override fun onGeocode(addresses: MutableList<Address>) {
                                            val address = addresses[0]
                                            val streetAddress = address.getAddressLine(0)
                                            val city = address.adminArea
                                            val countryCode = address.countryCode
                                            val countryName = address.countryName
                                            val postalCode = address.postalCode
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
                            }
                        }
                        /*binding.latitudeView.text = location?.latitude.toString()
                        binding.longitudeView.text= location?.longitude.toString()*/

                        //tvlatitude.text=""+location.latitude
                        //tvlatitude.text=""+location.longitude
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
        val locationManager:LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermissions():Boolean{
        if (ActivityCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Suppress("DEPRECATION")
    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== PERMISSION_REQUEST_ACCESS_LOCATION){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireContext(), "Accorder", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Non Accorder", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            //put(MediaStore.Images.Media.RELATIVE_PATH, "/Tengeneza/")
            put(MediaStore.Images.Media.RELATIVE_PATH, "/Tengeneza/")
        }

        // Create output options object which contains file + metadata
        val contentResolver = requireActivity().contentResolver
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build()
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

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Image capturée avec succès: ${output.savedUri}"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG,msg)
                }
            }
        )
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
        ContextCompat.checkSelfPermission(requireContext(),it) == PackageManager.PERMISSION_GRANTED
   }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
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
                Toast.makeText(requireContext(),
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
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