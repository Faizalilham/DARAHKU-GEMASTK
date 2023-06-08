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
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.models.SlideModel
import com.example.donordarah.databinding.ActivityDetailBinding
import com.example.donordarah.util.Constant
import com.example.donordarah.viewModel.DonorViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var donorViewModel : DonorViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latitude = ""
    private var longitude = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        donorViewModel = ViewModelProvider(this)[DonorViewModel::class.java]
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)
        val id = intent.getIntExtra(Constant.ID,0)
        getDetailData(id)
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if(checkingPermission()){
            if(isLocationEnable()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){
                    val location = it.result
                   if(location != null){
                       latitude = location.latitude.toString()
                       longitude = location.longitude.toString()
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
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ACCESS)

    }

    private fun isLocationEnable(): Boolean {
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

       return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkingPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
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

    @SuppressLint("LogNotTimber")
    private fun getDetailData(id : Int){
        showLoading(true)
        donorViewModel.doDetailDonor(id)
        donorViewModel.getDetailDonor().observe(this){
            if(it != null){
                showLoading(false)
                setData(it.nama,it.deskripsi,it.gambar)
                showMap(it.latitude,it.longitude)
                Log.d("DATAXX",it.toString())
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun showMap(lat : Double,lon : Double){
        binding.webMap.apply {
            settings.apply {
                setSupportZoom(true)
                domStorageEnabled = true
                javaScriptEnabled = true
            }
            webViewClient = WebViewClient()
            loadUrl("http://maps.google.com/maps?saddr=$latitude,$longitude&daddr=$lat,$lon&z=35");

        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setData(name : String, deskripsi :String, image : MutableList<String>){
        val listData = mutableListOf<SlideModel>()
        for(i in image){
            listData.add(SlideModel(i))
        }
        binding.apply {
            val cal = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
            val date = dateFormat.format(cal.time)
            tvName.text = name
            tvDeskripsi.text = deskripsi
            imageSlider.setImageList(listData)
            tvDate.text = date

        }
    }
    private fun showLoading(isLoading : Boolean){
        if (isLoading) binding.progressBar.visibility = View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        private const val PERMISSION_ACCESS = 101
    }
}