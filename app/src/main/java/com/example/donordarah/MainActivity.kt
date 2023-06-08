package com.example.donordarah

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.donordarah.adapter.DonorAdapter
import com.example.donordarah.databinding.ActivityMainBinding
import com.example.donordarah.model.DataDonor
import com.example.donordarah.ui.*
import com.example.donordarah.util.Constant
import com.example.donordarah.viewModel.DonorViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var donorAdapter : DonorAdapter
    private lateinit var donorViewModel : DonorViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        donorViewModel = ViewModelProvider(this)[DonorViewModel::class.java]
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        setContentView(binding.root)
        getAllDataDonor()
        setupView()
        searchData()
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID,"id")
            param(FirebaseAnalytics.Param.ITEM_NAME, "name")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        }


    }

    private fun setupView(){
        binding.apply {
            bloodAvailableCard.setOnClickListener {
                startActivity(Intent(this@MainActivity,BloodAvailableActivity::class.java))
            }
            hospitalCard.setOnClickListener {
                startActivity(Intent(this@MainActivity, HospitalActivity::class.java))
            }

            mapsCard.setOnClickListener {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }
            checkcLocationCard.setOnClickListener {
                startActivity(Intent(this@MainActivity, CheckLocationActivity::class.java))
            }
        }
    }

    @SuppressLint("LogNotTimber")
    private fun getAllDataDonor(){
        showLoading(true)
        donorViewModel.doDonor()
        donorViewModel.getAllDonor().observe(this){
            if(it != null){
                showLoading(false)
                val data = it.sortedByDescending { its -> its.total }
                setRecycler(data.toMutableList())
                Log.d("DATAS",it.toString())
            }else{
                Toast.makeText(this@MainActivity, Constant.ERROR, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRecycler(data : MutableList<DataDonor>){
        donorAdapter = DonorAdapter(object : DonorAdapter.Clicked{
            override fun onClicked(dataDonor: DataDonor) {
                startActivity(Intent(this@MainActivity,DetailActivity::class.java).also {
                    it.putExtra(Constant.ID,dataDonor.id)
                })
            }
        })
        donorAdapter.submitData(data)
        binding.recyclerBloodBag.apply {
            adapter = donorAdapter
            layoutManager = if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                GridLayoutManager(this@MainActivity,2)
            }else{
                LinearLayoutManager(this@MainActivity)
            }
        }
    }

    private fun searchData(){
        binding.etSearch.setOnEditorActionListener { _, actionId,_  ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                val search = binding.etSearch.text.toString().trim()
                startActivity(Intent(this,SearchActivity::class.java).also {
                    it.putExtra(Constant.SEARCH,search)
                })
                true
            }else false
        }
    }

    private fun showLoading(isLoading : Boolean){
        if (isLoading) binding.progressBar.visibility = View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}