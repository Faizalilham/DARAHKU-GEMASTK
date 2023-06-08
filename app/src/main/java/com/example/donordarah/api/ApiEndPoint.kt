package com.example.donordarah.api

import com.example.donordarah.model.DataDonor
import com.example.donordarah.model.ListResponse
import com.example.donordarah.model.SingleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiEndPoint {

    @GET("donor")
    fun getAllDonor():Call<ListResponse<DataDonor>>

    @GET("donor/{id}")
    fun getDetailDonor(@Path("id")id :Int):Call<SingleResponse<DataDonor>>

    @GET("donor/search/{key}")
    fun searchAllDonor(@Path("key")key : String):Call<ListResponse<DataDonor>>
}