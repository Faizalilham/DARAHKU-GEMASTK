package com.example.donordarah.model

data class ListResponse<T>(
    val msg : String,
    val status : Int,
    val datas : MutableList<T>
)
data class SingleResponse<T>(
    val msg : String,
    val status : Int,
    val datas : T
)


data class DataDonor(
    val id : Int,
    val nama : String,
    val deskripsi : String,
    val latitude : Double,
    val longitude : Double,
    val gambar :MutableList<String>,
    val golongan : MutableList<Golongan>,
    val available : Boolean,
    val total : Int
)

data class Golongan(
    val type : String,
    val stok :Int
)

