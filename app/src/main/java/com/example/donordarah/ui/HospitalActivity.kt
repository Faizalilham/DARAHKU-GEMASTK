package com.example.donordarah.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.donordarah.databinding.ActivityHospitalBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HospitalActivity : AppCompatActivity() {
    private var _binding : ActivityHospitalBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHospitalBinding.inflate(layoutInflater)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)
        getCurrentLocation()

    }

    private fun getCurrentLocation() {
        if(checkingPermission()){
            if(isLocationEnable()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){
                    val location = it.result
                    if(location != null){
                        showNearbyHospital(location.latitude.toString(),location.longitude.toString())

                    }else{
                        Toast.makeText(this, "location Null", Toast.LENGTH_SHORT).show()
                    }

                }
            }else{
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            checkrequestPermissions()
        }
    }

    private fun checkrequestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ACCESS
        )

    }

    private fun isLocationEnable(): Boolean {
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkingPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ACCESS){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }else{
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showNearbyHospital(lat : String, lon :String){
        binding.webView.apply {
            settings.apply {
                setSupportZoom(true)
                domStorageEnabled = true
                javaScriptEnabled = true
            }
            webViewClient = WebViewClient()
            loadUrl("https://maps.google.com?ll=$lat,$lon&q=rsud&z=14")
            Log.d("MAPS","https://maps.google.com?ll=$lat,$lon&q=rsud&z=14")
//            loadUrl("https://www.google.com/maps/search/?api=1&query=rsud")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    companion object{
        private const val PERMISSION_ACCESS = 101
    }
}