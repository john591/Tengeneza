package com.example.tengeneza.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.tengeneza.R
import com.example.tengeneza.databinding.ActivityHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import java.util.Locale

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityHomeBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        // Check location permissions
        if (checkLocationPermissions()) {

            getCurrentLocation()
        } else {
            requestLocationPermissions()
        }
        if (!hasCameraPermission()){
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 0
            )
        }
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.bottom_inspect -> openFragment(InspectFragment())
                R.id.bottom_explore -> openFragment(ExploreFragment())
                R.id.bottom_report -> openFragment(ReportFragment())
                R.id.bottom_profile -> openFragment(SettingsFragment())
                R.id.button_signaler -> openFragment(ReportFragment())
            }
            true
        }

    }

    private fun checkLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            ReportFragment.PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun getCurrentLocation(){
        if(checkPermissions()){
            if (isLocationEnabled()){
                //final latitude and longitude here
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task->
                    val location: Location?=task.result
                    if (location==null){
                        Toast.makeText(this, "Non localiser", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "L'endroit localiser'", Toast.LENGTH_SHORT)
                            .show()
                        val geocoder = Geocoder(this, Locale.getDefault())
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

                                    val sharedPreferences = getSharedPreferences("LocationPotholesData", Context.MODE_PRIVATE)
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
                                    editor.apply()
                                }

                                override fun onError(errorMessage: String?) {
                                    super.onError(errorMessage)
                                }
                            })
                    }

                }

            }else{
                //setting open here
                Toast.makeText(this, "Accordez l'access au GPS", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            //request permission here
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            HomeActivity.PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    companion object {
        const val PERMISSION_REQUEST_ACCESS_LOCATION = 123
    }

    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            HomeActivity.PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermissions():Boolean{
        return (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Accordé", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Non accordé", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_analytics -> openFragment(AnalyticsFragment())
            R.id.nav_buchafu -> openFragment(ExploreFragment())
            R.id.nav_setting -> openFragment(SettingsFragment())
            R.id.nav_faq -> openFragment(FaqFragment())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.navHostHomeActivityFragmentContainerView,fragment)
        fragmentTransaction.addToBackStack(null) // Optional
        fragmentTransaction.commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.navHostHomeActivityFragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED


}