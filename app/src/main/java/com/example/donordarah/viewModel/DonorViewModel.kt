package com.example.donordarah.viewModel

import androidx.lifecycle.ViewModel
import com.example.donordarah.repository.RepositoryDonor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DonorViewModel @Inject constructor(private val repositoryDonor: RepositoryDonor) : ViewModel() {

    // GET ALL DATA
    fun doDonor() = repositoryDonor.getData()
    fun getAllDonor() = repositoryDonor.listDatas

    // GET DATA BY ID

    fun doDetailDonor(id :Int) = repositoryDonor.getDetailData(id)
    fun getDetailDonor() = repositoryDonor.detailDatas


    // SEARCH DATA

    fun doSearchDonor(key : String) = repositoryDonor.searchAllData(key)
    fun getAllSearchDonor() = repositoryDonor.listDatasSearch


}