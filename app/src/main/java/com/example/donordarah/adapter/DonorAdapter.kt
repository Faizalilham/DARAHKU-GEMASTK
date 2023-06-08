package com.example.donordarah.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.donordarah.R
import com.example.donordarah.databinding.ListItemBinding
import com.example.donordarah.model.DataDonor
import com.squareup.picasso.Picasso

class DonorAdapter(private val listener : Clicked):RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {

    private val differ = object : DiffUtil.ItemCallback<DataDonor>(){
        override fun areItemsTheSame(oldItem: DataDonor, newItem: DataDonor): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataDonor, newItem: DataDonor): Boolean {
            return when{
                oldItem.id != newItem.id -> false
                oldItem.nama != newItem.nama -> false
                oldItem.deskripsi != newItem.deskripsi -> false
                oldItem.latitude != newItem.latitude -> false
                oldItem.longitude != newItem.longitude -> false
                oldItem.golongan != newItem.golongan -> false
                oldItem.total != newItem.total -> false
                else -> true
            }
        }
    }

    private val diffUtil = AsyncListDiffer(this,differ)

    fun submitData(data : MutableList<DataDonor>) = diffUtil.submitList(data)

    inner class DonorViewHolder(val binding : ListItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
       return DonorViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
       holder.binding.apply {
           val(_,nama,_,_,_,gambar,_,_,total) = diffUtil.currentList[position]
           tvTittle.text =  nama
           tvSubTittle.text = total.toString()
           Glide.with(root.context).load(gambar[0]).into(imageBloodBag)
           card.setOnClickListener {
               listener.onClicked(diffUtil.currentList[position])
           }
           card.startAnimation(AnimationUtils.loadAnimation(root.context, R.anim.anim))
       }
    }

    override fun getItemCount(): Int = diffUtil.currentList.size

    interface Clicked{
        fun onClicked(dataDonor: DataDonor)
    }

}