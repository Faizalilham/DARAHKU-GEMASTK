package com.example.donordarah.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.donordarah.adapter.LocationAdapter
import com.example.donordarah.databinding.ActivityCheckLocationBinding
import com.example.donordarah.model.DataDonor
import com.example.donordarah.util.Constant
import com.example.donordarah.viewModel.DonorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckLocationActivity : AppCompatActivity() {
    private var _binding : ActivityCheckLocationBinding? = null
    private val binding  get() = _binding!!
    private lateinit var locationAdapter : LocationAdapter
    private lateinit var donorViewModel : DonorViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCheckLocationBinding.inflate(layoutInflater)
        donorViewModel = ViewModelProvider(this)[DonorViewModel::class.java]
        setContentView(binding.root)
        setupView()
        getAllDataDonor()
    }

    private fun setupView(){
        binding.toolbar.setOnClickListener { finish() }
    }

    @SuppressLint("LogNotTimber")
    private fun getAllDataDonor(){
        showLoading(true)
        donorViewModel.doDonor()
        donorViewModel.getAllDonor().observe(this){
            if(it != null){
                showLoading(false)
                val data = it.filter { its -> its.available }
                setRecycler(data.toMutableList())
                Log.d("DATAS",it.toString())
            }else{
                Toast.makeText(this@CheckLocationActivity, Constant.ERROR, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRecycler(data : MutableList<DataDonor>){
        locationAdapter = LocationAdapter(object : LocationAdapter.Clicked{
            override fun onClicked(dataDonor: DataDonor) {
                startActivity(Intent(this@CheckLocationActivity,DetailActivity::class.java).also {
                    it.putExtra(Constant.ID,dataDonor.id)
                })
            }
        })
        locationAdapter.submitData(data)
        binding.rvLocation.apply {
            adapter = locationAdapter
            layoutManager = if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                GridLayoutManager(this@CheckLocationActivity,2)
            }else{
                LinearLayoutManager(this@CheckLocationActivity)
            }
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