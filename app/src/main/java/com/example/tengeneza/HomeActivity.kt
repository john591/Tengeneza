package com.example.tengeneza

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.tengeneza.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView

// It is like a MainActivity, it will be using with others fragments

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var navController: NavController
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasCameraPermission()){
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 0
            )
        }
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
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
            }
            true
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