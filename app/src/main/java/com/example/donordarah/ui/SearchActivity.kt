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
import com.example.donordarah.adapter.DonorAdapter
import com.example.donordarah.databinding.ActivitySearchBinding
import com.example.donordarah.model.DataDonor
import com.example.donordarah.util.Constant
import com.example.donordarah.viewModel.DonorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private var _binding : ActivitySearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var donorAdapter : DonorAdapter
    private lateinit var donorViewModel : DonorViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        donorViewModel = ViewModelProvider(this)[DonorViewModel::class.java]
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        val key = intent.getStringExtra(Constant.SEARCH)
        if(key != null){
            getAllDataDonor(key)
            binding.tvTittle.text = key
        }
    }

    @SuppressLint("LogNotTimber")
    private fun getAllDataDonor(key : String){
        showLoading(true)
        donorViewModel.doSearchDonor(key)
        donorViewModel.getAllSearchDonor().observe(this){
            if(it != null){
                showLoading(false)
                setRecycler(it)
                Log.d("DATAS",it.toString())
            }else{
                Toast.makeText(this, Constant.ERROR, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRecycler(data : MutableList<DataDonor>){
        donorAdapter = DonorAdapter(object : DonorAdapter.Clicked{
            override fun onClicked(dataDonor: DataDonor) {
                startActivity(Intent(this@SearchActivity,DetailActivity::class.java).also {
                    it.putExtra(Constant.ID,dataDonor.id)
                })
            }
        })
        donorAdapter.submitData(data)
        binding.rvSearch.apply {
            adapter = donorAdapter
            layoutManager = if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                GridLayoutManager(this@SearchActivity,2)
            }else{
                LinearLayoutManager(this@SearchActivity)
            }
        }
    }

    private fun showLoading(isLoading : Boolean){
        if (isLoading) binding.progressBar.visibility = View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    private fun setupView(){
        binding.toolbar.setOnClickListener { finish() }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}