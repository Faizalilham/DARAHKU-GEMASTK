package com.example.donordarah.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.donordarah.R
import com.example.donordarah.adapter.DonorAdapter
import com.example.donordarah.databinding.ActivityBloodAvailableBinding
import com.example.donordarah.model.DataDonor
import com.example.donordarah.util.Constant
import com.example.donordarah.viewModel.DonorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BloodAvailableActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private var _binding : ActivityBloodAvailableBinding? = null
    private val binding get() = _binding!!
    private var resultDropDown = ""
    private lateinit var donorAdapter : DonorAdapter
    private lateinit var donorViewModel : DonorViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBloodAvailableBinding.inflate(layoutInflater)
        donorViewModel = ViewModelProvider(this)[DonorViewModel::class.java]
        setContentView(binding.root)
        dropDownMenu()

    }

    private fun dropDownMenu(){
        val data = resources.getStringArray(R.array.list_passing)
        val adapter = ArrayAdapter(this,R.layout.dropdown_item,data)
        with(binding.etBloodDropDown){
            setAdapter(adapter)
            onItemClickListener = this@BloodAvailableActivity
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position).toString()
        resultDropDown = item
        getAllDataDonor(resultDropDown)
    }

    @SuppressLint("LogNotTimber")
    private fun getAllDataDonor(key :String){
        donorViewModel.doSearchDonor(key)
        donorViewModel.getAllSearchDonor().observe(this){
            if(it != null){
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
                startActivity(Intent(this@BloodAvailableActivity,DetailActivity::class.java).also {
                    it.putExtra(Constant.ID,dataDonor.id)
                })
            }
        })
        donorAdapter.submitData(data)
        binding.rvBloods.apply {
            adapter = donorAdapter
            layoutManager = if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                GridLayoutManager(this@BloodAvailableActivity,2)
            }else{
                LinearLayoutManager(this@BloodAvailableActivity)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}