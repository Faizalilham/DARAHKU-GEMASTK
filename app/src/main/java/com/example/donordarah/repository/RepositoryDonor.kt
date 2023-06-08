package com.example.donordarah.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.donordarah.api.ApiEndPoint
import com.example.donordarah.model.DataDonor
import com.example.donordarah.model.ListResponse
import com.example.donordarah.model.SingleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class RepositoryDonor @Inject constructor(private val api : ApiEndPoint) {

    private val listData : MutableLiveData<MutableList<DataDonor>?> = MutableLiveData()
    val listDatas : LiveData<MutableList<DataDonor>?> = listData

    private val detailData :  MutableLiveData<DataDonor?> = MutableLiveData()
    val detailDatas : LiveData<DataDonor?> = detailData

    private val listDataSearch :  MutableLiveData<MutableList<DataDonor>?> = MutableLiveData()
    val listDatasSearch : LiveData<MutableList<DataDonor>?> = listDataSearch


    fun getData(){
        api.getAllDonor().enqueue(object : Callback<ListResponse<DataDonor>>{
            @SuppressLint("LogNotTimber")
            override fun onResponse(
                call: Call<ListResponse<DataDonor>>,
                response: Response<ListResponse<DataDonor>>
            ) {
                if(response.isSuccessful){
                    val body = response.body()
                    if(body != null){
                        listData.postValue(body.datas)
                    }else{
                        listData.postValue(null)
                        Log.d("NULL","Body Null")
                    }
                }else{
                    listData.postValue(null)
                    Log.d("NotSuccess","Body Null")
                }
            }

            @SuppressLint("LogNotTimber")
            override fun onFailure(call: Call<ListResponse<DataDonor>>, t: Throwable) {
                listData.postValue(null)
                Log.d("OnFailure","Body Null")
            }

        })
    }

    fun getDetailData(id : Int){
        api.getDetailDonor(id).enqueue(object : Callback<SingleResponse<DataDonor>>{
            @SuppressLint("LogNotTimber")
            override fun onResponse(call: Call<SingleResponse<DataDonor>>, response: Response<SingleResponse<DataDonor>>) {
                if(response.isSuccessful){
                    val body = response.body()
                    if(body != null){
                        detailData.postValue(body.datas)
                    }else{
                        detailData.postValue(null)
                        Log.d("NULL","Body Null")
                    }
                }else{
                    detailData.postValue(null)
                    Log.d("NotSuccess","Body Null")
                }
            }

            @SuppressLint("LogNotTimber")
            override fun onFailure(call: Call<SingleResponse<DataDonor>>, t: Throwable) {
                detailData.postValue(null)
                Log.d("OnFailure","Body Null")
            }

        })
    }

    fun searchAllData(key : String){
        api.searchAllDonor(key).enqueue(object : Callback<ListResponse<DataDonor>>{
            @SuppressLint("LogNotTimber")
            override fun onResponse(
                call: Call<ListResponse<DataDonor>>,
                response: Response<ListResponse<DataDonor>>
            ) {

                if(response.isSuccessful){
                    val body = response.body()
                    if(body != null){
                        listDataSearch.postValue(body.datas)
                    }else{
                        listDataSearch.postValue(null)
                        Log.d("NULL","Body Null")
                    }
                }else{
                    listDataSearch.postValue(null)
                    Log.d("NotSuccess",response.message())
                }
            }

            @SuppressLint("LogNotTimber")
            override fun onFailure(call: Call<ListResponse<DataDonor>>, t: Throwable) {
                listDataSearch.postValue(null)
                Log.d("OnFailure","Body Null")
            }

        })
    }



}